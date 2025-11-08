package devdojo.compiler.python.security;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ForbiddenPythonConstants {

  private ForbiddenPythonConstants() {
    // Prevent instantiation
  }


  private static final String EVAL = "eval(";
  private static final String EXEC = "exec(";
  private static final String EXECFILE = "execfile(";
  private static final String COMPILE = "compile(";
  private static final String OPEN = "open(";
  private static final String INPUT = "input(";
  private static final String IMPORT = "import ";
  private static final String IMPORT_2 = "__import__(";
  private static final String BUILTINS = "__builtins__";


  private static final String GETATTR = "getattr(";
  private static final String SETATTR = "setattr(";
  private static final String DELATTR = "delattr(";
  private static final String LOCALS = "locals(";
  private static final String GLOBALS = "globals(";
  private static final String VARS = "vars(";
  private static final String DIR = "dir(";


  private static final String OS = "os";
  private static final String SYS = "sys";
  private static final String PLATFORM = "platform";
  private static final String PATHLIB = "pathlib";
  private static final String SUBPROCESS = "subprocess";
  private static final String SOCKET = "socket";
  private static final String SHUTIL = "shutil";
  private static final String SIGNAL = "signal";
  private static final String RESOURCE = "resource";


  private static final String IMPORTLIB = "importlib";
  private static final String IMP = "imp";
  private static final String MARSHAL = "marshal";
  private static final String PICKLE = "pickle";
  private static final String SHELL = "shell";
  private static final String POPEN = "popen";
  private static final String SYSTEM = "system(";
  private static final String RUN = "run(";
  private static final String CALL = "call(";


  private static final String REQUESTS = "requests";
  private static final String HTTP = "http";
  private static final String HTTPS = "https";
  private static final String URLLIB = "urllib";
  private static final String FTP = "ftplib";
  private static final String TELNET = "telnetlib";


  private static final String WHILE_TRUE = "while True";
  private static final String FOR_LOOP_INFINITE = "for(;;)";


  public static final Set<String> PYTHON_FORBIDDEN = Stream.of(

      EVAL,
      EXEC,
      EXECFILE,
      COMPILE,
      OPEN,
      INPUT,
      IMPORT,
      IMPORT_2,
      BUILTINS,
      GETATTR,
      SETATTR,
      DELATTR,
      LOCALS,
      GLOBALS,
      VARS,
      DIR,
      OS,
      SYS,
      PLATFORM,
      PATHLIB,
      SUBPROCESS,
      SOCKET,
      SHUTIL,
      SIGNAL,
      RESOURCE,
      IMPORTLIB,
      IMP,
      MARSHAL,
      PICKLE,
      SHELL,
      POPEN,
      SYSTEM,
      RUN,
      CALL,
      REQUESTS,
      HTTP,
      HTTPS,
      URLLIB,
      FTP,
      TELNET,
      WHILE_TRUE,
      FOR_LOOP_INFINITE
    )
    .map(String::toLowerCase)
    .collect(Collectors.toUnmodifiableSet());
}