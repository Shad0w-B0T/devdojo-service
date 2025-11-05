package devdojo.logic.amqp.message;

import devdojo.logic.compiler.ICompilerService;
import org.springframework.stereotype.Service;

@Service
public class HeartbeatSubscriber extends AbstractHeartbeatSubscriber {

  protected HeartbeatSubscriber(final ICompilerService compilerService) {
    super(compilerService);
  }
}
