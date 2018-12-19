package com.example.demo;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * OpenID Connect Dynamic Client Registration Endpoint https://openid.net/specs/openid-connect-registration-1_0.html
 */
@RestController
public class ClientRegistrationController {

  @PostMapping("/oidc/register")
  public ClientRegistrationResponse register(HttpServletRequest request) {
    final String clientId = RandomStringUtils.randomAlphanumeric(20);
    final String clientSecret = RandomStringUtils.randomAlphanumeric(40);
    return new ClientRegistrationResponse(clientId, clientSecret);
  }


  static class ClientRegistrationResponse {
    public String clientId;
    public String clientSecret;

    ClientRegistrationResponse(String clientId, String clientSecret) {
      this.clientId = clientId;
      this.clientSecret = clientSecret;
    }
  }
}
