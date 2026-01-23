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

  public AbstractWebSocketConfig(final JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
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
    registry.enableSimpleBroker("/topic", "/queue");
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
  }
}
