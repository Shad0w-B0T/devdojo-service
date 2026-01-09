package devdojo.schema.tag;


import devdojo.schema.entity.Identifiable;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public interface ITag extends Identifiable<UUID> {

  @NotNull
  String name();

}
