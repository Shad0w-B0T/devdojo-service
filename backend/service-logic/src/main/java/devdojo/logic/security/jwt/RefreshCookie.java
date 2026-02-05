package devdojo.logic.security.jwt;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class RefreshCookie implements IRefreshCookie{

  private final String cookieName;
  private final boolean secure;
  private final String sameSite;
  private final String path;

  public RefreshCookie(@Value("${security.refresh.cookieName:refresh_token}") final String cookieName,
                       @Value("${security.refresh.secure:false}") final boolean secure,
                       @Value("${security.refresh.sameSite:Lax}") final String sameSite,
                       @Value("${security.refresh.path:/api}") final String path
  ) {
    this.cookieName = cookieName;
    this.secure = secure;
    this.sameSite = sameSite;
    this.path = path;
  }

  @Override
  public String name() {
    return cookieName;
  }

  @Override
  public void set(final HttpServletResponse response,
                  final String refreshToken,
                  final long maxAgeSeconds) {
    final String cookie = cookieName + "=" + refreshToken +
      "; Max-Age=" + maxAgeSeconds +
      "; Path=" + path +
      "; HttpOnly" +
      (secure ? "; Secure" : "") +
      "; SameSite=" + sameSite;

    response.addHeader(HttpHeaders.SET_COOKIE, cookie);
  }

  @Override
  public void clear(final HttpServletResponse response) {
    final String cookie = cookieName + "=; Max-Age=0; Path=" + path + "; HttpOnly" +
      (secure ? "; Secure" : "") +
      "; SameSite=" + sameSite;

    response.addHeader(HttpHeaders.SET_COOKIE, cookie);
  }
}
