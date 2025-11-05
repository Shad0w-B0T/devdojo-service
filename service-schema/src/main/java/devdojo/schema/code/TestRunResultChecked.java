package devdojo.schema.code;

import java.io.*;
import java.util.List;

public record TestRunResultChecked(List<Object> input,
                                   Object output,
                                   List<String> logs,
                                   List<TestError> errors,
                                   long compileTimeMs,
                                   long runTimeMs,
                                   boolean pass
) implements Serializable {
}
