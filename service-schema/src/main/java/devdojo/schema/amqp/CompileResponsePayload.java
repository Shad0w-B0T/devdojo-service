package devdojo.schema.amqp;

import devdojo.schema.code.TestRunResult;
import java.util.List;
import java.util.UUID;

public record CompileResponsePayload(UUID requestId,
                                     List<TestRunResult> testRunResults) {
}
