package devdojo.graphql.controller.task;

import devdojo.logic.task.ITaskService;
import devdojo.logic.task.ITasksProgressService;
import org.springframework.stereotype.Controller;

@Controller
public class TaskController extends AbstractTaskController {

  protected TaskController(final ITaskService taskService,
                           final ITasksProgressService tasksProgressService) {
    super(taskService, tasksProgressService);
  }
}
