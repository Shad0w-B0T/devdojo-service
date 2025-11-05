package devdojo.logic.security.supabase;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Interface representing a security module responsible for user authentication.
 */
public interface ISupabaseEvaluator {

  @NotNull
  ISupabaseUser authenticate(@NotNull final HttpServletRequest request) throws UsernameNotFoundException;
}
