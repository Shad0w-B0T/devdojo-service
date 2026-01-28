package devdojo.logic.database;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

final class ExternalIdGeneratorTest {

  @Test
  @DisplayName("Generate - Returns external id with prefix and 64 character hash")
  void generateValidExternalId() {
    final String prefix = "task";

    final String externalId = ExternalIdGenerator.generate(prefix);

    assertNotNull(externalId);
    assertTrue(externalId.startsWith(prefix + "_"));

    final String hashPart = externalId.substring(prefix.length() + 1);
    assertEquals(64, hashPart.length());
    assertTrue(hashPart.matches("[a-f0-9]{64}"));
  }

  @Test
  @DisplayName("Generate - Generates different ids for multiple calls")
  void generateDifferentIdsForMultipleCalls() {
    final String prefix = "task";

    final Set<String> ids = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      ids.add(ExternalIdGenerator.generate(prefix));
    }

    assertEquals(10, ids.size());
  }

  @Test
  @DisplayName("Generate - Keeps prefix unchanged")
  void generateKeepsPrefix() {
    final String prefix = "user";

    final String externalId = ExternalIdGenerator.generate(prefix);

    assertTrue(externalId.startsWith("user_"));
  }

  @Test
  @DisplayName("Generate - Throws exception when prefix is null")
  void generateThrowsExceptionWhenPrefixIsNull() {
    final IllegalArgumentException exception =
      assertThrows(IllegalArgumentException.class, () ->
        ExternalIdGenerator.generate(null)
      );

    assertEquals("prefix must not be null or blank", exception.getMessage());
  }

  @Test
  @DisplayName("Generate - Throws exception when prefix is empty")
  void generateThrowsExceptionWhenPrefixIsEmpty() {
    final IllegalArgumentException exception =
      assertThrows(IllegalArgumentException.class, () ->
        ExternalIdGenerator.generate("")
      );

    assertEquals("prefix must not be null or blank", exception.getMessage());
  }

  @Test
  @DisplayName("Generate - Throws exception when prefix is blank")
  void generateThrowsExceptionWhenPrefixIsBlank() {
    final IllegalArgumentException exception =
      assertThrows(IllegalArgumentException.class, () ->
        ExternalIdGenerator.generate("   ")
      );

    assertEquals("prefix must not be null or blank", exception.getMessage());
  }
}
