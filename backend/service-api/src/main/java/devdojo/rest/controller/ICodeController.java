package devdojo.rest.controller;

import devdojo.schema.task.Test;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
    @NotNull
    String languageId,
    @NotBlank String payload,
    @NotNull(message = "Unit tests list must not be null")
    @Valid
    List<Test> unitTests
  ) {
  }
}
