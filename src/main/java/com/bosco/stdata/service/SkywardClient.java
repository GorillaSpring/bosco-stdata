package com.bosco.stdata.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
public class SkywardClient {
  
    private final RestTemplate restTemplate;

    public SkywardClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JsonNode get(String url, String token) throws Exception {

    HttpHeaders headers = new HttpHeaders();

    
    headers.setBearerAuth(token);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<JsonNode> response =
        restTemplate.exchange(url, HttpMethod.GET, request, JsonNode.class);

    JsonNode responseBody = response.getBody();
    if (responseBody == null || !responseBody.fields().hasNext()) {
      throw new IllegalStateException(
          "Empty or invalid response from Skyward API for: " + "students");
    }

    Map.Entry<String, JsonNode> entry = responseBody.fields().next();
    return entry.getValue();
  }
}
