package com.bosco.stdata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class AppConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
              
        
                .addTagsItem(new Tag().name("Import Defs").description("Import Definitions"))

                .addTagsItem(new Tag().name("Import Testing").description("Testing Of Imports - RUN NOW."))


                .addTagsItem(new Tag().name("Bosco Endpoints").description("Bosco Web will call these endpoints"))
                .addTagsItem(new Tag().name("Bosco API Examples").description("This is what StData will call in Bosco and send the data"))
                .addTagsItem(new Tag().name("Testing").description("Testing methods - Will be removed"));
    }
}
