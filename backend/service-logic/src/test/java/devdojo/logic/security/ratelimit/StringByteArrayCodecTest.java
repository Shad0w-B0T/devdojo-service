package devdojo.logic.security.ratelimit;

import io.lettuce.core.codec.RedisCodec;
import java.nio.ByteBuffer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StringByteArrayCodec Tests")
class StringByteArrayCodecTest {

  private final RedisCodec<String, byte[]> codec = new StringByteArrayCodec();

  @Test
  @DisplayName("should encode and decode key correctly")
  void shouldEncodeAndDecodeKey() {
    final String originalKey = "my:test:key";
    final ByteBuffer encoded = codec.encodeKey(originalKey);
    final String decoded = codec.decodeKey(encoded);

    assertEquals(originalKey, decoded, "Key should match after encode/decode");
  }

  @Test
  @DisplayName("should encode and decode byte array value correctly")
  void shouldEncodeAndDecodeValue() {
    final byte[] originalValue = new byte[]{1, 2, 3, 4, 5};
    final ByteBuffer encoded = codec.encodeValue(originalValue);
    final byte[] decoded = codec.decodeValue(encoded);

    assertArrayEquals(originalValue, decoded, "Value should match after encode/decode");
  }

  @Test
  @DisplayName("should preserve UTF-8 characters in key encoding")
  void shouldHandleUtf8CharactersInKey() {
    final String key = "äöüß";
    final ByteBuffer buffer = codec.encodeKey(key);
    final String decoded = codec.decodeKey(buffer);

    assertEquals(key, decoded, "Encoded and decoded UTF-8 key should match");
  }

  @Test
  @DisplayName("should decode empty buffer to empty byte array")
  void shouldHandleEmptyValueBuffer() {
    final ByteBuffer emptyBuffer = ByteBuffer.wrap(new byte[0]);
    final byte[] result = codec.decodeValue(emptyBuffer);

    assertEquals(0, result.length, "Decoded value should be empty");
  }
}
