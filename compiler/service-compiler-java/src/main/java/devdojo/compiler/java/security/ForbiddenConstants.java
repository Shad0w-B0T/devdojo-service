package devdojo.compiler.java.security;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ForbiddenConstants {

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

  private final static String REMOVE = "rm -rf";
  private final static String FILE_EXTENSION_EXE = ".exe";
  private final static String FILE_EXTENSION_PDF = ".pdf";
  private final static String FILE_EXTENSION_PDFX = ".pdfx";
  private final static String FILE_EXTENSION_PNG = ".png";
  private final static String FILE_EXTENSION_JPG = ".jpg";
  private final static String FILE_EXTENSION_JPEG = ".jpeg";
  private final static String FILE_EXTENSION_GIF = ".gif";
  private final static String FILE_EXTENSION_SVG = ".svg";
  private final static String FILE_EXTENSION_TXT = ".txt";
  private final static String FILE_EXTENSION_MD = ".md";
  private final static String FILE_EXTENSION_BAT = ".bat";
  private final static String FILE_EXTENSION_CMD = ".cmd";
  private final static String FILE_EXTENSION_JAR = ".jar";
  private final static String FILE_EXTENSION_JAVASCRIPT = ".js";
  private final static String FILE_EXTENSION_JAVA = ".java";
  private final static String FILE_EXTENSION_SHELL = ".sh";
  private final static String FILE_EXTENSION_ZIP = ".zip";
  private final static String FILE_EXTENSION_RAR = ".rar";
  private final static String FILE_EXTENSION_TAR = ".tar";
  private final static String FILE_EXTENSION_7Z = ".7z";
  private final static String FILE_EXTENSION_GZ = ".gz";
  private final static String FILE_EXTENSION_DLL = ".dll";
  private final static String FILE_EXTENSION_MSI = ".msi";
  private final static String FILE_EXTENSION_APK = ".apk";
  private final static String FILE_EXTENSION_PS1 = ".ps1";
  private final static String FILE_EXTENSION_VBS = ".vbs";
  private final static String FILE_EXTENSION_SO = ".so";


  private final static String DANGEROUS_FILES_ETC = "/etc";

  private final static String WHILE_LOOP = "while(true)";
  private final static String FOR_LOOP = "for(;;)";
  private final static String UNICODE = "\\u";
  private final static String COMMENT = "//";
  private final static String COMMENT_MULTILINE = "/*";


  public final static Set<String> FORBIDDEN_SET = Collections.unmodifiableSet(
    Stream.of(
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
        REMOVE,
        FILE_EXTENSION_EXE,
        FILE_EXTENSION_PDF,
        FILE_EXTENSION_PDFX,
        FILE_EXTENSION_PNG,
        FILE_EXTENSION_JPG,
        FILE_EXTENSION_JPEG,
        FILE_EXTENSION_GIF,
        FILE_EXTENSION_SVG,
        FILE_EXTENSION_TXT,
        FILE_EXTENSION_MD,
        FILE_EXTENSION_BAT,
        FILE_EXTENSION_CMD,
        FILE_EXTENSION_JAR,
        FILE_EXTENSION_JAVASCRIPT,
        FILE_EXTENSION_JAVA,
        FILE_EXTENSION_SHELL,
        FILE_EXTENSION_ZIP,
        FILE_EXTENSION_RAR,
        FILE_EXTENSION_TAR,
        FILE_EXTENSION_7Z,
        FILE_EXTENSION_GZ,
        FILE_EXTENSION_DLL,
        FILE_EXTENSION_MSI,
        FILE_EXTENSION_APK,
        FILE_EXTENSION_PS1,
        FILE_EXTENSION_VBS,
        FILE_EXTENSION_SO,
        DANGEROUS_FILES_ETC,
        WHILE_LOOP,
        FOR_LOOP,
        UNICODE,
        COMMENT,
        COMMENT_MULTILINE
      ).map(String::toLowerCase)
      .collect(Collectors.toSet()));

}
