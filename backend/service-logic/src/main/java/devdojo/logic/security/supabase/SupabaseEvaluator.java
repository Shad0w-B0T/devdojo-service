package devdojo.logic.security.supabase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SupabaseEvaluator extends AbstractSupabaseEvaluator {

  public SupabaseEvaluator(@Value("${application.security.supabase.algorithm}") final String algorithm,
                           @Value("${application.security.supabase.secret}") final String secret) {
    super(algorithm, secret);
  }
}
