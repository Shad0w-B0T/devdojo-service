package devdojo.logic.websocket.service;

/**
 * Listener for WebSocket presence events.
 * Implementations react to user connect and disconnect events
 * to update global presence state.
 */
public interface IPresenceListener {
  /**
   * Called when a WebSocket session is established.
   *
   * @param userId the authenticated user identifier
   */
  void handleConnect(final String userId);

  /**
   * Called when a WebSocket session is closed.
   *
   * @param userId the authenticated user identifier
   */
  void handleDisconnect(final String userId);
}

