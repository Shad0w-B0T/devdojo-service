
package devdojo.logic.security.ratelimit;

import io.lettuce.core.codec.RedisCodec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * A custom RedisCodec implementation that encodes keys as UTF-8 strings
 * and values as raw byte arrays.
 * <p>
 * This codec is useful when working with Redis clients like Lettuce,
 * allowing string-based keys and binary-safe value encoding.
 */
final class StringByteArrayCodec implements RedisCodec<String, byte[]> {
  /**
   * Decodes the key from a {@link ByteBuffer} to a {@link String} using UTF-8 encoding.
   *
   * @param bytes the byte buffer representing the key
   * @return the decoded key as a string
   */
  @Override
  public String decodeKey(final ByteBuffer bytes) {
    return StandardCharsets.UTF_8.decode(bytes).toString();
  }

  /**
   * Decodes the value from a {@link ByteBuffer} to a byte array.
   *
   * @param bytes the byte buffer representing the value
   * @return the decoded value as a byte array
   */
  @Override
  public byte[] decodeValue(final ByteBuffer bytes) {
    byte[] array = new byte[bytes.remaining()];
    bytes.get(array);
    return array;
  }

  /**
   * Encodes a string key into a {@link ByteBuffer} using UTF-8 encoding.
   *
   * @param key the key to encode
   * @return the encoded key as a byte buffer
   */

  @Override
  public ByteBuffer encodeKey(final String key) {
    return StandardCharsets.UTF_8.encode(key);
  }

  /**
   * Encodes a byte array value into a {@link ByteBuffer}.
   *
   * @param value the value to encode
   * @return the encoded value as a byte buffer
   */
  @Override
  public ByteBuffer encodeValue(final byte[] value) {
    return ByteBuffer.wrap(value);
  }
}
