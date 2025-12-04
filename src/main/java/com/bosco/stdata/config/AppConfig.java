package com.bosco.stdata.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class AppConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }


    // @Value("${springdoc.server.url:http://localhost:8080/bosco}")
    // private String serverUrl;

    // Add OAuth2 token URL configuration
    @Value("${okta.oauth2.token.url}")
    private String oauthTokenUrl;

  @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
              
        
                .addTagsItem(new Tag().name("Import Defs").description("Import Definitions"))

                .addTagsItem(new Tag().name("Import Testing").description("Testing Of Imports - RUN NOW."))


                .addTagsItem(new Tag().name("Bosco Endpoints").description("Bosco Web will call these endpoints"))
                .addTagsItem(new Tag().name("Bosco API Examples").description("This is what StData will call in Bosco and send the data"))
                .addTagsItem(new Tag().name("Testing").description("Testing methods - Will be removed"))


                .components(new Components()
                        // OAuth2 Security Scheme WITHOUT scopes
                        .addSecuritySchemes("oauth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("OAuth2 Client Credentials Flow - Enter your client_id and client_secret")
                                .flows(new OAuthFlows()
                                        .clientCredentials(new OAuthFlow()
                                        		.tokenUrl(oauthTokenUrl)
                                                .refreshUrl(oauthTokenUrl)))))


                  .addSecurityItem(new SecurityRequirement()
                        .addList("oauth2"));
                  


    }
}
