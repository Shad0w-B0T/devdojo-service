package devdojo.schema.task;

import devdojo.schema.entity.Identifiable;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public interface TaskProgress extends Identifiable<UUID> {

  @NotNull(message = "Input cannot be null.")
  String input();

  @NotNull(message = "State cannot be null.")
  TaskProgressState state();

  int score();

}

