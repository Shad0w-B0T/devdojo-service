package devdojo.logic.security.supabase;

import devdojo.schema.entity.Identifiable;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public interface ISupabaseUser extends Identifiable<UUID> {

  @NotNull String email();

  @NotNull String username();

  boolean aal();

}
