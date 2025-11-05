package devdojo.schema.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Represents the different roles a user can have within the system.
 * <p>
 * Each role is associated with a {@code permissionScore} (represented by {@link #id}),
 * which determines access rights and privileges. Higher scores typically indicate
 * higher levels of access.
 * <p>
 * Available roles:
 * <ul>
 *   <li>{@link #GUEST} – Minimal access, typically unauthenticated users</li>
 *   <li>{@link #USER} – Basic authenticated user</li>
 *   <li>{@link #STUDENT} – A user with student permissions</li>
 *   <li>{@link #PROFESSOR} – Elevated privileges for teaching staff</li>
 *   <li>{@link #ADMIN} – Full administrative access</li>
 *   <li>{@link #SYSTEM} – Reserved for system-level operations</li>
 * </ul>
 */
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum UserRole implements devdojo.schema.entity.Identifiable<Integer> {
  /**
   * Guest role with the lowest level of access.
   */
  GUEST(0, true),

  /**
   * Regular authenticated user.
   */
  USER(1, true),

  /**
   * Role representing a student, with extended access.
   */
  STUDENT(5, true),

  /**
   * Role for professors, usually with elevated permissions for course management.
   */
  PROFESSOR(10, true),

  /**
   * Administrator role with full access to the system.
   */
  ADMIN(100, true),

  /**
   * Special internal system role not assignable to actual users.
   */
  SYSTEM(999, false);

  /**
   * Numeric value representing the permission level of the role.
   * Can be used for comparing access levels programmatically.
   */
  private final Integer id;

  /**
   * Indicates whether the role is a user-assignable role.
   * <p>
   * This can be used to filter out internal or non-user roles (e.g., {@link #SYSTEM}).
   */
  private final boolean userRole;
}
