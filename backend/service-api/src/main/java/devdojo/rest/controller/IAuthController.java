package devdojo.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;

public interface IAuthController {

  @NotNull
  ResponseEntity<JwtResponse> login(final LoginRequest request,
                                                   final HttpServletResponse response);

  @NotNull
  ResponseEntity<JwtResponse> refresh(final HttpServletRequest request,
                                      final HttpServletResponse response);

  @NotNull
  ResponseEntity<Void> logout(final HttpServletRequest request,
                              final HttpServletResponse response,
                              final String authHeader);

  record LoginRequest(@NotBlank String username,
                      @NotBlank String password) {
  }

  record JwtResponse(@NotBlank String token) {
  }
}
