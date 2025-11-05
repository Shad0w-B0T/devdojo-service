package devdojo.schema.compiler;

import devdojo.schema.entity.Identifiable;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public interface ICompiler extends Identifiable<UUID> {

  @NotNull
  UUID compilerId();

  int cpuCores();

  double memoryTotal();

}
