package devdojo.graphql.controller.compiler;

import com.fasterxml.jackson.databind.ObjectMapper;
import devdojo.logic.compiler.ICompilerService;
import org.springframework.stereotype.Controller;

@Controller
public class CompilerController extends AbstractCompilerController {

  protected CompilerController(final ICompilerService compilerService,
                              final ObjectMapper objectMapper) {
    super(compilerService, objectMapper);
  }
}
