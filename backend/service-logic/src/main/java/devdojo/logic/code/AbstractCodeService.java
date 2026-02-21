package devdojo.logic.code;

import devdojo.logic.compiler.ICompilerService;
import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.task.TaskEntity;
import devdojo.schema.amqp.CompileRequestPayload;
import devdojo.schema.amqp.RabbitConst;
import devdojo.schema.task.ArgumentType;
import devdojo.schema.task.ITest;
import devdojo.schema.task.ITestArgument;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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
                                   @NotNull final String methodCode) {
    return this.executeCode(taskEntity.languageEntity(), taskEntity, methodCode, taskEntity.unitTests());
  }

  @Override
  public UUID executeCode(final LanguageEntity languageEntity,
                          @Null final TaskEntity taskEntity,
                          final String methodCode,
                          final List<ITest> unitTests) {
    final String languageId = languageEntity.externalId();

    this.ensureCompilerQueueExists(languageId);

    final UUID uuid = UUID.randomUUID();
    final List<List<Object>> argumentSets = extractTestInputs(unitTests);

    final CompileRequestPayload payload = new CompileRequestPayload(uuid, methodCode, argumentSets);

    final String taskId = (taskEntity != null) ? taskEntity.externalId() : null;

    this.compilerService.createCompilerTask(
      new ICompilerService.CreateCompilerTask(uuid, methodCode, unitTests, taskId, languageId)
    );

    this.rabbitTemplate.convertAndSend(
      RabbitConst.EXCHANGE_COMPILER,
      RabbitConst.routingKeyForCompiler(languageId),
      payload
    );

    return uuid;
  }

  private List<List<Object>> extractTestInputs(final List<? extends ITest> unitTests) {
    return unitTests.stream()
      .map(testEntity -> testEntity.input().stream()
        .filter(arg -> arg.type() == ArgumentType.INPUT)
        .map(ITestArgument::value)
        .toList())
      .toList();
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
