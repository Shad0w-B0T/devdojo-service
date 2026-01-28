package devdojo.graphql.controller.task;

import devdojo.logic.task.ITaskService;
import devdojo.logic.task.ITasksProgressService;
import devdojo.logic.user.IUserService;
import org.springframework.stereotype.Controller;

@Controller
public class TaskController extends AbstractTaskController {

  protected TaskController(final ITaskService taskService,
                           final IUserService userService,
                           final ITasksProgressService tasksProgressService) {
    super(taskService, userService, tasksProgressService);
  }
}
