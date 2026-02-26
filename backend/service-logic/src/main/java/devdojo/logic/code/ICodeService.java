package devdojo.logic.code;

import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.task.TaskEntity;
import devdojo.schema.task.ITest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.util.List;
import java.util.UUID;

public interface ICodeService {

  @NotNull
  UUID executeCode(@NotNull final TaskEntity taskEntity,
                   @NotNull final String methodCode
  );


  @NotNull
  UUID executeCode(@NotNull final LanguageEntity languageEntity,
                   @Null final TaskEntity taskEntity,
                   @NotNull final String methodCode,
                   @NotNull(message = "Unit tests list must not be null") final List<ITest> unitTests
  );
}
