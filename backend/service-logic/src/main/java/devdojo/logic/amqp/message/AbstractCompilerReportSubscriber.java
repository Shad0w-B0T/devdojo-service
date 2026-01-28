package devdojo.logic.amqp.message;

import devdojo.logic.compiler.ICompilerService;
import devdojo.schema.amqp.CompileResponsePayload;
import devdojo.schema.amqp.RabbitConst;
import devdojo.schema.exception.database.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Slf4j
public abstract class AbstractCompilerReportSubscriber {

  private final ICompilerService compilerService;

  protected AbstractCompilerReportSubscriber(final ICompilerService compilerService) {
    this.compilerService = compilerService;
  }

  @RabbitListener(queues = RabbitConst.QUEUE_COMPILER_REPORT)
  public void handleHeartbeatMessage(final CompileResponsePayload payload) {
    try {
      this.compilerService.completeCompilerTask(
        new ICompilerService.CompleteCompilerTask(payload.requestId(), payload.testRunResults()));
    } catch (final NotFoundException exception) {
      log.info("The compiler task={} not found, ignoring response.", payload.requestId());
    }
  }
}
