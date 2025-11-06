package devdojo.compiler.javascript;

import devdojo.compiler.ICompiler;
import devdojo.schema.code.TestRunResult;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JavaScriptExecutor Unit Tests")
class JavaScriptCompilerTest {

  private ICompiler compiler;

  @BeforeEach
  @DisplayName("Initialize JavaScriptExecutor before each test")
  void setUp() {
    this.compiler = new JavaScriptCompiler();
  }

  @Test
  @DisplayName("Should correctly execute simple JavaScript addition")
  void testSimpleJavaScriptCodeExecution() throws Exception {
    final String jsCode = "function add(a, b) {\n  return a + b;\n}";
    final Object[][] arguments = new Object[][] {{2, 3}, {5, 7}};

    final List<TestRunResult> results = compiler.compileAndRun(jsCode, arguments);

    assertEquals(2, results.size(), "Expected 2 execution results");

    final TestRunResult firstResult = results.get(0);
    assertEquals(5, firstResult.output());
    assertTrue(firstResult.errors().isEmpty(), "First result should have no errors");

    final TestRunResult secondResult = results.get(1);
    assertEquals(12, secondResult.output());
    assertTrue(secondResult.errors().isEmpty(), "Second result should have no errors");
  }

  @Test
  @DisplayName("Should capture console.log output and return string length")
  void testJavaScriptCodeWithConsoleLog() throws Exception {
    final String jsCode = """
      function greet(name) {
        console.log('Hello, ' + name + '!');
        return name.length;
      }
      """;
    final Object[][] arguments = new Object[][] {{"Alice"}, {"Bob"}};

    final List<TestRunResult> results = compiler.compileAndRun(jsCode, arguments);

    assertEquals(2, results.size(), "Expected 2 execution results");

    final TestRunResult firstResult = results.get(0);
    assertEquals(5, firstResult.output());
    assertTrue(firstResult.logs().contains("Hello, Alice!"), "First result should log greeting for Alice");

    final TestRunResult secondResult = results.get(1);
    assertEquals(3, secondResult.output());
    assertTrue(secondResult.logs().contains("Hello, Bob!"), "Second result should log greeting for Bob");
  }

  @Test
  @DisplayName("Should handle and report JavaScript division by zero error")
  void testJavaScriptCodeWithError() throws Exception {
    final String jsCode = """
      function divide(a, b) {
        if (b === 0) throw new Error('Division by zero');
        return a / b;
      }
      """;
    final Object[][] arguments = new Object[][] {{10, 2}, {10, 0}};

    final List<TestRunResult> results = compiler.compileAndRun(jsCode, arguments);

    assertEquals(2, results.size(), "Expected 2 execution results");

    final TestRunResult firstResult = results.get(0);
    assertEquals(5.0, ((Number) firstResult.output()).doubleValue());
    assertTrue(firstResult.errors().isEmpty(), "First result should have no errors");

    final TestRunResult secondResult = results.get(1);
    assertNull(secondResult.output(), "Second result should have null output due to error");
    assertFalse(secondResult.errors().isEmpty(), "Second result should contain errors");

    final String errorMessage = secondResult.errors().get(0).message();
    assertTrue(errorMessage.contains("Division by zero"), "Error message should contain 'Division by zero'");
  }

  @Test
  @DisplayName("Should return JavaScript object with multiple calculated values")
  void testJavaScriptCodeWithMultipleStatements() throws Exception {
    final String jsCode = """
      function processNumber(n) {
        console.log('Processing number:', n);
        const squared = n * n;
        const doubled = n * 2;
        return {
          original: n,
          squared: squared,
          doubled: doubled
        };
      }
      """;
    final Object[][] arguments = new Object[][] {{3}, {5}};

    final List<TestRunResult> results = compiler.compileAndRun(jsCode, arguments);

    assertEquals(2, results.size(), "Expected 2 execution results");

    final Map<?, ?> result1 = (Map<?, ?>) results.get(0).output();
    assertEquals(3, result1.get("original"));
    assertEquals(9, result1.get("squared"));
    assertEquals(6, result1.get("doubled"));

    final Map<?, ?> result2 = (Map<?, ?>) results.get(1).output();
    assertEquals(5, result2.get("original"));
    assertEquals(25, result2.get("squared"));
    assertEquals(10, result2.get("doubled"));
  }
}
