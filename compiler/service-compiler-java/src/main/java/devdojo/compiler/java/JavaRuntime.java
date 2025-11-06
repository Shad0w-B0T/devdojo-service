package devdojo.compiler.java;

import devdojo.compiler.CompilerContext;
import devdojo.compiler.CompilerServer;

public final class JavaRuntime {
  public static void main(final String[] args) throws Exception {
    CompilerContext
      .instance()
      .addSecurityFilter(null)
      .compiler(new JavaCompiler());
    CompilerServer.instance().begin();
  }
}
