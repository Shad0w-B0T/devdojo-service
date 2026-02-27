package devdojo.logic.security.ldap.service;

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
      return this.userService.getUser(new IUserService.GetUserByAuthId(authId));
    } catch (NotFoundException e) {
      Map<String, Object> attrs = this.ldapAttributeService.loadUserAttributes(authId);

      UserRole ldapUserRole =
        mapLdapUserRole(attrs.get("eduPersonAffiliation"));

      return this.userService.createUser(new IUserService.CreateUser(
        authId,
        Objects.toString(attrs.get("mail"), ""),
        Objects.toString(attrs.get("displayName"), ""),
        ldapUserRole,
        ldapUserRole,
        true));
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
