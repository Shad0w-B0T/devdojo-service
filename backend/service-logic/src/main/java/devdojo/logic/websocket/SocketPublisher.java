package devdojo.logic.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SocketPublisher extends AbstractSocketPublisher {
  protected SocketPublisher(final SimpMessagingTemplate messagingTemplate) {
    super(messagingTemplate);
  }
}
