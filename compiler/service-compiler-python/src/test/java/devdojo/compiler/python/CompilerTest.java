
package devdojo.compiler.python;

import devdojo.compiler.ICompiler;
import devdojo.schema.code.TestRunResult;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PythonExecutor Unit Tests")
class CompilerTest {

  private ICompiler compiler;

  @BeforeEach
  @DisplayName("Initialize PythonExecutor before each test")
  void setUp() {
    compiler = new Compiler();
  }

  @Test
  @DisplayName("Should correctly execute simple Python addition")
  void testSimplePythonCodeExecution() throws Exception {
    final String pythonCode = "def add(a, b):\n    return int(a) + int(b)";
    final Object[][] arguments = new Object[][] {{2, 3}, {5, 7}};

    final List<TestRunResult> results = compiler.compileAndRun(pythonCode, arguments);

    assertEquals(2, results.size(), "Expected 2 execution results");

    final TestRunResult firstResult = results.get(0);
    assertEquals(5, firstResult.output());
    assertTrue(firstResult.errors().isEmpty(), "First result should have no errors");

    final TestRunResult secondResult = results.get(1);
    assertEquals(12, secondResult.output());
    assertTrue(secondResult.errors().isEmpty(), "Second result should have no errors");
  }

  @Test
  @DisplayName("Should capture printed output and return correct length")
  void testPythonCodeWithPrintStatements() throws Exception {
    final String pythonCode = "def greet(name):\n    print(f'Hello, {name}!')\n    return len(name)";
    final Object[][] arguments = new Object[][] {{"Alice"}, {"Bob"}};

    final List<TestRunResult> results = compiler.compileAndRun(pythonCode, arguments);

    assertEquals(2, results.size(), "Expected 2 execution results");

    final TestRunResult firstResult = results.get(0);
    assertEquals(5, firstResult.output());
    assertTrue(firstResult.logs().contains("Hello, Alice!"), "First result should log greeting for Alice");

    final TestRunResult secondResult = results.get(1);
    assertEquals(3, secondResult.output());
    assertTrue(secondResult.logs().contains("Hello, Bob!"), "Second result should log greeting for Bob");
  }

  @Test
  @DisplayName("Should handle and report Python division by zero error")
  void testPythonCodeWithError() throws Exception {
    final String pythonCode = "def divide(a, b):\n    return a // b";
    final Object[][] arguments = new Object[][] {{10, 2}, {10, 0}};

    final List<TestRunResult> results = compiler.compileAndRun(pythonCode, arguments);

    assertEquals(2, results.size(), "Expected 2 execution results");

    final TestRunResult firstResult = results.get(0);
    assertEquals(5, firstResult.output());
    assertTrue(firstResult.errors().isEmpty(), "First result should have no errors");

    final TestRunResult secondResult = results.get(1);
    assertNull(secondResult.output(), "Second result should have null output due to error");
    assertFalse(secondResult.errors().isEmpty(), "Second result should contain errors");

    final String errorMessage = secondResult.errors().get(0).message();
    assertTrue(
      errorMessage.contains("division by zero") ||
        errorMessage.contains("integer division or modulo by zero"),
      "Error message should indicate division by zero"
    );
  }


}
