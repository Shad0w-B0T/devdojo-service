package devdojo.schema.compiler;

public record CompilerResourceAnalytic(long timestamp,
                                       double averageCpu,
                                       double averageMemory) {
}
