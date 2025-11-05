package devdojo.logic.amqp.message;

import devdojo.schema.amqp.RabbitConst;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

public abstract class AbstractRabbitRouting {

  @Bean
  public TopicExchange compiler() {
    return new TopicExchange(RabbitConst.EXCHANGE_COMPILER, true, false);
  }

  @Bean
  public @NotNull TopicExchange compilerReport() {
    return new TopicExchange(RabbitConst.EXCHANGE_COMPILER_REPORT, true, false);
  }

  @Bean
  public @NotNull TopicExchange heartbeat() {
    return new TopicExchange(RabbitConst.EXCHANGE_HEARTBEAT, true, false);
  }

  @Bean
  public @NotNull Queue compilerReportQueue() {
    return new Queue(RabbitConst.QUEUE_COMPILER_REPORT, true);
  }

  @Bean
  public @NotNull Queue heartbeatQueue() {
    return new Queue(RabbitConst.QUEUE_HEARTBEAT, true);
  }

  @Bean
  public Binding compilerReportBinding(@Qualifier("compilerReportQueue") final Queue queue,
                                       @Qualifier("compilerReport") final TopicExchange exchange) {
    return BindingBuilder
      .bind(queue)
      .to(exchange)
      .with(RabbitConst.KEY_COMPILER_REPORT);
  }

  @Bean
  public Binding heartbeatBinding(@Qualifier("heartbeatQueue") final Queue queue,
                                  @Qualifier("heartbeat") final TopicExchange exchange) {
    return BindingBuilder
      .bind(queue)
      .to(exchange)
      .with(RabbitConst.KEY_HEARTBEAT);
  }
}
