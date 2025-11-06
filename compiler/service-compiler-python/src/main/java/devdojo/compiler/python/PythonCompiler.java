package devdojo.compiler.python;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devdojo.compiler.ICompiler;
import devdojo.schema.code.TestError;
import devdojo.schema.code.TestRunResult;
import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PythonCompiler implements ICompiler {
  private static final int BYTES = 1024;
  private static final long SECOND_MS = 1000;
  private static final long TIMEOUT_SECONDS = 10;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private String findPythonExecutable() {
    // Try common Python executable names in order of preference
    String[] pythonCommands = {"python3", "python", "py"};

    for (String cmd : pythonCommands) {
      try {
        Process process = new ProcessBuilder(cmd, "--version").start();
        if (process.waitFor() == 0) {
          log.info("Found Python executable: {}", cmd);
          return cmd;
        }
      } catch (IOException | InterruptedException e) {
        // Try next command
        log.debug("Python command '{}' not available: {}", cmd, e.getMessage());
      }
    }
    throw new RuntimeException(
      "No suitable Python interpreter found. Please install Python 3 and ensure it's in your PATH.");
  }

  @Override
  public List<TestRunResult> compileAndRun(final String code, final Object[][] arguments) throws Exception {
    log.debug("Python execution task requested.");

    // 1. Prepare data for Python script (as JSON)
    Map<String, Object> payload = new HashMap<>();
    payload.put("code", code);
    payload.put("arguments", arguments);
    String jsonInput = objectMapper.writeValueAsString(payload);

    // Get the path to the Python bootstrap script
    String pythonScriptPath = getPythonScriptPath();
    String pythonExecutable = findPythonExecutable();

    log.info("Executing Python script with {}: {}", pythonExecutable, pythonScriptPath);

    // The command to execute Python with the script
    ProcessBuilder builder = new ProcessBuilder(pythonExecutable, "-u", pythonScriptPath);
    builder.redirectErrorStream(true);

    // Set up environment if needed
    Map<String, String> env = builder.environment();
    env.put("PYTHONIOENCODING", "UTF-8");

    Process process = builder.start();

    // 2. Send JSON input to the Python process
    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
      writer.write(jsonInput);
      writer.flush();
    }

    // 3. Wait for the process to finish (with timeout)
    boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

    // 4. Read the results
    String stdOut = readStream(process.getInputStream());
    String stdErr = readStream(process.getErrorStream());

    if (!finished) {
      process.destroyForcibly();
      log.warn("Python process timed out after {} seconds.", TIMEOUT_SECONDS);
      TestError error = new TestError("TimeoutException",
        "Execution exceeded " + TIMEOUT_SECONDS + " seconds.");
      return Collections.singletonList(new TestRunResult(null, null, Collections.emptyList(),
        Collections.singletonList(error), 0, TIMEOUT_SECONDS * SECOND_MS));
    }

    // If there was an error in the Python interpreter itself (not in user code)
    if (!stdErr.isEmpty()) {
      log.error("Python interpreter error: {}", stdErr);
      TestError error = new TestError("InterpreterError", stdErr);
      return Collections.singletonList(new TestRunResult(null, null, Collections.emptyList(),
        Collections.singletonList(error), 0, 0));
    }

    log.debug("Python process finished. Raw output: {}", stdOut);

    try {
      // 5. Convert JSON output from Python script back to Java objects
      TypeReference<List<TestRunResult>> typeRef = new TypeReference<>() {
      };
      List<TestRunResult> pythonResults = objectMapper.readValue(stdOut, typeRef);

      // Convert DTOs to Result objects
      return pythonResults.stream()
        .map(dto -> new TestRunResult(
          dto.input(),
          dto.output(),
          dto.logs() != null ? dto.logs() : Collections.emptyList(),
          dto.errors() != null ? dto.errors() : Collections.emptyList(),
          0, // CompileTime is 0 for Python
          dto.runTimeMs()))
        .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Error parsing Python execution results: {}", e.getMessage());
      TestError error = new TestError("ResultParseError",
        "Failed to parse execution results: " + e.getMessage());
      return Collections.singletonList(new TestRunResult(null, null, Collections.emptyList(),
        Collections.singletonList(error), 0, 0));
    }
  }

  private String getPythonScriptPath() {
    // First try to find the script in the resources
    String resourcePath = "/executor_bootstrap.py";
    try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
      if (is != null) {
        // If running from JAR, extract the script to a temporary file
        File tempFile = File.createTempFile("executor_bootstrap", ".py");
        tempFile.deleteOnExit();

        try (FileOutputStream out = new FileOutputStream(tempFile)) {
          byte[] buffer = new byte[BYTES];
          int bytesRead;
          while ((bytesRead = is.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
          }
        }
        return tempFile.getAbsolutePath();
      }
    } catch (IOException e) {
      log.warn("Failed to extract Python script from resources: {}", e.getMessage());
    }

    // Fallback: look for the script in the current directory
    File localFile = new File("executor_bootstrap.py");
    if (localFile.exists()) {
      return localFile.getAbsolutePath();
    }

    // If not found, try to create it
    try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
      if (is != null) {
        try (FileOutputStream out = new FileOutputStream(localFile)) {
          byte[] buffer = new byte[BYTES];
          int bytesRead;
          while ((bytesRead = is.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
          }
        }
        return localFile.getAbsolutePath();
      }
    } catch (IOException e) {
      log.error("Failed to create Python script file: {}", e.getMessage());
    }

    throw new RuntimeException("Python bootstrap script not found and could not be created");
  }

  private String readStream(final InputStream inputStream) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      return reader.lines().collect(Collectors.joining("\n"));
    }
  }

}
