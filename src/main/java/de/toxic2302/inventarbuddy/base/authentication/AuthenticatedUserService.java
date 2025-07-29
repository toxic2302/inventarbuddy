package de.toxic2302.inventarbuddy.base.authentication;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserService implements AuthenticationService {

  private static final String EMAIL = "email";
  private static final String FIRST_NAME = "given_name";
  private static final String LAST_NAME = "family_name";

  @Override
  public String getUserName() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  @Override
  public String getEmail() {
    return getClaim(EMAIL);
  }

  @Override
  public String getFirstName() {
    return getClaim(FIRST_NAME);
  }

  @Override
  public String getLastName() {
    return getClaim(LAST_NAME);
  }

  private String getClaim(String key) {
    final Jwt principal = getPrincipal();
    final Map<String, Object> claims = principal.getClaims();
    final Map<String, Object> userClaims = (Map<String, Object>) claims.get("user");

    return userClaims.get(key).toString();
  }

  private Jwt getPrincipal() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (Jwt) authentication.getPrincipal();
  }
}
