package devdojo.logic.websocket;

import devdojo.logic.websocket.service.IPresenceService;
import devdojo.logic.websocket.service.PresenceService;
import devdojo.schema.code.TestRun;
import jakarta.validation.constraints.NotNull;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;

@Validated
public abstract class AbstractSocketPublisher implements ISocketPublisher {

  private final long PRESENCE_BROADCAST = 20_000;

  private final @NotNull SimpMessagingTemplate messagingTemplate;
  private final IPresenceService presenceService;

  protected AbstractSocketPublisher(
    @NotNull(message = "SimpMessagingTemplate must not be null") final SimpMessagingTemplate messagingTemplate,
    @NotNull(message = "PresenceService must not be null")  final IPresenceService presenceService
  ) {
    this.messagingTemplate = messagingTemplate;
    this.presenceService = presenceService;
  }

  @Scheduled(fixedRate = PRESENCE_BROADCAST)
  @SchedulerLock(
    name = "presenceBroadcast",
    lockAtMostFor = "30s",
    lockAtLeastFor = "5s"
  )
  public void broadcastUsersActiveCount() {
    this.notifyUsersActiveCount();
  }

  @Override
  public void notifyUsersActiveCount() {
    this.messagingTemplate.convertAndSend(
      "/topic/usersactivecount",
      this.presenceService.getActiveUserCount()
    );
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
