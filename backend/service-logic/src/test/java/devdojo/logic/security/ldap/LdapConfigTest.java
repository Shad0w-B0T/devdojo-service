package devdojo.logic.security.ldap;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AbstractLdapConfig Unit Tests")
final class LdapConfigTest {

  private static final String LDAP_URL = "ldaps://ldap.example.org:636";
  private static final String LDAP_BASE = "dc=example,dc=org";
  private static final String LDAP_PATTERN = "uid={0},ou=users";

  private TestLdapConfig config;
  private Validator validator;

  @BeforeEach
  void setUp() {
    this.config = new TestLdapConfig(LDAP_URL, LDAP_BASE, LDAP_PATTERN);

    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    this.validator = factory.getValidator();
  }

  @Test
  @DisplayName("should create LdapContextSource with correct url and base")
  void shouldCreateLdapContextSource() {
    final LdapContextSource contextSource = this.config.ldapContextSource();

    assertNotNull(contextSource);
    assertEquals(LDAP_URL, contextSource.getUrls()[0]);
    assertEquals(LDAP_BASE, contextSource.getBaseLdapName().toString());
  }

  @Test
  @DisplayName("should create LdapTemplate from context source")
  void shouldCreateLdapTemplate() {
    final LdapContextSource contextSource = this.config.ldapContextSource();

    final LdapTemplate ldapTemplate = this.config.ldapTemplate(contextSource);

    assertNotNull(ldapTemplate);
  }

  @Test
  @DisplayName("should create AuthenticationProvider")
  void shouldCreateAuthenticationProvider() {
    final AuthenticationProvider provider = this.config.ldapAuthenticationProvider();

    assertNotNull(provider);
  }

  /**
   * Test-Implementierung der abstrakten Klasse
   */
  private static final class TestLdapConfig extends AbstractLdapConfig {

    protected TestLdapConfig(
      final String ldapUrl,
      final String ldapBase,
      final String ldapUserPattern
    ) {
      super(ldapUrl, ldapBase, ldapUserPattern);
    }
  }
}
