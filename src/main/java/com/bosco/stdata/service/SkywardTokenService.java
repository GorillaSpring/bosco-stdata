package com.bosco.stdata.service;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.Base64;
import java.util.Map;


@Service
public class SkywardTokenService {
    private final RestTemplate restTemplate;

  public SkywardTokenService(RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
  }

    public String getAccessToken(String clientId, String clientSecret, String tokenUrl) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    String auth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
    headers.set("Authorization", "Basic " + auth);

    HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);

    ResponseEntity<Map> response =
        restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);
    return (String) response.getBody().get("access_token");
  }
}

  
