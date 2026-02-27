package devdojo.compiler;

import devdojo.compiler.heartbeat.IHeartbeatProcess;
import devdojo.compiler.security.ISecurityFilter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
@Accessors(fluent = true)
public final class CompilerContext {

  private final static CompilerContext INSTANCE = new CompilerContext();

  public static CompilerContext instance() {
    return INSTANCE;
  }

  private CompilerContext() {
    log.info("CompilerContext initialized.");
  }

  private ICompiler compiler;
  private IHeartbeatProcess heartbeatProcess;
  private List<ISecurityFilter> securityFilters = new ArrayList<>();

  public CompilerContext compiler(final ICompiler compiler) {
    if (this.compiler != null) {
      log.warn("Compiler instance is being replaced. Old={}, New={}",
        this.compiler.getClass().getName(),
        compiler != null ? compiler.getClass().getName() : "null");
    } else {
      log.info("Compiler registered: {}",
        compiler != null ? compiler.getClass().getName() : "null");
    }

    this.compiler = compiler;
    return this;
  }

  public CompilerContext heartbeatProcess(final IHeartbeatProcess heartbeatProcess) {
    if (this.heartbeatProcess != null) {
      log.warn("HeartbeatProcess is being replaced.");
    } else {
      log.info("HeartbeatProcess registered: {}",
        heartbeatProcess != null ? heartbeatProcess.getClass().getName() : "null");
    }

    this.heartbeatProcess = heartbeatProcess;
    return this;
  }

  public CompilerContext addSecurityFilter(final ISecurityFilter securityFilter) {
    if (securityFilter == null) {
      log.warn("Attempted to register null SecurityFilter.");
      return this;
    }

    this.securityFilters.add(securityFilter);

    log.info("SecurityFilter registered: {} (total={})",
      securityFilter.getClass().getName(),
      this.securityFilters.size());

    return this;
  }
}