package devdojo.rest.controller;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface ICodeController {

  @NotNull(message = "Response must not be null")
  ResponseEntity<IProcessCodeResponse> processCode(
    @NotNull(message = "Payload must not be null") String payload,
    @NotNull(message = "Task ID must not be null") String taskId
  );

  interface IProcessCodeResponse {
    @NotNull(message = "Id must not be null")
    UUID id();
   /* @NotNull(message = "Results must not be null")
    List<CodeResultUnit> results();

    @NotNull(message = "Completed must not be null")
    boolean completed();

    @NotNull(message = "Score must not be null")
    int score();

    */
  }
}
