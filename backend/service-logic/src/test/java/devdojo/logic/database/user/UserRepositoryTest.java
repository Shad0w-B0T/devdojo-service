package devdojo.logic.database.user;

import devdojo.logic.database.BaseJpaTest;
import devdojo.schema.user.UserRole;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserRepository Tests")
final class UserRepositoryTest extends BaseJpaTest {

  @Autowired
  private UserRepository cut;

  private UserEntity user;

  private final Date DATE = Date.from(Instant.parse("2025-04-20T12:00:00Z"));
  private final String ID = "e25a0f7f-6f46-412c-ac86-4f50bc5faed4";


  @BeforeEach
  void beforeEach() {
    this.user = new UserEntity(
      ID,
      "rick@hdm-stuttgart.de",
      "Rick",
      UserRole.ADMIN,
      UserRole.ADMIN,
      true,
      this.DATE,
      false
    );
  }

  @Test
  @DisplayName("ExistsByDisplayName - Returns true because the display name exists")
  void displayNameExists() {
    final UserEntity user = this.user;
    this.cut.save(user);
    final boolean result = this.cut.existsByDisplayName("Rick");
    assertTrue(result);
  }

  @Test
  @DisplayName("ExistsByDisplayName - Returns false display name does not exist")
  void displayNameDoesNotExist() {
    final UserEntity user = this.user;
    this.cut.save(user);

    final boolean result = this.cut.existsByDisplayName("Morty");
    assertFalse(result);
  }


  @Test
  @DisplayName("ExistsByDisplayName - Returns false display name is null")
  void displayNameIsNull() {
    final UserEntity user = this.user;
    this.cut.save(user);

    final boolean result = this.cut.existsByDisplayName(null);
    assertFalse(result);
  }


  @Test
  @DisplayName("FindBySupabaseId - Returns correct user")
  void findUserBySupabaseId() {
    final UserEntity user = this.user;
    this.cut.save(user);

    final Optional<UserEntity> result = this.cut.findByAuthId(ID);

    assertTrue(result.isPresent());
    assertEquals(1, this.cut.count());
    assertEquals(user, result.get());
    assertEquals(user.email(), result.get().email());
    assertEquals(user.displayName(), result.get().displayName());
    assertEquals(user.ldapRole(), result.get().ldapRole());
    assertEquals(user.active(), result.get().active());
    assertEquals(user.lastLogin(), result.get().lastLogin());
  }

  @Test
  @DisplayName("FindBySupabaseId - Returns empty id is null")
  void supabaseIdIsNull() {
    final UserEntity user = this.user;
    this.cut.save(user);

    final Optional<UserEntity> result = this.cut.findByAuthId(null);

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("FindBySupabaseId - Returns empty id does not exist")
  void supabaseIdDoesNotExist() {
    final UserEntity user = this.user;
    this.cut.save(user);

    final Optional<UserEntity> result =
      this.cut.findByAuthId("5fc95d2a-e881-4b30-aeda-714ad45e770b");

    assertTrue(result.isEmpty());
  }
}
