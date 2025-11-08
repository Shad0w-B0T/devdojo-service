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
@PreAuthorize("@clientEvaluator.isValid(authentication) && " +
  "@clientEvaluator.isSelf(authentication, {field}, T(devdojo.schema.user.UserRole).{bypassRole})")
public @interface SelfRequired {

  //Spel field
  @NotNull String field() default "user";

  UserRole bypassRole() default UserRole.SYSTEM;
}
