package devdojo.logic.task;

import devdojo.logic.database.task.TaskRepository;
import devdojo.logic.language.ILanguageService;
import org.springframework.stereotype.Service;

@Service
public class TaskService extends AbstractTaskService {

  protected TaskService(final ILanguageService languageService,
                        final TaskRepository taskRepository) {
    super(languageService, taskRepository);
  }
}
