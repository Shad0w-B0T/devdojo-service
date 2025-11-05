package devdojo.logic.scheduler;

import javax.sql.DataSource;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractSchedulerConfig {
  @Bean
  public LockProvider lockProvider(final DataSource dataSource) {
    return new JdbcTemplateLockProvider(new JdbcTemplate(dataSource));
  }
}
