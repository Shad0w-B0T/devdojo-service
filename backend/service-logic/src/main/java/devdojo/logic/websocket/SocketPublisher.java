package devdojo.logic.websocket;

import devdojo.logic.websocket.service.IPresenceService;
import jakarta.validation.constraints.NotNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class SocketPublisher extends AbstractSocketPublisher {
  protected SocketPublisher(
    @NotNull(message = "SimpMessagingTemplate must not be null.") final SimpMessagingTemplate messagingTemplate,
    @NotNull(message = "PresenceService must not be null.") final IPresenceService presenceService) {
    super(messagingTemplate, presenceService);
  }
}
