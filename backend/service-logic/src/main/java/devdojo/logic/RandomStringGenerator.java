package devdojo.logic;

import java.security.SecureRandom;

public final class RandomStringGenerator {

  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();

  public static String generateRandomString(final int length) {
    final StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      final int rndCharAt = RANDOM.nextInt(CHARACTERS.length());
      final char rndChar = CHARACTERS.charAt(rndCharAt);
      sb.append(rndChar);
    }
    return sb.toString();
  }
}
