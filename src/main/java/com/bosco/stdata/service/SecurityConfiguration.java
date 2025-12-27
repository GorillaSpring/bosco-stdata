package com.bosco.stdata.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtAudienceValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private static Logger logger = Logger.getLogger(SecurityConfiguration.class.getName());

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:https://integrator-2192431.okta.com/oauth2/default/v1/keys}")
    private String jwkSetUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:https://integrator-2192431.okta.com/oauth2/default}")
    private String issuerUri;


	@Autowired
	private Environment env;

    @Bean
    @Order(1)  // Ensure this filter chain has priority
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
    	//String url = env.getProperty("success.login.url", "/bosco/dashboard");
    	//logger.info("Success login URL from properties: " + url);
    	String requestMatchersConfig = env.getProperty("request.matchers", "/import/swagger-ui/**");
    	logger.info("Request matchers from properties: " + requestMatchersConfig);
    	String[] requestMatchers = requestMatchersConfig.split(",");
    	//List<String> patternList = new ArrayList<>();

    	// Add the request matchers from the configuration
		// for (String matcher : requestMatchers) {
		// 	patternList.add(matcher.trim());
		// }

    	// Convert the List to a String array
    	//String[] patternsArray = patternList.toArray(new String[0]);


    	//logger.info("Success login URL: " + url);
    	//OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
    	//authenticationProvider.setResponseAuthenticationConverter(groupsConverter());



    	http.authorizeHttpRequests(authorize -> authorize
    			.requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers(requestMatchers).permitAll()
    			.anyRequest().authenticated())
    			//.requestMatchers("/import/**").authenticated() //"/logout", "/login/*", "/bosco/*", "/bosco/saml2/**", "/bosco/login/saml2/**", "/public/**", "/error", "/saml2/**","/login/saml2/**").permitAll()
    			//.anyRequest().permitAll())

    	//.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(jwt -> {}))

/*

    	 .oauth2ResourceServer(oauth2 -> oauth2
                 .jwt(jwt -> jwt
                     .decoder(jwtDecoder())
                     .jwtAuthenticationConverter(jwtAuthenticationConverter())
                 )
                 .authenticationEntryPoint((request, response, authException) -> {
                     System.err.println("=== JWT Authentication Failure ===");
                     System.err.println("URI: " + request.getRequestURI());
                     System.err.println("Error: " + authException.getMessage());
                     authException.printStackTrace();

                     response.setStatus(401);
                     response.setContentType("application/json");
                     response.getWriter().write(String.format(
                         "{\"error\":\"jwt_authentication_failed\",\"message\":\"%s\"}",
                         authException.getMessage().replace("\"", "\\\"")
                     ));
                 })
                 )




*/

    	  .oauth2ResourceServer(oauth2 -> {
              System.out.println("Configuring OAuth2 Resource Server...");
              oauth2.jwt(jwt -> {
                  System.out.println("Configuring JWT decoder...");
                  jwt
                      .decoder(jwtDecoder())
                      .jwtAuthenticationConverter(jwtAuthenticationConverter());
              })
              .authenticationEntryPoint((request, response, authException) -> {
                  System.err.println("=== JWT Authentication Entry Point ===");
                  System.err.println("Request URI: " + request.getRequestURI());
                  System.err.println("Request Method: " + request.getMethod());
                  System.err.println("Authorization Header: " + request.getHeader("Authorization"));
                  System.err.println("Authentication Error: " + authException.getMessage());
                  System.err.println("Error Type: " + authException.getClass().getSimpleName());

                  if (authException.getCause() != null) {
                      System.err.println("Root Cause: " + authException.getCause().getMessage());
                  }

                  authException.printStackTrace();

                  response.setStatus(401);
                  response.setContentType("application/json");
                  response.getWriter().write(String.format(
                      "{\"error\":\"authentication_required\",\"message\":\"%s\",\"uri\":\"%s\"}",
                      authException.getMessage().replace("\"", "\\\""),
                      request.getRequestURI()
                  ));
              })
              .accessDeniedHandler((request, response, accessDeniedException) -> {
                  System.err.println("=== Access Denied ===");
                  System.err.println("Request URI: " + request.getRequestURI());
                  System.err.println("User: " + (request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "null"));
                  System.err.println("Access Denied: " + accessDeniedException.getMessage());

                  response.setStatus(403);
                  response.setContentType("application/json");
                  response.getWriter().write(String.format(
                      "{\"error\":\"access_denied\",\"message\":\"%s\",\"uri\":\"%s\"}",
                      accessDeniedException.getMessage().replace("\"", "\\\""),
                      request.getRequestURI()
                  ));
              })


              ;
          })
          .csrf(csrf -> csrf.disable())


    	//



    	/*
    	.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwkSetUri(jwkSetUri)
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
                .authenticationEntryPoint((request, response, authException) -> {
                	System.err.println("=== Authentication Entry Point Error ===");
                    System.err.println("Request URI: " + request.getRequestURI());
                    System.err.println("Auth header: " + request.getHeader("Authorization"));
                    System.err.println("Error: " + authException.getMessage());
                    System.err.println("Error class: " + authException.getClass().getSimpleName());
                    authException.printStackTrace();

                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.getWriter().write(String.format(
                        "{\"error\":\"authentication_failed\",\"message\":\"%s\",\"suggestion\":\"Check /debug/jwt/analyze endpoint with your token\"}",
                        authException.getMessage().replace("\"", "\\\"")
                    ));
                })
            )
            */
    	;

    	return http.build();
    }



public JwtAuthenticationConverter jwtAuthenticationConverter2() {
        System.out.println("Creating JWT Authentication Converter...");

        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("SCOPE_");
        authoritiesConverter.setAuthoritiesClaimName("scp"); // Try "scp" first, then "scope"

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        // Debug converter
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            System.out.println("=== Converting JWT to Authorities ===");
            System.out.println("JWT Claims: " + jwt.getClaims());

            // Try multiple scope claim names
            List<String> scopes = new ArrayList<>();
            if (jwt.hasClaim("scp")) {
                scopes = jwt.getClaimAsStringList("scp");
                System.out.println("Found scopes in 'scp' claim: " + scopes);
            } else if (jwt.hasClaim("scope")) {
                String scopeString = jwt.getClaimAsString("scope");
                if (scopeString != null) {
                    scopes = List.of(scopeString.split(" "));
                }
                System.out.println("Found scopes in 'scope' claim: " + scopes);
            }

            return authoritiesConverter.convert(jwt);
        });

        return converter;
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        System.out.println("=== Creating JWT Decoder ===");
        System.out.println("JWK Set URI: " + jwkSetUri);
        System.out.println("Issuer URI: " + issuerUri);

        // Create the decoder with your specific configuration
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
            //.cache(new Cache(Duration.ofMinutes(5)))
            .build();

        // Set up validator that handles your token's audience
        jwtDecoder.setJwtValidator(jwtValidator());

        return jwtDecoder;
    }

    @Bean
    public OAuth2TokenValidator<Jwt> jwtValidator() {
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();

        // Timestamp validation
        validators.add(new JwtTimestampValidator());

        // Issuer validation
        validators.add(new JwtIssuerValidator(issuerUri));

        // Audience validation - accept your specific audience
        validators.add(new JwtAudienceValidator("api://default")); //Arrays.asList("api://default")));

        // Custom debug validator
        validators.add(new OAuth2TokenValidator<Jwt>() {
            @Override
            public OAuth2TokenValidatorResult validate(Jwt jwt) {
                System.out.println("=== JWT Validation Debug ===");
                System.out.println("Algorithm: " + jwt.getHeaders().get("alg"));
                System.out.println("Key ID: " + jwt.getHeaders().get("kid"));
                System.out.println("Subject: " + jwt.getSubject());
                System.out.println("Issuer: " + jwt.getIssuer());
                System.out.println("Audience: " + jwt.getAudience());
                System.out.println("Scopes: " + jwt.getClaimAsStringList("scp"));
                System.out.println("Expires At: " + jwt.getExpiresAt());
                System.out.println("Client ID: " + jwt.getClaim("cid"));

                return OAuth2TokenValidatorResult.success();
            }
        });

        return new DelegatingOAuth2TokenValidator<>(validators);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Your token uses "scp" claim with ["AccessAll"]
        authoritiesConverter.setAuthorityPrefix("SCOPE_");
        authoritiesConverter.setAuthoritiesClaimName("scp");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // Custom authorities converter to handle your specific scopes
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            System.out.println("=== Converting JWT to Authorities ===");

            Collection<String> scopes = jwt.getClaimAsStringList("scp");
            System.out.println("Found scopes: " + scopes);

            if (scopes != null && !scopes.isEmpty()) {
                return scopes.stream()
                    .map(scope -> "SCOPE_" + scope)
                    .map(authority -> (org.springframework.security.core.GrantedAuthority) () -> authority)
                    .collect(java.util.stream.Collectors.toList());
            }

            // Fallback - grant basic access if no scopes
            return List.of(() -> "SCOPE_api:access");
        });

        return converter;
    }

   // @Bean
    public JwtDecoder jwtDecoderD() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // Optional: customize JWT validation
        // jwtDecoder.setClaimSetConverter(claims -> {
        //     // Custom claim processing if needed
        //     return claims;
        // });

        //jwtDecoder.setJwtValidator(jwt -> {
            // Only validate timestamp for now
          //  return JwtValidators.createDefault().validate(jwt);
        //});

        return jwtDecoder;
    }


    //@Bean
    public JwtDecoder relaxedJwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
            .withJwkSetUri("https://integrator-2192431.okta.com/oauth2/v1/keys")
            //.cache(Duration.ofMinutes(10))
            .build();

        // Use minimal validation for debugging
        //jwtDecoder.setJwtValidator(jwt -> {
            // Only validate timestamp for now
        //	System.out.println("\n\n\n\n\n\n\n\nRelaxed JWT Decoder: Validating JWT\n\n\n\n\n\n");
        //    return JwtValidators.createDefault().validate(jwt);
        //});
        jwtDecoder.setJwtValidator(jwtValidator());

        return jwtDecoder;
    }



//@Bean
public OAuth2TokenValidator<Jwt> jwtValidatorOld() {
    List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
    validators.add(new JwtTimestampValidator());
    validators.add(new JwtIssuerValidator("https://integrator-2192431.okta.com/oauth2/default"));
    // Add audience validator if your tokens have audience claim
    // validators.add(new JwtAudienceValidator("your-audience"));

    return new DelegatingOAuth2TokenValidator<>(validators);
}



}