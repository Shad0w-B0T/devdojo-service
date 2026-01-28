package devdojo.logic.code;

import devdojo.logic.compiler.ICompilerService;
import devdojo.logic.database.task.TaskEntity;
import devdojo.schema.amqp.CompileRequestPayload;
import devdojo.schema.amqp.RabbitConst;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public abstract class AbstractCodeService implements ICodeService {

  private final ICompilerService compilerService;
  private final RabbitAdmin rabbitAdmin;
  private final RabbitTemplate rabbitTemplate;
  private final TopicExchange compilerExchange;

  protected AbstractCodeService(final ICompilerService compilerService,
                                final RabbitAdmin rabbitAdmin,
                                final RabbitTemplate rabbitTemplate,
                                final TopicExchange compilerExchange) {
    this.compilerService = compilerService;
    this.rabbitAdmin = rabbitAdmin;
    this.rabbitTemplate = rabbitTemplate;
    this.compilerExchange = compilerExchange;
  }

  @Override
  public @NotNull UUID executeCode(final TaskEntity taskEntity,
                                   @NotNull final String methodCode,
                                   @NotNull final List<List<Object>> argumentSets) {
    final String compilerKey = taskEntity.languageEntity().token();
    this.ensureCompilerQueueExists(compilerKey);

    final UUID uuid = UUID.randomUUID();
    final CompileRequestPayload payload = new CompileRequestPayload(uuid, methodCode, argumentSets);

    this.compilerService.createCompilerTask(
      new ICompilerService.CreateCompilerTask(uuid, methodCode, taskEntity.externalId()));

    this.rabbitTemplate.convertAndSend(RabbitConst.EXCHANGE_COMPILER,
      RabbitConst.routingKeyForCompiler(compilerKey),
      payload);

    return uuid;
  }

  private void ensureCompilerQueueExists(final String compilerKey) {
    final String queueName = RabbitConst.queueForCompiler(compilerKey);
    final String routingKey = RabbitConst.routingKeyForCompiler(compilerKey);

    final Queue queue = new Queue(queueName, true);
    final Binding binding = BindingBuilder.bind(queue).to(compilerExchange).with(routingKey);

    this.rabbitAdmin.declareQueue(queue);
    this.rabbitAdmin.declareBinding(binding);
  }
}
