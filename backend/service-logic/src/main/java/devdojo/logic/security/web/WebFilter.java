package devdojo.logic.security.web;

import devdojo.logic.user.IUserService;
import org.springframework.stereotype.Service;

@Service
public class WebFilter extends AbstractWebFilter {
  public WebFilter(final IUserService userService) {
    super(userService);
  }
}
