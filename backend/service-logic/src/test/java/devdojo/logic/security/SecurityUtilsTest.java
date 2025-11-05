package devdojo.logic.security;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

final class SecurityUtilsTest {

  private final @NotNull String TEST_STRING = "test";
  private final @NotNull String TEST_ELEMENT_STRING = "<p>" + TEST_STRING + "</p>" + TEST_STRING;

  /**
   * Tests the {@link SecurityUtils#owaspHtmlSanitize(String, Supplier)} method.
   * <p>
   * Ensures that HTML sanitization is performed correctly and returns the expected value.
   */
  @Test
  @DisplayName("Should sanitize input and remove unsafe HTML elements")
  void shouldSanitizeHtmlAndRemoveUnsafeElements() {
    assertEquals(TEST_STRING, SecurityUtils.owaspHtmlSanitize(TEST_STRING, null));
    assertEquals(TEST_STRING,
      SecurityUtils.owaspHtmlSanitize("<script>alert('xss');</script>" + TEST_STRING, null));
  }

  /**
   * Tests the {@link SecurityUtils#owaspHtmlSanitize(String, Supplier)} method.
   * <p>
   * Checks that the HTML sanitization works with a custom policy factory supplier.
   */
  @Test
  @DisplayName("Should sanitize input but allow defined HTML elements via custom policy")
  void shouldSanitizeHtmlWithCustomPolicy() {
    final Supplier<PolicyFactory> factorySupplier = () -> new HtmlPolicyBuilder()
      .allowElements("p")
      .toFactory();

    assertEquals(TEST_STRING, SecurityUtils.owaspHtmlSanitize(TEST_STRING, factorySupplier));
    assertEquals(TEST_ELEMENT_STRING, SecurityUtils.owaspHtmlSanitize(TEST_ELEMENT_STRING, factorySupplier));
  }
}
