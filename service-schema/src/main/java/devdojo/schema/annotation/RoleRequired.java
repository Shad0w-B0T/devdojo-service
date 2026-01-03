package devdojo.schema.annotation;

import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@tokenEvaluator.isValid(authentication) && " +
  "@tokenEvaluator.hasRole(authentication, T(devdojo.schema.user.UserRole).{role})")
public @interface RoleRequired {

  @NotNull UserRole role();
}

