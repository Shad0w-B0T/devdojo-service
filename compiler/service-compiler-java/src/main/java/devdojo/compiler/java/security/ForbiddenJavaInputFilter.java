package devdojo.compiler.java.security;


import static devdojo.compiler.java.security.ForbiddenConstants.FORBIDDEN_SET;
import devdojo.compiler.security.ISecurityFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ForbiddenJavaInputFilter implements ISecurityFilter {


  @Override
  public boolean check(final String methodCode) {

    if (methodCode == null) {
      log.warn("Method code is null.");
      throw new NullPointerException("Method code is null.");
    }

    final String lowerCaseMethodCode = methodCode.toLowerCase();
    boolean hasForbidden = false;
    StringBuilder foundForbidden = new StringBuilder();

    for (String keyword : FORBIDDEN_SET) {
      if (lowerCaseMethodCode.contains(keyword)) {
        hasForbidden = true;
        foundForbidden.append(keyword).append(", ");
      }
    }

    if (hasForbidden) {
      log.warn("Forbidden keywords detected: [ {} ] in input:\n'{}'", foundForbidden, methodCode);
      return false;
    }

    log.info("Valid input code:\n[ {} ].",  methodCode);
    return true;
  }
}
