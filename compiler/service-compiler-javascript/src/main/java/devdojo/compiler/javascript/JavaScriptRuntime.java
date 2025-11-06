package devdojo.compiler.javascript;

import devdojo.compiler.CompilerContext;
import devdojo.compiler.CompilerServer;

public final class JavaScriptRuntime {
  public static void main(final String[] args) throws Exception {
    CompilerContext
      .instance()
      .compiler(new JavaScriptCompiler());
    CompilerServer.instance().begin();
  }
}
