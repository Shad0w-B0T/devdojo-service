package devdojo.rest.controller;

import devdojo.logic.code.ICodeService;
import devdojo.logic.language.ILanguageService;
import devdojo.logic.task.ITaskService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CodeController extends AbstractCodeController {

  public CodeController(final ITaskService taskService,
                        final ICodeService codeService,
                        final ILanguageService languageService) {
    super(taskService, codeService, languageService);
  }
}

