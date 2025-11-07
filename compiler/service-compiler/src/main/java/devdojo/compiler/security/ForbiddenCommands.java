package devdojo.compiler.security;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ForbiddenCommands {

  private ForbiddenCommands() {
    // Prevent instantiation
  }

  private final static String REMOVE = "rm -rf";
  private final static String PYTHON_COMMAND = "python -c";
  private final static String CHANGE_MOD = "chmod";
  private final static String UNICODE = "\\u";
  private final static String RELATIVE_PATH = "./";
  private final static String RELATIVE_PATH_2 = "../";
  private final static String PATH_TRAVERSAL = "..";
  private final static String SLASHES = "\\";

  public final static Set<String> FORBIDDEN_COMMANDS = Stream.of(
      REMOVE,
      PYTHON_COMMAND,
      CHANGE_MOD,
      UNICODE,
      RELATIVE_PATH,
      RELATIVE_PATH_2,
      PATH_TRAVERSAL,
      SLASHES
    ).map(String::toLowerCase)
    .collect(Collectors.toUnmodifiableSet());

}
