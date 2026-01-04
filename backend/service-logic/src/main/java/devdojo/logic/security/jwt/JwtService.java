package devdojo.logic.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService extends AbstractJwtService {

  protected JwtService(@Value("${security.jwt.secretHash}") final String secretHash) {
    super(secretHash);
  }
}
