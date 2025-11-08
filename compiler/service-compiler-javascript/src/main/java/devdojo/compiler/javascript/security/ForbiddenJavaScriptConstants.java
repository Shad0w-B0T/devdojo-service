package devdojo.compiler.javascript.security;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class ForbiddenJavaScriptConstants {

  private ForbiddenJavaScriptConstants() {
    // Prevent instantiation
  }


  private final static String IMPORT = "import";
  private final static String REQUIRE = "require(";
  private final static String MODULE = "module.exports";
  private final static String EXPORTS = "exports.";


  private final static String EVAL = "eval(";
  private final static String FUNCTION = "function(";
  private final static String FUNCTION_CONSTRUCTOR = "new function(";


  private final static String PROCESS = "process";
  private final static String PROCESS_ENV = "process.env";
  private final static String PROCESS_EXIT = "process.exit";
  private final static String PROCESS_KILL = "process.kill";
  private final static String PROCESS_BINDING = "process.binding";


  private final static String CHILD_PROCESS = "child_process";
  private final static String SPAWN = ".spawn(";
  private final static String EXEC = ".exec(";
  private final static String EXEC_SYNC = ".execSync(";
  private final static String SPAWN_SYNC = ".spawnSync(";
  private final static String FORK = ".fork(";


  private final static String FS = "fs";
  private final static String FS_READFILE = "fs.readFile";
  private final static String FS_WRITEFILE = "fs.writeFile";
  private final static String FS_OPEN = "fs.open";
  private final static String FS_UNLINK = "fs.unlink";
  private final static String FS_WRITEFILE_SYNC = "fs.writeFileSync";


  private final static String NET = "net";
  private final static String SOCKET = "socket";
  private final static String CREATE_SERVER = ".createServer(";
  private final static String HTTP = "http";
  private final static String HTTPS = "https";
  private final static String TLS = "tls";
  private final static String DGRAM = "dgram";
  private final static String REQUEST = ".request(";
  private final static String HTTP_GET = ".get(";


  private final static String CHILD_PROCESS_MODULE = "child_process";
  private final static String DOCKER = "docker";
  private final static String NPM = "npm";
  private final static String YARN = "yarn";
  private final static String POPEN = "popen";


  private final static String VM = "vm.";
  private final static String VM_RUNINTHIS = "vm.runInThisContext";
  private final static String VM_RUNINNEW = "vm.runInNewContext";
  private final static String WORKER_THREADS = "worker_threads";
  private final static String WORKER = "worker";


  private final static String GLOBAL = "global";
  private final static String GLOBAL_THIS = "globalThis";
  private final static String BUFFER = "Buffer";


  private final static String CONSTRUCTOR = ".constructor";
  private final static String PROTOTYPE = "prototype";
  private final static String __PROTO__ = "__proto__";


  private final static String WHILE_TRUE = "while(true)";
  private final static String FOR_LOOP_INFINITE = "for(;;)";


  private final static String WINDOW = "window";
  private final static String DOCUMENT = "document";
  private final static String XMLHTTPREQUEST = "XMLHttpRequest";


  private final static String DENO = "Deno";
  private final static String RAPTOR = "deno.";

  public final static Set<String> JAVASCRIPT_FORBIDDEN = Stream.of(
      IMPORT,
      REQUIRE,
      MODULE,
      EXPORTS,
      EVAL,
      FUNCTION_CONSTRUCTOR,
      FUNCTION,
      PROCESS,
      PROCESS_ENV,
      PROCESS_EXIT,
      PROCESS_KILL,
      PROCESS_BINDING,
      CHILD_PROCESS,
      SPAWN,
      EXEC,
      EXEC_SYNC,
      SPAWN_SYNC,
      FORK,
      FS,
      FS_READFILE,
      FS_WRITEFILE,
      FS_OPEN,
      FS_UNLINK,
      FS_WRITEFILE_SYNC,
      NET,
      SOCKET,
      CREATE_SERVER,
      HTTP,
      HTTPS,
      TLS,
      DGRAM,
      REQUEST,
      HTTP_GET,
      DOCKER,
      CHILD_PROCESS_MODULE,
      NPM,
      YARN,
      POPEN,
      VM,
      VM_RUNINTHIS,
      VM_RUNINNEW,
      WORKER_THREADS,
      WORKER,
      GLOBAL,
      GLOBAL_THIS,
      BUFFER,
      CONSTRUCTOR,
      PROTOTYPE,
      __PROTO__,
      WHILE_TRUE,
      FOR_LOOP_INFINITE,
      WINDOW,
      DOCUMENT,
      XMLHTTPREQUEST,
      DENO,
      RAPTOR
    ).map(String::toLowerCase)
    .collect(Collectors.toUnmodifiableSet());
}
