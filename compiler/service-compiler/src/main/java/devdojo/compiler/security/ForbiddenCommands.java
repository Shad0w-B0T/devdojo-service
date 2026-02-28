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
  private final static String CHANGE_OWN = "chown";
  private final static String UNICODE = "\\u";
  private final static String RELATIVE_PATH = "./";
  private final static String RELATIVE_PATH_2 = "../";
  private final static String PATH_TRAVERSAL_1 = "/../";
  private final static String PATH_TRAVERSAL_2 = "\\..\\";

  private final static String WGET = "wget";
  private final static String CURL = "curl";
  private final static String NETCAT = "nc -e";
  private final static String NETCAT_LISTENER = "nc -l";
  private final static String BASH = "bash";
  private final static String BASH_C = "bash -c";
  private final static String NODE = "node -e";


  public final static Set<String> FORBIDDEN_COMMANDS = Stream.of(
      REMOVE,
      PYTHON_COMMAND,
      CHANGE_MOD,
      CHANGE_OWN,
      UNICODE,
      RELATIVE_PATH,
      RELATIVE_PATH_2,
      PATH_TRAVERSAL_1,
      PATH_TRAVERSAL_2,
      WGET,
      CURL,
      NETCAT,
      NETCAT_LISTENER,
      BASH,
      BASH_C,
      NODE
    ).map(String::toLowerCase)
    .collect(Collectors.toUnmodifiableSet());

}
