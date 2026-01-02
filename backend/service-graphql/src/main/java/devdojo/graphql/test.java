package devdojo.graphql;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class test {

  @GetMapping
  public Map<String, Object> t(final Authentication auth) {
    System.out.println(auth);
    if (auth == null) {
      return Map.of();
    }

    Map<String, Object> map = new HashMap<>();
    map.put("p", auth.getPrincipal().toString());

    if (auth instanceof AbstractAuthenticationToken token) {
      map.put("extra", token.getDetails());
    }

    return map;
  }

}
