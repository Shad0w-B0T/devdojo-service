package devdojo.logic.websocket;

import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

public interface IWebSocketConfig extends WebSocketMessageBrokerConfigurer {

  void configureClientInboundChannel(final ChannelRegistration registration);

  void registerStompEndpoints(final StompEndpointRegistry registry);

  void configureMessageBroker(final MessageBrokerRegistry registry);

}
