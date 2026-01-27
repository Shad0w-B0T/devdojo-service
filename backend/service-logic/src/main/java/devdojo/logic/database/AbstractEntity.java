package devdojo.logic.database;

import devdojo.schema.entity.DateAuditable;
import devdojo.schema.entity.Identifiable;
import jakarta.persistence.*;
import java.io.*;
import java.util.Date;
import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Abstract base class for entities, providing a unique identifier and timestamp fields.
 * This class implements common properties and methods shared by all entities extending it.
 *
 * <p>
 * This class includes:
 * <ul>
 *     <li>An automatically generated ID</li>
 *     <li>Automatic timestamps for creation and updating</li>
 *     <li>Pre-hook methods for persistence, updating, and removal</li>
 * </ul>
 */
@Data
@Getter
@Accessors(fluent = true)
@MappedSuperclass
@Slf4j
public abstract class AbstractEntity implements Identifiable<UUID>, DateAuditable, Serializable {
  /**
   * The unique identifier for the entity.
   * Automatically generated using the identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /**
   * The timestamp when the entity was created.
   * This field is not updatable.
   */
  @Column(name = "createdAt", updatable = false)
  @CreationTimestamp
  private Date createdAt;

  /**
   * The timestamp when the entity was last updated.
   * Automatically updated with each update to the entity.
   */
  @Column(name = "updatedAt")
  @UpdateTimestamp
  private Date updatedAt;

  /**
   * Default constructor for {@code AbstractEntity}.
   * <p>
   * The constructor is protected to restrict instantiation to subclasses within the same package or classes
   * extending {@code AbstractEntity}.
   * <p>
   * Subclasses can provide their own constructors or additional initialization as needed.
   */
  protected AbstractEntity() {
    // Default constructor
  }

  /**
   * Method invoked before persisting the entity.
   * Ensures that the creation and update timestamps are set.
   */
  @PrePersist
  protected void prePersist() {
    //Set created if not present
    if (this.createdAt == null) {
      this.createdAt = new Date();
    }
    //Set updated if not present
    if (this.updatedAt == null) {
      this.updatedAt = new Date();
    }

    this.handlePrePersist();
    log.debug("Created entity={} with id={}", this.getClass().getSimpleName(), this.id);
  }

  protected void handlePrePersist() {
  }

  /**
   * Method invoked before updating the entity.
   * Ensures that the update timestamp is set.
   */
  @PreUpdate
  protected void preUpdate() {
    this.updatedAt = new Date();

    log.debug("Updated entity={} with id={}.", this.getClass().getSimpleName(), this.id);
  }

  /**
   * Method invoked before removing the entity.
   * Ensures that the update timestamp is set.
   */
  @PreRemove
  protected void preRemove() {
    this.updatedAt = new Date();

    log.debug("Removed entity={} with id={}.", this.getClass().getSimpleName(), this.id);
  }
}
