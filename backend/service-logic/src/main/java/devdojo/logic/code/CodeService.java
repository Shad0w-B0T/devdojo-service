package devdojo.logic.code;

import devdojo.logic.compiler.ICompilerService;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CodeService extends AbstractCodeService {

  public CodeService(final ICompilerService compilerService,
                     final RabbitAdmin rabbitAdmin,
                     final RabbitTemplate rabbitTemplate,
                     @Qualifier("compiler") final TopicExchange compilerExchange) {
    super(compilerService, rabbitAdmin, rabbitTemplate, compilerExchange);
  }
}
