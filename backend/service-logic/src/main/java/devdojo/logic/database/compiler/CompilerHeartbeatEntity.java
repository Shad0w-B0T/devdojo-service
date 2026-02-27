package devdojo.logic.database.compiler;

import devdojo.logic.database.AbstractEntity;
import devdojo.schema.compiler.ICompilerHeartbeat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "compilerHeartbeat")
@Getter
@Setter
@NoArgsConstructor
@Accessors(fluent = true)
public class CompilerHeartbeatEntity extends AbstractEntity implements ICompilerHeartbeat {

  @NotNull(message = "Compiler entity must not be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "compiler_id", nullable = false)
  private CompilerEntity compilerEntity;

  @NotNull(message = "CpuUsage name must not be null")
  @Column(name = "cpuUsage", nullable = false, updatable = false)
  private double cpuUsage;

  @NotNull(message = "MemoryUsage name must not be null")
  @Column(name = "memoryUsage", nullable = false, updatable = false)
  private double memoryUsage;

  @NotNull(message = "TotalTasksProcessed name must not be null")
  @Column(name = "totalTasksProcessed", nullable = false, updatable = false)
  private long totalTasksProcessed;

  public CompilerHeartbeatEntity(final CompilerEntity compilerEntity,
                                 final double memoryUsage,
                                 final double cpuUsage,
                                 final long totalTasksProcessed) {
    this.compilerEntity = compilerEntity;
    this.memoryUsage = memoryUsage;
    this.cpuUsage = cpuUsage;
    this.totalTasksProcessed = totalTasksProcessed;
  }
}
