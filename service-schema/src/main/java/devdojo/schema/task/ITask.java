package devdojo.schema.task;

import devdojo.schema.entity.Activatable;
import devdojo.schema.entity.Identifiable;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface ITask extends Identifiable<UUID>, Activatable {

  @NotNull(message = "Name cannot be null.")
  String name();

  @NotNull(message = "Description cannot be null.")
  String description();

  @NotNull(message = "Template cannot be null.")
  String template();

  @NotNull(message = "Difficulty cannot be null.")
  Difficulty difficulty();

  @NotNull(message = "Complete cannot be null.")
  String complete();

  @NotNull(message = "UnitTests cannot be null.")
  List<ITest> unitTests();

}
