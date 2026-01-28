package devdojo.logic.websocket;

import devdojo.logic.security.web.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketConfig {

  public WebSocketConfig(final JwtAuthenticationFilter jwtAuthenticationFilter,
                         @Value("${spring.rabbitmq.host}") final String rabbitHost,
                         @Value("${spring.rabbitmq.stomp.port}") final int rabbitPort,
                         @Value("${spring.rabbitmq.username}") final String rabbitUsername,
                         @Value("${spring.rabbitmq.password}") final String rabbitPassword,
                         @Value("${spring.rabbitmq.virtual-host}") final String rabbitVirtualHost) {
    super(jwtAuthenticationFilter, rabbitHost, rabbitPort, rabbitUsername, rabbitPassword, rabbitVirtualHost);
  }
}

