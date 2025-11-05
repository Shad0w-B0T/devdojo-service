package devdojo.compiler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CompilerContextTest - Unit tests for security filter management")
final class CompilerContextTest {

  @Test
  @DisplayName("should add a security filter to the context")
  void shouldAddSecurityFilter() {
    final ISecurityFilter dummyFilter = code -> true;
    final CompilerContext ctx = CompilerContext.instance();

    ctx.addSecurityFilter(dummyFilter);

    assertEquals(1, ctx.securityFilters().size(), "Expected one security filter in the list");
    assertSame(dummyFilter, ctx.securityFilters().get(0), "Expected the added filter to match");
  }
}
