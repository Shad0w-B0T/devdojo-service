package devdojo.logic.websocket.service;

/**
 * Service for managing cluster-wide WebSocket presence state.
 * <p>
 * Implementations are responsible for tracking connected users
 * and providing the current active user count.
 */
public interface IPresenceService {

  /**
   * Registers a user as connected.
   *
   * @param userId the authenticated user identifier
   */
  void userConnected(final String userId);

  /**
   * Registers a user as disconnected.
   *
   * @param userId the authenticated user identifier
   */
  void userDisconnected(final String userId);

  /**
   * Returns the current number of active users.
   *
   * @return active user count (never negative)
   */
  long getActiveUserCount();
}
