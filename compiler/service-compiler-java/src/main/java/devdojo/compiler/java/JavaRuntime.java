package devdojo.compiler.java;

import devdojo.compiler.CompilerContext;
import devdojo.compiler.CompilerServer;
import devdojo.compiler.java.security.JavaInputFilter;

public final class JavaRuntime {
  public static void main(final String[] args) throws Exception {
    CompilerContext
      .instance()
      .addSecurityFilter(new JavaInputFilter())
      .compiler(new JavaCompiler());
    CompilerServer.instance().begin();
  }
}
