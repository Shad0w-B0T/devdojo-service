package devdojo.logic.websocket;

import devdojo.logic.security.web.JwtAuthenticationFilter;
import jakarta.validation.constraints.NotNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Validated
public abstract class AbstractWebSocketConfig implements IWebSocketConfig {

  private final @NotNull JwtAuthenticationFilter jwtAuthenticationFilter;
  private final String rabbitHost;
  private final int rabbitPort;
  private final String rabbitUsername;
  private final String rabbitPassword;
  private final String rabbitVirtualHost;

  public AbstractWebSocketConfig(final JwtAuthenticationFilter jwtAuthenticationFilter,
                                 final String rabbitHost,
                                 final int rabbitPort,
                                 final String rabbitUsername,
                                 final String rabbitPassword,
                                 final String rabbitVirtualHost) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.rabbitHost = rabbitHost;
    this.rabbitPort = rabbitPort;
    this.rabbitUsername = rabbitUsername;
    this.rabbitPassword = rabbitPassword;
    this.rabbitVirtualHost = rabbitVirtualHost;
  }

  @Override
  public void configureClientInboundChannel(final ChannelRegistration registration) {
    registration.interceptors(this.jwtAuthenticationFilter);
  }

  @Override
  public void registerStompEndpoints(final StompEndpointRegistry registry) {
    registry.addEndpoint("/ws");
  }

  @Override
  public void configureMessageBroker(final MessageBrokerRegistry registry) {
    registry
      .enableStompBrokerRelay("/topic", "/queue")
      .setRelayHost(this.rabbitHost)
      .setRelayPort(this.rabbitPort)
      .setClientLogin(this.rabbitUsername)
      .setClientPasscode(this.rabbitPassword)
      .setSystemLogin(this.rabbitUsername)
      .setSystemPasscode(this.rabbitPassword)
      .setVirtualHost(this.rabbitVirtualHost);
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
  }
}
