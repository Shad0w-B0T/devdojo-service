package devdojo.logic.security.jwt;

import jakarta.servlet.http.HttpServletResponse;

public interface IRefreshCookie {

  String name();

  void set(final HttpServletResponse response,
           final String refreshToken,
           final long maxAgeSeconds);

  void clear(final HttpServletResponse response);
}
