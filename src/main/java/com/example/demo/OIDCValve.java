package com.example.demo;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * todo:
 */
public class OIDCValve extends ValveBase {

  private static final String JWT_COOKIE_NAME = "__JWT__";

  /**
   * Key used to verify JWT signature; obtained as part of dynamic registration process
   */
  private PublicKey signingKey;

  /**
   * OAuth client_id for this service; result of dynamic registration
   */
  private String clientId;

  /**
   * OAuth client_secret for this service; result of dynamic registration
   */
  private String clientSecret;

  /**
   * The OAuth Authorize full URL (without parameters)
   */
  private String authorizationUrl;

  /**
   * The OAuth Token Endpoint
   */
  private String tokenUrl;

  @Override
  protected void initInternal() throws LifecycleException {
    super.initInternal();

    containerLog.info("Performing dynamic OAuth registration for the service");

    //todo: 1. perform dynamic registration
    clientId = "";
    clientSecret = "";

    containerLog.info("Obtaining signing key for the service");
    //todo: 2. fetch keys with jwks endpoint
    signingKey = null; //todo:

    authorizationUrl = "/authorize";
    tokenUrl = "/token";
  }

  @Override
  public void invoke(Request request, Response response) throws IOException, ServletException {
    final Optional<Cookie> optional = findJwtCookie(request);

    if (!optional.isPresent()) { //todo: or token is expired
      final String code = request.getParameter("code");
      final String state = request.getParameter("state");

      if (code != null) {
        getToken(code);
        return;
      }


      final String params = Map.of(
          "client_id", clientId,
          "response_type", "code",
          "scope", "openid",
          "redirect_uri", "http://localhost:8080/",
          "state", "56790", //todo:?? should be validated before calling /token endpoint!
          "nonce", "12345") //todo: random nonce, encoded into JWT token for validation
          .entrySet().stream()
          .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue()))
          .collect(Collectors.joining("&"));

      response.sendRedirect(authorizationUrl + "?" + params);

      //todo: 1. send redirect to /authorize endpoint
      //todo

      //todo: start oauth flow here
      return;
    }

//    final Jws<Claims> claim = Jwts.parser()
//        .setSigningKey(this.signingKey)
//        .parseClaimsJws(optional.get().getValue());

    //todo: exception if signature is invalid

    //todo: verify if token expired
//
//    final String subject = claim.getBody().getSubject();

//    request.setUserPrincipal();

    getNext().invoke(request, response);
  }

  private Optional<Cookie> findJwtCookie(Request request) {
    final Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      return Arrays.stream(cookies)
          .filter(cookie -> JWT_COOKIE_NAME.equals(cookie.getName()))
          .findAny();
    }
    return Optional.empty();
  }

  /**
   * todo:
   */
  private void getToken(String authorizationCode) {
    String auth = "Basic " + Base64.getEncoder().encodeToString(
        (clientId + ":" + clientSecret).getBytes());


    final HttpClient client = HttpClient.newBuilder() //todo:
        .build();

    final String query = toQuery(
        Map.of("code", authorizationCode,
            "redirect_uri", "http://localhost:8080/",
            "grant_type", "authorization_code"));

    try {

      final HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(tokenUrl))
          .POST(HttpRequest.BodyPublishers.ofString(query))
          .header("Content-Type", "application/x-www-form-urlencoded")
          .header("Authorization", auth)
          .build();

      final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      System.out.println(response);

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

  }

  /**
   * Converting map to URL-encoded string of URL query parameters
   */
  private static String toQuery(Map<String, String> params) {
    if (params == null || params.isEmpty()) {
      return "";
    }
    return params
        .entrySet().stream()
        .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue()))
        .collect(Collectors.joining("&"));
  }


  /**
   * todo:
   */
  private enum AuthState {

    /**
     * todo:
     */
    AUTHENTICATED_WITH_COOKIE,

    AUTHORIZATION_REQUEST,

    /**
     * The second step in OAuth authorization_code flow where code must be exchanged for access and OID tokens
     */
    TOKEN_EXCHANGE
  }

}
