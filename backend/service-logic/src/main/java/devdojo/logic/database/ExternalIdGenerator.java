package devdojo.logic.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public final class ExternalIdGenerator {

  private ExternalIdGenerator() {
  }

  public static String generate(final String prefix) {
    if (prefix == null || prefix.isBlank()) {
      throw new IllegalArgumentException("prefix must not be null or blank");
    }

    final String raw = UUID.randomUUID().toString();

    return prefix + "_" + sha256(raw);
  }

  private static String sha256(final String input) {
    try {
      final MessageDigest digest = MessageDigest.getInstance("SHA-256");
      final byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

      final StringBuilder hex = new StringBuilder(64);
      for (byte b : hash) {
        hex.append(String.format("%02x", b));
      }
      return hex.toString();
    } catch (final NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 not available", exception);
    }
  }
}
