package devdojo.logic.security.web;

import devdojo.logic.security.supabase.ISupabaseEvaluator;
import devdojo.logic.user.IUserService;
import org.springframework.stereotype.Service;

@Service
public class WebFilter extends AbstractWebFilter {
  public WebFilter(final ISupabaseEvaluator supabaseEvaluator,
                   final IUserService userService) {
    super(supabaseEvaluator, userService);
  }
}
