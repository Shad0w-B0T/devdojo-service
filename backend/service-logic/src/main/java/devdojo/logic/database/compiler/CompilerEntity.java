package devdojo.logic.database.compiler;

import devdojo.logic.database.AbstractExternalEntity;
import devdojo.logic.database.language.LanguageEntity;
import devdojo.schema.compiler.ICompiler;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "compiler")
@Getter
@Setter
@NoArgsConstructor
@Accessors(fluent = true)
public final class CompilerEntity extends AbstractExternalEntity implements ICompiler {

  @NotNull(message = "Language entity must not be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "language_id", nullable = false)
  private LanguageEntity languageEntity;

  @NotNull(message = "CompilerId name must not be null")
  @Column(name = "compilerId", nullable = false, unique = true, updatable = false)
  private UUID compilerId;

  @NotNull(message = "CpuCores name must not be null")
  @Column(name = "cpuCores", nullable = false, updatable = false)
  private int cpuCores;

  @NotNull(message = "MemoryTotal name must not be null")
  @Column(name = "memoryTotal", nullable = false, updatable = false)
  private double memoryTotal;

  @OneToMany(
    mappedBy = "compilerEntity",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<CompilerHeartbeatEntity> heartbeatEntities = new ArrayList<>();

  public CompilerEntity(final LanguageEntity languageEntity,
                        final UUID compilerId,
                        final int cpuCores,
                        final double memoryTotal) {
    this.languageEntity = languageEntity;
    this.compilerId = compilerId;
    this.cpuCores = cpuCores;
    this.memoryTotal = memoryTotal;
  }

  @Override
  protected String prefix() {
    return "com";
  }
}
