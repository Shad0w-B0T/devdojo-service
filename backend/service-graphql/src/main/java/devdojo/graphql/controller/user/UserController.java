package devdojo.graphql.controller.user;

import devdojo.logic.user.IUserService;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Controller;

@Controller
public class UserController extends AbstractUserController {

  public UserController(@NotNull(message = "UserService cannot be null.") final IUserService userService) {
    super(userService);
  }
}
