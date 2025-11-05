package devdojo.logic.scheduler;

import devdojo.logic.compiler.ICompilerService;
import org.springframework.stereotype.Service;

@Service
public class CompilerScheduler extends AbstractCompilerScheduler {
  protected CompilerScheduler(final ICompilerService compilerService) {
    super(compilerService);
  }
}
