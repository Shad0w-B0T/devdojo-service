package devdojo.logic.compiler;

import com.fasterxml.jackson.databind.ObjectMapper;
import devdojo.logic.cache.compiler.CompilerTaskRepository;
import devdojo.logic.cache.compiler.CompilerTaskResultRepository;
import devdojo.logic.database.compiler.CompilerHeartbeatRepository;
import devdojo.logic.database.compiler.CompilerRepository;
import devdojo.logic.database.language.LanguageRepository;
import devdojo.logic.language.ILanguageService;
import devdojo.logic.task.ITaskService;
import devdojo.logic.task.ITasksProgressService;
import devdojo.logic.user.IUserService;
import devdojo.logic.websocket.ISocketPublisher;
import org.springframework.stereotype.Service;

@Service
public class CompilerService extends AbstractCompilerService {


  protected CompilerService(final ObjectMapper objectMapper,
                            final ILanguageService languageService,
                            final ITaskService taskService,
                            final ITasksProgressService tasksProgressService,
                            final CompilerRepository compilerRepository,
                            final CompilerHeartbeatRepository compilerHeartbeatRepository,
                            final CompilerTaskRepository compilerTaskRepository,
                            final CompilerTaskResultRepository compilerTaskResultRepository,
                            final ISocketPublisher socketPublisher,
                            final IUserService userService) {
    super(objectMapper, languageService, taskService, tasksProgressService, compilerRepository,
      compilerHeartbeatRepository, compilerTaskRepository, compilerTaskResultRepository,
      socketPublisher, userService);
  }
}
