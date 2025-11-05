package devdojo.compiler;

import devdojo.schema.code.TestRunResult;
import java.util.List;

public interface ICompiler {

  List<TestRunResult> compileAndRun(final String methodCode,
                                    final Object[][] arguments) throws Exception;

}
