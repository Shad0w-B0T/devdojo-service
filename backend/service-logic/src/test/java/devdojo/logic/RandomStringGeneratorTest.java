package devdojo.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RandomStringGenerator Tests")
final class RandomStringGeneratorTest {

  @Test
  @DisplayName("should generate string with correct length")
  void shouldGenerateStringWithCorrectLength() {
    final int length = 16;
    final String result = RandomStringGenerator.generateRandomString(length);
    assertEquals(length, result.length(), "Generated string should have correct length");
  }

  @Test
  @DisplayName("should generate different strings for multiple calls")
  void shouldGenerateDifferentStringsForMultipleCalls() {
    final String first = RandomStringGenerator.generateRandomString(12);
    final String second = RandomStringGenerator.generateRandomString(12);
    assertNotEquals(first, second, "Consecutive calls should produce different strings");
  }

  @Test
  @DisplayName("should only contain allowed characters")
  void shouldOnlyContainAllowedCharacters() {
    final int length = 32;
    final String result = RandomStringGenerator.generateRandomString(length);
    final String allowed = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for (final char c : result.toCharArray()) {
      assertTrue(allowed.indexOf(c) >= 0, () -> "Character '" + c + "' is not allowed");
    }
  }

  @Test
  @DisplayName("should return empty string when length is zero")
  void shouldReturnEmptyStringWhenLengthIsZero() {
    final String result = RandomStringGenerator.generateRandomString(0);
    assertEquals("", result, "Empty string should be returned for length 0");
  }

  @Test
  @DisplayName("should throw StringIndexOutOfBoundsException if negative length is given")
  void shouldThrowExceptionForNegativeLength() {
    assertThrows(NegativeArraySizeException.class, () -> {
      // SecureRandom doesn't throw, but StringBuilder does for negative size
      RandomStringGenerator.generateRandomString(-5);
    });
  }
}
