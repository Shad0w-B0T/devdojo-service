package devdojo.logic.security.ldap;

import devdojo.logic.database.user.UserEntity;
import devdojo.logic.user.IUserService;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.user.UserRole;
import java.util.*;
import org.springframework.validation.annotation.Validated;

@Validated
public abstract class AbstractLdapService implements ILdapService {

  private final IUserService userService;
  private final ILdapAttributeService ldapAttributeService;

  protected AbstractLdapService(final IUserService userService,
                                final ILdapAttributeService ldapAttributeService) {
    this.userService = userService;
    this.ldapAttributeService = ldapAttributeService;
  }

  @Override
  public UserEntity loadOrSyncUser(final String authId) {
    try {
      return userService.getUser(new IUserService.UserGet() {
        @Override
        public String authId() {
          return authId;
        }

        @Override
        public java.util.UUID userId() {
          return null;
        }
      });
    } catch (NotFoundException e) {
      Map<String, Object> attrs = this.ldapAttributeService.loadUserAttributes(authId);

      UserRole ldapUserRole =
        mapLdapUserRole(attrs.get("eduPersonAffiliation"));

      return userService.createUser(new IUserService.UserCreate() {
        @Override
        public String authId() {
          return authId;
        }

        @Override
        public String email() {
          return Objects.toString(attrs.get("mail"), "");
        }

        @Override
        public String displayName() {
          return Objects.toString(attrs.get("displayName"), "");
        }

        @Override
        public UserRole ldapRole() {
          return ldapUserRole;
        }

        @Override
        public UserRole applicationRole() {
          return ldapUserRole;
        }

        @Override
        public boolean active() {
          return true;
        }
      });
    }
  }

  private UserRole mapLdapUserRole(final Object attributeObject) {
    if (!(attributeObject instanceof java.util.List<?> list)) {
      return UserRole.GUEST;
    }
    if (list.contains("employee") || list.contains("staff")) {
      return UserRole.ADMIN;
    }
    if (list.contains("student")) {
      return UserRole.STUDENT;
    }
    return UserRole.GUEST;
  }

}
