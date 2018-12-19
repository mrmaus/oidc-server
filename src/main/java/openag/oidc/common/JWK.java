package openag.oidc.common;


/**
 * JSON Web Key container https://tools.ietf.org/html/rfc7517
 */
public class JWK {

  /**
   * Key Type (cryptographic algorithm, such as "RSA" or "EC") https://tools.ietf.org/html/rfc7517#section-4.1
   */
  private String kty;

  /**
   * Public Key Use; "sig" (signature) or "enc" (encryption); https://tools.ietf.org/html/rfc7517#section-4.2
   */
  private String use;

  /**
   * RSA Public Key Exponent
   */
  private String e;

  /**
   * RSA Public Key Modulus
   */
  private String n;

  /**
   * identifies the algorithm intended for use with the key. https://tools.ietf.org/html/rfc7517#section-4.4
   */
  private String alg;
}
