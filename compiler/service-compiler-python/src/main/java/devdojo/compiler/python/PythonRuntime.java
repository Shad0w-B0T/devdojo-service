package devdojo.compiler.python;

import devdojo.compiler.CompilerContext;
import devdojo.compiler.CompilerServer;

public final class PythonRuntime {
  public static void main(final String[] args) throws Exception {
    CompilerContext
      .instance()
      .compiler(new PythonCompiler());
    CompilerServer.instance().begin();
  }
}
