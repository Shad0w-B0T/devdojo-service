package devdojo.logic.task;

import devdojo.logic.database.task.TaskProgressRepository;
import devdojo.logic.language.ILanguageService;
import devdojo.logic.user.IUserService;
import org.springframework.stereotype.Service;

@Service
public class TasksProgressService extends AbstractTasksProgressService {

  protected TasksProgressService(final IUserService userService,
                                 final ITaskService taskService,
                                 final ILanguageService languageService,
                                 final TaskProgressRepository taskProgressRepository) {
    super(userService, taskService, languageService, taskProgressRepository);
  }
}
