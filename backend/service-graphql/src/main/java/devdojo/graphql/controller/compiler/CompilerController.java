package devdojo.graphql.controller.compiler;

import devdojo.logic.compiler.ICompilerService;
import org.springframework.stereotype.Controller;

@Controller
public class CompilerController extends AbstractCompilerController {

  protected CompilerController(final ICompilerService compilerService) {
    super(compilerService);
  }
}
