package devdojo.compiler.javascript;

import devdojo.compiler.CompilerContext;
import devdojo.compiler.CompilerServer;
import devdojo.compiler.javascript.security.ForbiddenJavaScriptInputFilter;

public final class JavaScriptRuntime {
  public static void main(final String[] args) throws Exception {
    CompilerContext
      .instance()
      .addSecurityFilter(new ForbiddenJavaScriptInputFilter())
      .compiler(new JavaScriptCompiler());
    CompilerServer.instance().begin();
  }
}
