package devdojo.logic.amqp.message;

import devdojo.logic.compiler.ICompilerService;
import devdojo.schema.amqp.HeartbeatPayload;
import devdojo.schema.amqp.RabbitConst;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Slf4j
public abstract class AbstractHeartbeatSubscriber {

  private final ICompilerService compilerService;

  protected AbstractHeartbeatSubscriber(final ICompilerService compilerService) {
    this.compilerService = compilerService;
  }

  @RabbitListener(queues = RabbitConst.QUEUE_HEARTBEAT)
  public void handleHeartbeatMessage(final HeartbeatPayload payload) {
    this.compilerService.updateHeartbeat(new ICompilerService.UpdateHeartbeatRecord(
      UUID.fromString(payload.compilerId()),
      payload.languageKey(),
      payload.cpuUsage(),
      payload.cpuCores(),
      payload.memoryUsageMb(),
      payload.memoryTotalMb(),
      payload.totalTasksProcessed()));

    log.debug(
      "Heartbeat received from compiler {} (language: {}): " +
        "CPU {}% ({} cores), Memory {} / {} MB, Total tasks processed: {}",
      payload.compilerId(),
      payload.languageKey(),
      payload.cpuUsage(),
      payload.cpuCores(),
      payload.memoryUsageMb(),
      payload.memoryTotalMb(),
      payload.totalTasksProcessed()
    );
  }
}
