package devdojo.schema.compiler;

public interface ICompilerHeartbeat {

  double cpuUsage();

  double memoryUsage();

  long totalTasksProcessed();

}
