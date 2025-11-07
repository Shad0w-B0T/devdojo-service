package devdojo.compiler.java.security;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ForbiddenJavaConstants {

  private ForbiddenJavaConstants() {
    // Prevent instantiation
  }

  private final static String IMPORT = "import";

  private final static String JAVA_UTIL = "java.util";
  private final static String JAVA_LANG = "java.lang";
  private final static String JAVA_IO = "java.io";
  private final static String JAVA_NIO = "java.nio";
  private final static String JAVA_NET = "java.net";
  private final static String JAVA_RMI = "java.rmi";
  private final static String JAVAX = "javax.";

  private final static String SYSTEM_EXIT = "system.exit";
  private final static String SYSTEM_LOAD = "system.load";
  private final static String SYSTEM_LOAD_LIBRARY = "system.loadlibrary";
  private final static String SYSTEM_SET_SECURITY_MANAGER = "system.setSecurityManager";

  private final static String RUNTIME = "runtime.";

  private final static String THREAD = "thread";
  private final static String THREAD_GROUP = "threadgroup";

  private final static String PROCESS = "process";
  private final static String PROCESS_BUILDER = "processbuilder";

  private final static String SUN_MISC_UNSAFE = "sun.misc.unsafe";

  private final static String CLASSLOADER = "classloader";
  private final static String CLASS_FOR_NAME = "class.forname";

  private final static String GET_DECLARED_METHODS = "getdeclaredmethods";
  private final static String SET_ACCESSIBLE = "setaccessible";

  private final static String SECURITY_MANAGER = "securitymanager";

  private final static String ACCESSCONTROLLER_DO_PRIVILEGED = "accesscontroller.doprivileged";


  private final static String WHILE_LOOP = "while(true)";
  private final static String FOR_LOOP = "for(;;)";

  private final static String COMMENT = "//";
  private final static String COMMENT_MULTILINE = "/*";


  public final static Set<String> JAVA_FORBIDDEN = Stream.of(
      IMPORT,
      JAVA_UTIL,
      JAVA_LANG,
      JAVA_IO,
      JAVA_NIO,
      JAVA_NET,
      JAVA_RMI,
      JAVAX,
      SYSTEM_EXIT,
      SYSTEM_LOAD,
      SYSTEM_LOAD_LIBRARY,
      SYSTEM_SET_SECURITY_MANAGER,
      RUNTIME,
      THREAD,
      THREAD_GROUP,
      PROCESS,
      PROCESS_BUILDER,
      SUN_MISC_UNSAFE,
      CLASSLOADER,
      CLASS_FOR_NAME,
      GET_DECLARED_METHODS,
      SET_ACCESSIBLE,
      SECURITY_MANAGER,
      ACCESSCONTROLLER_DO_PRIVILEGED,
      WHILE_LOOP,
      FOR_LOOP,
      COMMENT,
      COMMENT_MULTILINE
    ).map(String::toLowerCase)
    .collect(Collectors.toUnmodifiableSet());
}
