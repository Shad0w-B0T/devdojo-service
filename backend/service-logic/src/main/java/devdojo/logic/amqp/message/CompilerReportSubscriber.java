package devdojo.logic.amqp.message;

import devdojo.logic.compiler.ICompilerService;
import org.springframework.stereotype.Service;

@Service
public class CompilerReportSubscriber extends AbstractCompilerReportSubscriber {

  protected CompilerReportSubscriber(final ICompilerService compilerService) {
    super(compilerService);
  }
}
