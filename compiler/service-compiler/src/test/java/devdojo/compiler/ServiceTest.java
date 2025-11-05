package devdojo.compiler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceTest - Code Execution Service Unit Tests")
final class ServiceTest {
/*
  @Mock
  private ICompiler compilerMock;
  @Mock
  private StreamObserver<CodeExecutionReply> responseObserver;

  private Service service;

  @BeforeEach
  void setUp() {
    CompilerContext.instance().compiler(this.compilerMock);
    CompilerContext.instance().securityFilters(List.of(code -> true)); // Allow all code for tests
    service = new Service();
  }

  @Test
  @DisplayName("should execute code and return a successful result")
  void shouldExecuteCodeAndReturnResult() throws Exception {
    final String code = "public int add(int a, int b) { return a + b; }";

    final CodeExecutionRequest request = CodeExecutionRequest.newBuilder()
      .setMethodCode(code)
      .addArgumentSets(ArgumentSet.newBuilder()
        .addArguments(Argument.newBuilder().setIntValue(2))
        .addArguments(Argument.newBuilder().setIntValue(3)))
      .build();

    final CodeResult result = new CodeResult(List.of(2, 3), 5, List.of(), List.of(), 10, 2);

    when(this.compilerMock.compileAndRun(any(), any())).thenReturn(List.of(result));

    service.executeCode(request, responseObserver);

    verify(responseObserver).onNext(any(CodeExecutionReply.class));
    verify(responseObserver).onCompleted();
  }

  @Test
  @DisplayName("should fail execution due to security violation")
  void shouldFailOnSecurityViolation() {
    CompilerContext.instance().securityFilters(List.of(code -> false)); // Block all code

    final CodeExecutionRequest request = CodeExecutionRequest.newBuilder()
      .setMethodCode("maliciousCode()")
      .build();

    service.executeCode(request, responseObserver);

    verify(responseObserver).onError(any(Exception.class));
  }

 */
}
