package devdojo.logic.audit;

import org.springframework.stereotype.Service;

@Service
public class WebAuditConfigurer extends AbstractWebAuditConfigurer {
  public WebAuditConfigurer(final IWebAudit webAudit) {
    super(webAudit);
  }
}
