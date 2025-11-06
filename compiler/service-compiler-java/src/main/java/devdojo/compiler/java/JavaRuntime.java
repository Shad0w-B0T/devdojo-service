package devdojo.compiler.java;

import devdojo.compiler.CompilerContext;
import devdojo.compiler.CompilerServer;
import devdojo.compiler.java.security.ForbiddenJavaInputFilter;

public final class JavaRuntime {
  public static void main(final String[] args) throws Exception {
    CompilerContext
      .instance()
      .addSecurityFilter(new ForbiddenJavaInputFilter())
      .compiler(new JavaCompiler());
    CompilerServer.instance().begin();
  }
}
