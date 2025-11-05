package devdojo.logic.amqp.config;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;

@Slf4j
public abstract class AbstractRabbitConfig {

  @Bean
  public @NotNull Jackson2JsonMessageConverter jsonMessageConverter() {
    final Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
    log.info("Initialized jackson2JsonMessageConverter for Rabbit.");
    return jackson2JsonMessageConverter;
  }

  @Bean
  public @NotNull RabbitAdmin rabbitAdmin(final ConnectionFactory connectionFactory) {
    return new RabbitAdmin(connectionFactory);
  }
}
