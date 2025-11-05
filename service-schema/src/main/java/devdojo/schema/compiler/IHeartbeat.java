package devdojo.schema.compiler;

import devdojo.schema.entity.Identifiable;
import java.util.UUID;

public interface IHeartbeat extends Identifiable<UUID> {

  String languageToken();

  double cpuUsagePercent();

  int cpuCores();

  double memoryUsage();

  double memoryTotal();

  long totalTasksProcessed();

}
