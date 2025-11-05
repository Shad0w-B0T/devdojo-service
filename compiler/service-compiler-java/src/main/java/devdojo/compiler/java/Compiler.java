package devdojo.compiler.java;

import devdojo.compiler.ICompiler;
import devdojo.schema.code.TestError;
import devdojo.schema.code.TestRunResult;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import javax.tools.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Compiler implements ICompiler {

  private static final long TIMEOUT_MS = 10_000;

  /**
   * Dynamically compiles and executes a Java static method from source code with provided arguments.
   * <p>
   * This method:
   * <ul>
   *     <li>Generates a temporary Java class source file containing the provided method</li>
   *     <li>Compiles the class at runtime using the system Java compiler</li>
   *     <li>Measures compilation time</li>
   *     <li>Loads the compiled class and invokes the method for each argument set</li>
   *     <li>Captures output or any runtime/compilation errors per invocation</li>
   *     <li>Measures runtime for each invocation</li>
   * </ul>
   *
   * @param methodCode the Java method code as a string (must include full method signature and body)
   * @param arguments  a 2D array of argument sets to be passed to the compiled method
   * @return a list of {@link TestRunResult}, each containing input arguments, output, timing or errors
   * @throws Exception if class loading or file access fails (not related to method execution itself)
   */
  public List<TestRunResult> compileAndRun(final String methodCode,
                                           final Object[][] arguments) throws Exception {
    log.debug("Compile task requested.");
    final String className = "DynamicCode";
    final String methodName = extractMethodName(methodCode);

    log.info("Compiling method {} and methods.", methodName);

    final File sourceFile = writeSourceToFile(className, methodCode);

    final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    final long compileStart = System.currentTimeMillis();
    final boolean success = compileSource(sourceFile, diagnostics);
    final long compileTime = System.currentTimeMillis() - compileStart;

    if (!success) {
      sourceFile.delete();
      log.warn("Compilation failed.");
      return compilerError(diagnostics, compileTime);
    }

    final Class<?> dynamicClass = loadCompiledClass(className);

    log.info("Compiled dynamic class {}, running tests.", dynamicClass.getSimpleName());

    final List<TestRunResult> results =
      invokeMethod(dynamicClass, methodName, arguments, compileTime);

    sourceFile.delete();
    new File(className + ".class").delete();

    return results;
  }

  /**
   * Writes the Java source code to a temporary .java file.
   *
   * @param className  the name of the class to generate
   * @param methodCode the Java method code
   * @return the created source file
   * @throws IOException if writing the file fails
   */
  private static File writeSourceToFile(final String className,
                                        final String methodCode) throws IOException {
    final String source = """
      import java.util.*;
      import java.lang.*;
      import java.io.*;
      
      public class %s {
      %s
      }
      """.formatted(className, methodCode);
    final File sourceFile = new File(className + ".java");
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
      writer.write(source);
    }
    return sourceFile;
  }

  /**
   * Compiles the Java source file using the system compiler.
   *
   * @param sourceFile  the Java source file to compile
   * @param diagnostics the diagnostic collector to capture compilation messages
   * @return true if compilation succeeded, false otherwise
   * @throws IOException if file manager operations fail
   */
  private static boolean compileSource(final File sourceFile,
                                       final DiagnosticCollector<JavaFileObject> diagnostics) throws IOException {
    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    final StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
    final Iterable<? extends JavaFileObject> compilationUnits =
      fileManager.getJavaFileObjectsFromFiles(List.of(sourceFile));
    final JavaCompiler.CompilationTask task =
      compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
    final boolean success = task.call();
    fileManager.close();
    return success;
  }

  /**
   * Loads the compiled class using a URLClassLoader.
   *
   * @param className the name of the compiled class
   * @return the loaded Class object
   * @throws Exception if class loading fails
   */
  private static Class<?> loadCompiledClass(final String className) throws Exception {
    final URLClassLoader classLoader = URLClassLoader.newInstance(
      new URL[] {new File(".").toURI().toURL()});
    return Class.forName(className, true, classLoader);
  }


  private static List<TestRunResult> invokeMethod(final Class<?> clazz,
                                                  final String methodName,
                                                  final Object[][] arguments,
                                                  final long compileTime) {
    final List<TestRunResult> results = new ArrayList<>();

    final Method method = Arrays.stream(clazz.getDeclaredMethods())
      .filter(m -> m.getName().equals(methodName))
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Method not found: " + methodName));

    final Class<?>[] paramTypes = method.getParameterTypes();

    for (final Object[] args : arguments) {
      Object[] convertedArgs = convertArguments(args, paramTypes);

      log.info("Invoking {} with arguments {}", methodName, Arrays.toString(args));

      try {
        results.add(invokeWithTimeout(method, convertedArgs, compileTime, args));
      } catch (final TimeoutException exception) {
        results.add(new TestRunResult(null, null, Collections.emptyList(),
          Collections.singletonList(
            new TestError("TimeoutException", "One method execution exceeded %s ms.".formatted(TIMEOUT_MS))),
          compileTime, TIMEOUT_MS));
        break;
      }
    }

    return results;
  }

  private static TestRunResult invokeWithTimeout(final Method method,
                                                 final Object[] convertedArgs,
                                                 final long compileTime,
                                                 final Object[] originalArgs) throws TimeoutException {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;
    final List<TestError> errors = new ArrayList<>();
    final AtomicLong runTime = new AtomicLong();
    Object output = null;

    System.setOut(new PrintStream(stream));
    System.setErr(new PrintStream(stream));

    try {
      Future<Object> future = executor.submit(() -> {
        long start = System.currentTimeMillis();
        try {
          return method.invoke(null, convertedArgs);
        } finally {
          runTime.set(System.currentTimeMillis() - start);
        }
      });

      output = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);

    } catch (final TimeoutException exception) {
      errors.add(new TestError("TimeoutException", "Method execution exceeded %s ms.".formatted(TIMEOUT_MS)));
      throw exception;
    } catch (final ExecutionException exception) {
      if (exception.getCause() instanceof InvocationTargetException invocationTargetException) {
        final Throwable throwable = invocationTargetException.getTargetException();
        errors.add(new TestError(throwable.getClass().getSimpleName(), throwable.getMessage()));
      } else {
        final Throwable cause = exception.getCause();
        errors.add(new TestError(
          cause.getClass().getSimpleName(),
          Optional.ofNullable(cause.getMessage()).orElse("No error message")));
      }
    } catch (final InterruptedException exception) {
      Thread.currentThread().interrupt();
      errors.add(new TestError("InterruptedException", "Thread was interrupted during execution."));
    } catch (final Exception exception) {
      errors.add(new TestError(exception.getClass().getSimpleName(),
        Optional.ofNullable(exception.getMessage()).orElse("Unexpected error")));
    } finally {
      executor.shutdownNow();
      System.out.flush();
      System.err.flush();
      System.setOut(originalOut);
      System.setErr(originalErr);
    }

    return new TestRunResult(
      Arrays.stream(originalArgs).toList(),
      output,
      Arrays.asList(stream.toString().split("\\R")),
      errors,
      compileTime,
      runTime.get()
    );
  }

  private static Object[] convertArguments(final Object[] args,
                                           final Class<?>[] paramTypes) {
    final Object[] convertedArgs = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
      final Object arg = args[i];
      final Class<?> expectedType = paramTypes[i];

      if (expectedType.isArray() && arg instanceof List<?>) {
        List<?> list = (List<?>) arg;
        Class<?> componentType = expectedType.getComponentType();
        Object array = java.lang.reflect.Array.newInstance(componentType, list.size());
        for (int j = 0; j < list.size(); j++) {
          java.lang.reflect.Array.set(array, j, list.get(j));
        }
        convertedArgs[i] = array;
      } else {
        convertedArgs[i] = arg;
      }
    }
    return convertedArgs;
  }

  /**
   * Converts compilation diagnostics into a single {@link TestRunResult} containing all error messages.
   *
   * @param diagnostics the compiler diagnostics
   * @param compileTime the time spent during compilation
   * @return a list with one ExecutionResult representing the compilation failure
   */
  private static List<TestRunResult> compilerError(final DiagnosticCollector<JavaFileObject> diagnostics,
                                                   final long compileTime) {
    final StringBuilder errorBuilder = new StringBuilder();

    for (final Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
      errorBuilder
        .append("Line ").append(diagnostic.getLineNumber())
        .append(": ").append(diagnostic.getMessage(null))
        .append("\n");
    }

    final TestError error = new TestError("Compiler", "Compilation failed:\n" + errorBuilder);

    return Collections.singletonList(new TestRunResult(null, null, Collections.emptyList(),
      Collections.singletonList(error), compileTime, 0)
    );
  }

  /**
   * Extracts the method name from the given method code.
   *
   * @param methodCode the Java method code as a string
   * @return the name of the method
   * @throws IllegalArgumentException if no method name could be extracted
   */
  private static String extractMethodName(final String methodCode) {
    final int staticIndex = methodCode.indexOf("static");
    if (staticIndex == -1) {
      throw new IllegalArgumentException("No 'static' keyword found in the method code.");
    }

    final int start = staticIndex + "static".length();
    final int openParen = methodCode.indexOf("(", start);
    if (openParen == -1) {
      throw new IllegalArgumentException("No opening parenthesis '(' found after 'static'.");
    }

    final String methodSignature = methodCode.substring(start, openParen).trim();
    final String[] parts = methodSignature.split("\\s+");
    if (parts.length == 0) {
      throw new IllegalArgumentException("Could not extract method name from signature: " + methodSignature);
    }

    return parts[parts.length - 1];
  }
}
