package devdojo.schema.task;

import devdojo.schema.entity.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public enum Difficulty implements Identifiable<Integer> {
  EASY(0, 1),
  MEDIUM(1, 3),
  HARD(2, 5);

  private final Integer id;
  private final int score;

}
