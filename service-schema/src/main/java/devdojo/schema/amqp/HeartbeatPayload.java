package devdojo.schema.amqp;

public record HeartbeatPayload(String compilerId,
                               String languageKey,
                               double cpuUsage,
                               int cpuCores,
                               double memoryUsageMb,
                               double memoryTotalMb,
                               long totalTasksProcessed) {
}
