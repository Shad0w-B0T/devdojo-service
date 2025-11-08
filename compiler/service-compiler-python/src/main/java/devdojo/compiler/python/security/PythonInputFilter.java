package devdojo.compiler.python.security;


import static devdojo.compiler.python.security.ForbiddenPythonConstants.PYTHON_FORBIDDEN;
import static devdojo.compiler.security.ForbiddenCommands.FORBIDDEN_COMMANDS;
import static devdojo.compiler.security.ForbiddenFiles.FORBIDDEN_FILE_ENDINGS;
import devdojo.compiler.security.ISecurityFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PythonInputFilter implements ISecurityFilter {

  @Override
  public boolean check(final String methodCode) {

    if (methodCode == null || methodCode.trim().isEmpty()) {
      log.warn("Method code is null.");
      return false;
    }

    final String lowerCaseMethodCode = methodCode.toLowerCase();
    boolean hasForbiddenInput = false;
    final StringBuilder forbiddenInput = new StringBuilder();


    // Python stuff
    for (String keyword : PYTHON_FORBIDDEN) {
      if (lowerCaseMethodCode.contains(keyword)) {
        hasForbiddenInput = true;
        forbiddenInput.append("\"").append(keyword).append("\"").append(", ");
      }
    }

    // File endings
    for (String keyword : FORBIDDEN_FILE_ENDINGS) {
      if (lowerCaseMethodCode.contains(keyword)) {
        hasForbiddenInput = true;
        forbiddenInput.append("\"").append(keyword).append("\"").append(", ");
      }
    }

    // Commands
    for (String keyword : FORBIDDEN_COMMANDS) {
      if (lowerCaseMethodCode.contains(keyword)) {
        hasForbiddenInput = true;
        forbiddenInput.append("\"").append(keyword).append("\"").append(", ");
      }
    }


    if (hasForbiddenInput) {
      log.warn("Forbidden keywords detected: [ {} ] in input:\n'{}'", forbiddenInput, methodCode);
      return false;
    }

    log.info("Valid input code:\n[ {} ].", methodCode);
    return true;
  }
}
