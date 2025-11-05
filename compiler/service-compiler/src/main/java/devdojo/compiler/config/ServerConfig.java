package devdojo.compiler.config;

import devdojo.schema.Constants;
import java.util.Optional;

public final class ServerConfig {

  public String token() {
    return get("COMPILER_TOKEN",
      "4unl6q0Z1bkKKDde35OaVxJwPnf4ze21VeYPZGgJkXKvnIp4w31oy52r1gxFVdDmZlEMIDqAqxBOm5uIYELu8vwVF660jhMJEIjB6lmUUPfS4Z6ckl1unA0NnSFhlSYw");
  }

  public String rabbitHost() {
    return get("RABBIT_HOST", "localhost");
  }

  public int rabbitPort() {
    return getInt("RABBIT_PORT", Constants.RABBIT_MQ_PORT);
  }

  public String rabbitUsername() {
    return get("RABBIT_USERNAME", "user");
  }

  public String rabbitPassword() {
    return get("RABBIT_PASSWORD", "password");
  }

  public String rabbitVirtualHost() {
    return get("RABBIT_VIRTUAL_HOST", "icarus");
  }

  private String get(final String key, final String defaultValue) {
    return Optional.ofNullable(System.getenv(key)).orElse(defaultValue);
  }

  private int getInt(final String key, final int defaultValue) {
    try {
      return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}
