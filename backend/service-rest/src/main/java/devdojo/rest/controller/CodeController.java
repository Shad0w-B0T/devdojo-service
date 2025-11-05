package devdojo.rest.controller;

import devdojo.logic.code.ICodeService;
import devdojo.logic.compiler.ICompilerService;
import devdojo.logic.task.ITaskService;
import devdojo.logic.task.ITasksProgressService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CodeController extends AbstractCodeController {

  public CodeController(final ITaskService taskService,
                        final ICodeService codeService,
                        final ITasksProgressService tasksProgressService,
                        final ICompilerService compilerService) {
    super(taskService, codeService, tasksProgressService, compilerService);
  }
}

