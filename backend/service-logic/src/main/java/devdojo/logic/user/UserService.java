package devdojo.logic.user;

import devdojo.logic.database.user.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractUserService {
  public UserService(@NotNull final UserRepository userRepository) {
    super(userRepository);
  }
}
