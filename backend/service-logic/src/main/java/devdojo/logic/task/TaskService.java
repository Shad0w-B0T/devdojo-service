package devdojo.logic.task;

import devdojo.logic.database.task.TaskRepository;
import devdojo.logic.language.ILanguageService;
import devdojo.logic.user.IUserService;
import org.springframework.stereotype.Service;

@Service
public class TaskService extends AbstractTaskService {


  protected TaskService(final ILanguageService languageService,
                        final IUserService userService,
                        final TaskRepository taskRepository) {
    super(languageService, userService, taskRepository);
  }
}
