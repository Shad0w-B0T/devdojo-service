package devdojo.logic.scheduler;

import devdojo.logic.compiler.ICompilerService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public abstract class AbstractCompilerScheduler implements Runnable {

  private final ICompilerService compilerService;

  protected AbstractCompilerScheduler(final ICompilerService compilerService) {
    this.compilerService = compilerService;
  }

  @Scheduled(cron = "0 0 3 * * *")
  @SchedulerLock(name = "cleanupCompiler", lockAtMostFor = "10m", lockAtLeastFor = "1m")
  @Transactional
  @Override
  public void run() {
    log.info("Running compiler cleanup...");
    this.compilerService.cleanup();
    log.info("Compiler cleanup complete.");
  }
}
