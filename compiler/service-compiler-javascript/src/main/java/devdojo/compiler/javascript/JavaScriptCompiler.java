package devdojo.compiler.javascript;

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
public final class JavaScriptCompiler implements ICompiler {
  private static final long SECOND_MS = 1000;
  private static final long TIMEOUT_SECONDS = 10;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final String BOOTSTRAP_SCRIPT_PATH = "src/main/resources/executor_bootstrap.js";

  @Override
  public List<TestRunResult> compileAndRun(final String code, final Object[][] arguments) throws Exception {
    log.debug("JavaScript execution task requested.");

    // Prepare data for Node.js script
    Map<String, Object> payload = new HashMap<>();
    payload.put("code", code);
    payload.put("arguments", arguments);
    String jsonInput = objectMapper.writeValueAsString(payload);

    ProcessBuilder builder = new ProcessBuilder("node", BOOTSTRAP_SCRIPT_PATH);
    Process process = builder.start();

    // Send JSON input to Node.js process
    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
      writer.write(jsonInput);
    }

    // Wait for process to complete
    boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

    // Read results
    String stdOut = readStream(process.getInputStream());
    String stdErr = readStream(process.getErrorStream());

    if (!finished) {
      process.destroyForcibly();
      log.warn("Node.js process timed out after {} seconds.", TIMEOUT_SECONDS);
      TestError error = new TestError("TimeoutException", "Execution exceeded " + TIMEOUT_SECONDS + " seconds.");
      return Collections.singletonList(new TestRunResult(null, null, Collections.emptyList(),
        Collections.singletonList(error), 0, TIMEOUT_SECONDS * SECOND_MS));
    }

    if (!stdErr.isEmpty()) {
      log.error("Node.js interpreter error: {}", stdErr);
      TestError error = new TestError("InterpreterError", stdErr);
      return Collections.singletonList(new TestRunResult(null, null, Collections.emptyList(),
        Collections.singletonList(error), 0, 0));
    }

    log.debug("Node.js process finished. Raw output: {}", stdOut);

    // Parse JSON output from script
    final TypeReference<List<TestRunResult>> typeRef = new TypeReference<>() {
    };
    List<TestRunResult> jsResults = objectMapper.readValue(stdOut, typeRef);

    return jsResults.stream()
      .map(dto -> new TestRunResult(
        dto.input(),
        dto.output(),
        dto.logs(),
        dto.errors(),
        0, // CompileTime is 0 for JS
        dto.runTimeMs()))
      .collect(Collectors.toList());
  }

  private String readStream(final InputStream inputStream) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      return reader.lines().collect(Collectors.joining("\n"));
    }
  }
}
