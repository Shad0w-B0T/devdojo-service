package devdojo.rest.controller;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface ICodeController {

  @NotNull(message = "Response must not be null")
  ResponseEntity<ProcessCodeResponse> processCode(
    @NotNull(message = "Payload must not be null") final String payload,
    @NotNull(message = "Task ID must not be null") final String taskId
  );

  ResponseEntity<ProcessCodeResponse> processCodeWithTests(
    final ExecuteCodeWithTestsRequest request
  );

  record ProcessCodeResponse(@NotNull(message = "Id must not be null") UUID id) {

  }

  record ExecuteCodeWithTestsRequest(
    String payload,
    List<List<Object>> testInputs
  ) {
  }
}
