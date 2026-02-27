package devdojo.logic.database;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import java.io.*;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Getter
@Accessors(fluent = true)
@MappedSuperclass
@Slf4j
public abstract class AbstractExternalEntity extends AbstractEntity implements Serializable {

  @Column(name = "externalId", unique = true, updatable = false, nullable = false)
  private String externalId;

  protected abstract @NotNull String prefix();

  @Override
  protected void handlePrePersist() {
    if (this.externalId == null) {
      this.externalId = ExternalIdGenerator.generate(this.prefix());
      log.debug(
        "Generated externalId={} for entity={}",
        this.externalId,
        this.getClass().getSimpleName()
      );
    }
  }
}
