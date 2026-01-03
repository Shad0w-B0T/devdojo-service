package devdojo.logic.security.ldap;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.naming.directory.Attributes;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.validation.annotation.Validated;

@Validated
public abstract class AbstractLdapService implements ILdapService {

  private static final String USERS_BASE_DN = "ou=userlist";

  private final LdapTemplate ldapTemplate;

  protected AbstractLdapService(
    @NotNull(message = "LdapTemplate must not be null") final LdapTemplate ldapTemplate
  ) {
    this.ldapTemplate = ldapTemplate;
  }

  @Cacheable(
    cacheNames = "ldapUserAttributes",
    key = "#p0",
    unless = "#result == null"
  )
  public Map<String, Object> loadUserAttributes(final String uid) {
    final List<Map<String, Object>> results = ldapTemplate.search(
      USERS_BASE_DN,
      "(uid=" + uid + ")",
      userAttributesMapper()
    );

    return results.stream()
      .findFirst()
      .orElseThrow(() ->
        new IllegalStateException(
          "Kein LDAP-User mit uid=" + uid + " gefunden"
        )
      );
  }

  private AttributesMapper<Map<String, Object>> userAttributesMapper() {
    return attrs -> Map.of(
      "displayName", getSingle(attrs, "displayName"),
      "mail", getSingle(attrs, "mail"),
      "eduPersonAffiliation", getMulti(attrs, "eduPersonAffiliation"),
      "employeeType", getSingle(attrs, "employeeType"),
      "givenName", getSingle(attrs, "givenName"),
      "sn", getSingle(attrs, "sn")
    );
  }

  private String getSingle(final Attributes attrs, final String name) {
    try {
      return Optional.ofNullable(attrs.get(name))
        .map(attr -> {
          try {
            return attr.get().toString();
          } catch (Exception e) {
            return null;
          }
        })
        .orElse(null);
    } catch (Exception e) {
      return null;
    }
  }

  private List<String> getMulti(final Attributes attrs, final String name) {
    try {
      final var attr = attrs.get(name);
      if (attr == null) {
        return List.of();
      }
      return Collections.list(attr.getAll())
        .stream()
        .map(Object::toString)
        .toList();
    } catch (Exception e) {
      return List.of();
    }
  }
}
