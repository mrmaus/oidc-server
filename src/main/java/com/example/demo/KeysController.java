package com.example.demo;


import openag.oidc.common.JWKS;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * todo: Returns a JSON Web Key Set (JWKS), which contains the public keys that can be used to verify the signatures of
 * tokens that you receive from your authorization server.
 */
@RestController
public class KeysController {

  @GetMapping("/oidc/keys")
  public JWKS keys(@RequestParam("client_id") String clientId) {

  }

}
