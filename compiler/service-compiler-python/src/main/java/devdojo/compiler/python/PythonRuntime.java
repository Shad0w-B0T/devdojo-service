package devdojo.compiler.python;

import devdojo.compiler.CompilerContext;
import devdojo.compiler.CompilerServer;
import devdojo.compiler.python.security.PythonInputFilter;

public final class PythonRuntime {
  public static void main(final String[] args) throws Exception {
    CompilerContext
      .instance()
      .addSecurityFilter(new PythonInputFilter())
      .compiler(new PythonCompiler());
    CompilerServer.instance().begin();
  }
}
