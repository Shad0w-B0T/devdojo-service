package devdojo.graphql.controller.user;

import devdojo.logic.user.IUserService;
import devdojo.logic.websocket.service.IPresenceService;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Validated
@Controller
public class UserController extends AbstractUserController {

  public UserController(@NotNull(message = "UserService cannot be null.") final IUserService userService,
                        @NotNull(message = "PresenceService cannot be null.") final IPresenceService presenceService
  ) {
    super(userService, presenceService);
  }
}
