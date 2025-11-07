package devdojo.compiler.security;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ForbiddenFiles {

  private ForbiddenFiles() {
    // Prevent instantiation
  }

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
  private final static String FILE_EXTENSION_PYTHON = ".py";
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

  // --- Linux paths ---
  private final static String FOLDER_ETC = "/etc";
  private final static String FOLDER_BIN = "/bin";
  private final static String FOLDER_SBIN = "/sbin";
  private final static String FOLDER_USR = "/usr";
  private final static String FOLDER_LIB = "/lib";
  private final static String FOLDER_BOOT = "/boot";
  private final static String FOLDER_DEV = "/dev";
  private final static String FOLDER_PROC = "/proc";
  private final static String FOLDER_SYS = "/sys";
  private final static String FOLDER_ROOT = "/root";
  private final static String FOLDER_RUN = "/run";
  private final static String FOLDER_VAR = "/var";
  private final static String FOLDER_MNT = "/mnt";
  private final static String FOLDER_MEDIA = "/media";
  private final static String FOLDER_SRV = "/srv";
  private final static String FOLDER_OPT = "/opt";
  private final static String FOLDER_TMP = "/tmp";
  private final static String FOLDER_HOME = "/home";

  // --- Windows paths ---
  private final static String FOLDER_C = Paths.get("C:", "\\").toString();
  private final static String FOLDER_WINDOWS = Paths.get("C:", "Windows").toString();
  private final static String FOLDER_PROGRAM_FILES = Paths.get("C:", "Program Files").toString();
  private final static String FOLDER_SYSTEM_32 = Paths.get("C:", "Windows", "System32").toString();
  private final static String FOLDER_USERS = Paths.get("C:", "Users").toString();
  private final static String FOLDER_WIN_TMP = "%TMP%";
  private final static String FOLDER_WIN_TMP_2 = "%TEMP%";

  private final static String USERPROFILE = "%USERPROFILE%";
  private final static String APP_DATA = "AppData";
  private final static String PROGRAM_DATA = "%ProgramData%";


  public final static Set<String> FORBIDDEN_FILE_ENDINGS = Collections.unmodifiableSet(
    Stream.of(
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
        FILE_EXTENSION_PYTHON,
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
        FOLDER_ETC,
        FOLDER_BIN,
        FOLDER_SBIN,
        FOLDER_USR,
        FOLDER_LIB,
        FOLDER_BOOT,
        FOLDER_DEV,
        FOLDER_PROC,
        FOLDER_SYS,
        FOLDER_ROOT,
        FOLDER_RUN,
        FOLDER_VAR,
        FOLDER_MNT,
        FOLDER_MEDIA,
        FOLDER_SRV,
        FOLDER_OPT,
        FOLDER_TMP,
        FOLDER_HOME,
        FOLDER_C,
        FOLDER_WINDOWS,
        FOLDER_PROGRAM_FILES,
        FOLDER_SYSTEM_32,
        FOLDER_USERS,
        FOLDER_WIN_TMP,
        FOLDER_WIN_TMP_2,
        USERPROFILE,
        APP_DATA,
        PROGRAM_DATA
      ).map(String::toLowerCase)
      .collect(Collectors.toSet()));
}
