package devdojo.logic.compiler;

import devdojo.logic.cache.compiler.CompilerTaskRepository;
import devdojo.logic.cache.compiler.CompilerTaskResultRepository;
import devdojo.logic.database.compiler.CompilerHeartbeatRepository;
import devdojo.logic.database.compiler.CompilerRepository;
import devdojo.logic.database.language.LanguageRepository;
import devdojo.logic.task.ITaskService;
import devdojo.logic.task.ITasksProgressService;
import org.springframework.stereotype.Service;

@Service
public class CompilerService extends AbstractCompilerService {


  protected CompilerService(final LanguageRepository languageRepository,
                            final ITaskService taskService,
                            final ITasksProgressService tasksProgressService,
                            final CompilerRepository compilerRepository,
                            final CompilerHeartbeatRepository compilerHeartbeatRepository,
                            final CompilerTaskRepository compilerTaskRepository,
                            final CompilerTaskResultRepository compilerTaskResultRepository) {
    super(languageRepository, taskService, tasksProgressService, compilerRepository, compilerHeartbeatRepository,
      compilerTaskRepository, compilerTaskResultRepository);
  }
}
