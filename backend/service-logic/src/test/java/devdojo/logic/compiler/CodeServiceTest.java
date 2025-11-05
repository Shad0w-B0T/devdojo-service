package devdojo.logic.compiler;

import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.code.CodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@DisplayName("Test CodeService executeCode method without gRPC-Server")
@ExtendWith(MockitoExtension.class)
class CodeServiceTest {

  @Mock
  private RabbitAdmin rabbitAdmin;
  @Mock
  private RabbitTemplate rabbitTemplate;
  @Mock
  private TopicExchange topicExchange;
  @Mock
  private TaskEntity taskEntity;
  @Mock
  private ICompilerService compilerService;

  private CodeService codeService;

  @BeforeEach
  void setUp() {
    codeService = new CodeService(this.compilerService, this.rabbitAdmin, this.rabbitTemplate, this.topicExchange);
  }

  @Test
  @DisplayName("ExecuteCode correct answer without error")
  void testHelloWorldErfolgreich() {
    // Arrange
    String javaCode = """
      public static String sayHello() { return "Hello, World!"; }
      """;

    //Todo fix texst
    // Act
    //List<CodeResult> results = codeService.executeCode("localhost", javaCode, List.of(List.of()));

    // Assert
   /* assertEquals(1, results.size());
    CodeResult r = results.getFirst();
    assertTrue(r.errors().isEmpty());
    assertEquals("Hello, World!", r.output());

    */
  }

  @Test
  @DisplayName("ExecuteCode answer with error")
  void testFehlerMapping() {
    // Arrange: ExecutionResult with Error


    //Todo fix tests
    // Act
    // List<CodeResult> results =
    //   codeService.executeCode("localhost", "broken code", List.of(List.of()));

    // Assert
    /*assertEquals(1, results.size());
    List<CodeResultError> errors = results.getFirst().errors();
    assertFalse(errors.isEmpty());
    assertEquals("syntax error", errors.getFirst().message());
    assertEquals("COMPILE", errors.getFirst().type());

     */
  }

  @Test
  @DisplayName("ExecuteCode correct answer with multiple arguments")
  void testRequestWirdKorrektGebaut() {
    // Arrange

  }
}
