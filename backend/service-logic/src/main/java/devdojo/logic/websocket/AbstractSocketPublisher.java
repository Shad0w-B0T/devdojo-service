package devdojo.logic.websocket;

import devdojo.schema.code.TestRun;
import jakarta.validation.constraints.NotNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;

@Validated
public abstract class AbstractSocketPublisher implements ISocketPublisher {

  private final @NotNull SimpMessagingTemplate messagingTemplate;

  protected AbstractSocketPublisher(
    @NotNull(message = "SimpMessagingTemplate must not be null") final SimpMessagingTemplate messagingTemplate
  ) {
    this.messagingTemplate = messagingTemplate;
  }

  @Override
  public void notifyTaskUpdate(final String authId,
                               final TestRun testRun) {
    this.messagingTemplate.convertAndSendToUser(
      authId,
      "/queue/tasks",
      testRun
    );
  }
}
