package devdojo.schema.code;

import jakarta.validation.constraints.NotNull;
import java.io.*;
import java.util.List;
import java.util.UUID;

public record TestRun(@NotNull UUID id,
                      @NotNull List<TestRunResultChecked> results,
                      boolean taskComplete,
                      int taskScore) implements Serializable {
}
