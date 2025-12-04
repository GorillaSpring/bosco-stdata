package com.bosco.stdata.controllers;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/oauth2")
@Tag(name = "Authentication", description = "Token management and authentication endpoints")
public class TokenProxyController {

    @Value("${okta.oauth2.issuer:https://integrator-2192431.okta.com/oauth2/default}")
    private String oktaIssuer;

    @Value("${okta.oauth2.client-id:}")
    private String defaultClientId;

    @Value("${okta.oauth2.client-secret:}")
    private String defaultClientSecret;

    /**
     * Proxy endpoint to obtain OAuth2 tokens from Okta IDP
     * This endpoint simplifies token acquisition for Swagger UI testing
     */
    @PostMapping("/token")
    @Operation(
        summary = "Obtain OAuth2 Access Token",
        description = "Proxy endpoint for OAuth2 client credentials flow. Accepts client_id and client_secret from Swagger UI authorization form.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token successfully obtained"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Invalid client credentials")
        }
    )
    public ResponseEntity<Map<String, Object>> getToken(
            HttpServletRequest request,

            // Standard OAuth2 parameters - try multiple parameter names
            @RequestParam(name = "grant_type", defaultValue = "client_credentials") String grantType,

            // Try multiple parameter names that Swagger might use
            @RequestParam(name = "client_id", required = false) String clientId,
            @RequestParam(name = "clientId", required = false) String clientIdAlt,
            @RequestParam(name = "username", required = false) String username, // Swagger sometimes uses this

            @RequestParam(name = "client_secret", required = false) String clientSecret,
            @RequestParam(name = "clientSecret", required = false) String clientSecretAlt,
            @RequestParam(name = "password", required = false) String password, // Swagger sometimes uses this

            //@RequestParam(defaultValue = "openid profile email") String scope,
            //@RequestParam(defaultValue = "api:read api:write") String scope,
            @RequestParam(defaultValue = "") String scope,
            @RequestParam(defaultValue = "okta") String idp,
            @RequestParam(required = false) String code,
            @RequestParam(name = "redirect_uri", required = false) String redirectUri) {


        try {
            // Log all parameters for debugging
            System.out.println("=== Token Request Debug ===");
            System.out.println("All request parameters:");
            request.getParameterMap().forEach((key, values) -> {
                System.out.println("  " + key + " = " + (key.toLowerCase().contains("secret") || key.toLowerCase().contains("password") ?
                    "[REDACTED]" : String.join(", ", values)));
            });

            // Try to extract client_id from multiple possible parameter names
            String effectiveClientId = getFirstNonEmpty(clientId, clientIdAlt, username);
            String effectiveClientSecret = getFirstNonEmpty(clientSecret, clientSecretAlt, password);

            // Check Authorization header for Basic auth (common with OAuth2)
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Basic ")) {
                try {
                    String encodedCredentials = authHeader.substring(6);
                    String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials));
                    String[] credentials = decodedCredentials.split(":", 2);
                    if (credentials.length == 2) {
                        effectiveClientId = credentials[0];
                        effectiveClientSecret = credentials[1];
                        System.out.println("Using credentials from Authorization header");
                    }
                } catch (Exception e) {
                    System.out.println("Failed to decode Authorization header: " + e.getMessage());
                }
            }

            // Fall back to application properties if still empty
            if (isEmpty(effectiveClientId)) {
                effectiveClientId = defaultClientId;
                System.out.println("Using default client_id from application properties");
            }

            if (isEmpty(effectiveClientSecret)) {
                effectiveClientSecret = defaultClientSecret;
                System.out.println("Using default client_secret from application properties");
            }

            System.out.println("Final effective client_id: " + effectiveClientId);
            System.out.println("Final effective client_secret: " + (isEmpty(effectiveClientSecret) ? "EMPTY" : "[REDACTED]"));

            // Validate we have credentials
            if (isEmpty(effectiveClientId) || isEmpty(effectiveClientSecret)) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                    "invalid_client",
                    "Client credentials not provided and not configured in application properties. " +
                    "Please provide client_id and client_secret in the Swagger authorization form, " +
                    "or configure them in application properties."
                ));
            }

            // Route to appropriate IDP
            return switch (idp.toLowerCase()) {
                case "okta" -> proxyToOkta(grantType, effectiveClientId, effectiveClientSecret, scope, code, redirectUri);
                //case "allen", "allenisd" -> proxyToAllenISD(grantType, effectiveClientId, effectiveClientSecret, scope, code, redirectUri);
                default -> ResponseEntity.badRequest().body(createErrorResponse(
                    "unsupported_idp",
                    "Supported IDPs: okta, allen. Received: " + idp
                ));
            };

        } catch (Exception e) {
            System.err.println("Token proxy error: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("proxy_error", "Internal server error: " + e.getMessage()));
        }
    }

    // Helper methods
    private String getFirstNonEmpty(String... values) {
        for (String value : values) {
            if (!isEmpty(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

	private Map<String, Object> createErrorResponse(String error, String description) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("error", error);
		errorResponse.put("error_description", description);
		return errorResponse;
	}



    private ResponseEntity<Map<String, Object>> proxyToOkta(String grantType, String clientId,
            String clientSecret, String scope, String code, String redirectUri) {

        RestTemplate restTemplate = new RestTemplate();

        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Use Basic Authentication
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);

            // Prepare request body based on grant type
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", grantType);
            body.add("scope", scope);

            if ("authorization_code".equals(grantType)) {
                if (code == null || redirectUri == null) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "invalid_request",
                        "error_description", "code and redirect_uri are required for authorization_code grant"
                    ));
                }
                body.add("code", code);
                body.add("redirect_uri", redirectUri);
            }

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            // Call Okta token endpoint
            ResponseEntity<Map> response = restTemplate.postForEntity(
                oktaIssuer + "/v1/token",
                request,
                Map.class
            );

            // Add metadata to response
            Map<String, Object> enhancedResponse = new HashMap<>(response.getBody());
            enhancedResponse.put("idp", "okta");
            enhancedResponse.put("issued_at", System.currentTimeMillis() / 1000);

            return ResponseEntity.ok(enhancedResponse);

        } catch (HttpClientErrorException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "idp_error");
            errorResponse.put("error_description", "Okta returned error: " + e.getResponseBodyAsString());
            errorResponse.put("status_code", e.getStatusCode().value());
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        }
    }

    private ResponseEntity<Map<String, Object>> proxyToAllenISD(String grantType, String clientId,
            String clientSecret, String scope, String code, String redirectUri) {

        // Allen ISD (RapidIdentity) typically uses SAML, not OAuth2
        // This is a placeholder for potential OAuth2 support or custom implementation
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "not_supported");
        errorResponse.put("error_description", "Allen ISD uses SAML authentication, not OAuth2. Use SAML login flow instead.");
        errorResponse.put("saml_login_url", "/saml2/authenticate/allenisd");

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(errorResponse);
    }

    /**
     * Get authorization URL for OAuth2 authorization code flow
     */
    @GetMapping("/authorize-url")
    @Operation(
        summary = "Get Authorization URL",
        description = "Generate the authorization URL for OAuth2 authorization code flow"
    )
    public ResponseEntity<Map<String, Object>> getAuthorizationUrl(
            @RequestParam(defaultValue = "okta") String idp,
            @RequestParam(required = false) String client_id,
            @RequestParam(required = false) String redirect_uri,
            @RequestParam(defaultValue = "openid profile email") String scope,
            @RequestParam(defaultValue = "code") String response_type) {

        String effectiveClientId = client_id != null ? client_id : defaultClientId;
        String effectiveRedirectUri = redirect_uri != null ? redirect_uri : "http://localhost:9090/auth/callback";

        if ("okta".equalsIgnoreCase(idp)) {
            String authUrl = oktaIssuer + "/v1/authorize" +
                "?client_id=" + effectiveClientId +
                "&response_type=" + response_type +
                "&scope=" + scope.replace(" ", "%20") +
                "&redirect_uri=" + effectiveRedirectUri +
                "&state=" + System.currentTimeMillis();

            return ResponseEntity.ok(Map.of(
                "authorization_url", authUrl,
                "client_id", effectiveClientId,
                "redirect_uri", effectiveRedirectUri,
                "scope", scope,
                "idp", "okta"
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
            "error", "unsupported_idp",
            "error_description", "Authorization URL generation not supported for IDP: " + idp
        ));
    }

    // Response schema for OpenAPI documentation
    public static class TokenResponse {
        public String access_token;
        public String token_type;
        public Integer expires_in;
        public String scope;
        public String idp;
        public Long issued_at;
    }
}