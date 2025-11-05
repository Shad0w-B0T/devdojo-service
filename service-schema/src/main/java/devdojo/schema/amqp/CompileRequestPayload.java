package devdojo.schema.amqp;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CompileRequestPayload(@NotNull UUID requestId,
                                    @NotNull String methodCode,
                                    @NotNull List<List<Object>> arguments) {
}
