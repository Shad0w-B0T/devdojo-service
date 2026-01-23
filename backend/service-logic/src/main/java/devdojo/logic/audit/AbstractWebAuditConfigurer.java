package devdojo.logic.audit;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

public abstract class AbstractWebAuditConfigurer implements IWebAuditConfigurer {

  private final @NotNull IWebAudit webAudit;

  protected AbstractWebAuditConfigurer(final IWebAudit webAudit) {
    Objects.requireNonNull(webAudit, "Webaudit is required, to log web connections.");

    this.webAudit = webAudit;
  }

  @Override
  public void addInterceptors(@NotNull final InterceptorRegistry registry) {
    registry.addInterceptor(this.webAudit).excludePathPatterns("/ws/**");
  }
}
