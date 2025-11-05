package devdojo.compiler;

import devdojo.compiler.heartbeat.IHeartbeatProcess;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(fluent = true)
public final class CompilerContext {

  private final static CompilerContext INSTANCE = new CompilerContext();

  public static CompilerContext instance() {
    return INSTANCE;
  }

  private CompilerContext() {

  }

  private ICompiler compiler;
  private IHeartbeatProcess heartbeatProcess;
  private List<ISecurityFilter> securityFilters = new ArrayList<>();

  public CompilerContext addSecurityFilter(final ISecurityFilter securityFilter) {
    this.securityFilters.add(securityFilter);
    return this;
  }
}
