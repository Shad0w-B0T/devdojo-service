package devdojo.logic.task;

import devdojo.logic.database.task.TaskRatingRepository;
import devdojo.logic.database.task.TaskRepository;
import devdojo.logic.language.ILanguageService;
import devdojo.logic.user.IUserService;
import org.springframework.stereotype.Service;

@Service
public class TaskService extends AbstractTaskService {


  protected TaskService(final ILanguageService languageService,
                        final IUserService userService,
                        final TaskRepository taskRepository,
                        final TaskRatingRepository ratingRepository) {
    super(languageService, userService, taskRepository, ratingRepository);
  }
}
