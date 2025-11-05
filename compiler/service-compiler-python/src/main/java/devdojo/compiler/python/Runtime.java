package devdojo.compiler.python;

import devdojo.compiler.CompilerContext;
import devdojo.compiler.CompilerServer;

public final class Runtime {
  public static void main(final String[] args) throws Exception {
    CompilerContext
      .instance()
      .compiler(new Compiler());
    CompilerServer.instance().begin();
  }
}
