package devdojo.logic.websocket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Default implementation of {@link IPresenceListener}.
 * <p>
 * Listens to WebSocket connect and disconnect events and
 * updates the global presence state accordingly.
 */
@Slf4j
@Component
public class PresenceListener implements IPresenceListener {

  private final IPresenceService presenceService;

  public PresenceListener(final IPresenceService presenceService) {
    this.presenceService = presenceService;
  }

  @Override
  public void handleConnect(final String userId) {
    this.presenceService.userConnected(userId);

    log.debug(
      "WebSocket CONNECT: userId='{}', activeUsers={}",
      userId,
      this.presenceService.getActiveUserCount()
    );
  }

  @Override
  public void handleDisconnect(final String userId) {
    this.presenceService.userDisconnected(userId);

    log.debug(
      "WebSocket DISCONNECT: userId='{}', activeUsers={}",
      userId,
      this.presenceService.getActiveUserCount()
    );
  }

  @EventListener
  public void onSessionConnect(final SessionConnectEvent event) {
    final StompHeaderAccessor accessor =
      StompHeaderAccessor.wrap(event.getMessage());

    if (accessor.getUser() != null) {
      handleConnect(accessor.getUser().getName());
    } else {
      log.debug(
        "WebSocket CONNECT without authenticated user (sessionId={})",
        accessor.getSessionId()
      );
    }
  }

  @EventListener
  public void onSessionDisconnect(final SessionDisconnectEvent event) {
    final StompHeaderAccessor accessor =
      StompHeaderAccessor.wrap(event.getMessage());

    if (accessor.getUser() != null) {
      handleDisconnect(accessor.getUser().getName());
    } else {
      log.debug(
        "WebSocket DISCONNECT without authenticated user (sessionId={})",
        accessor.getSessionId()
      );
    }
  }
}
