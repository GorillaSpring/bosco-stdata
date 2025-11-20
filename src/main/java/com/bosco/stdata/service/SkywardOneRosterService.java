package com.bosco.stdata.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SkywardOneRosterService {

  private final SkywardClient skywardClient;

  public SkywardOneRosterService(SkywardClient skywardClient) {
    this.skywardClient = skywardClient;
  }


  private final int PAGE_SIZE = 5000;


  // this is used for NON oneroster apis.
  public JsonNode fetchSkywardApi(String apiUrl, String token) throws Exception {
    JsonNode data = skywardClient.getSkyward(apiUrl, token);

    return data;
  }


  public JsonNode fetchResourcePageWithFilter(String apiUrl, String filter, String token, int page) throws Exception {
    
    String pagination = "?offset=" + page * PAGE_SIZE + "&limit=" + PAGE_SIZE;

    //String tfilter = "&filter=role='guardian'";

    JsonNode data = skywardClient.get(apiUrl + pagination + "&filter=" + filter, token);
    //JsonNode data = skywardClient.get(apiUrl + pagination + tfilter , token);

    return data;


    
  }

  
  public JsonNode fetchResourcePageWithFilter1000(String apiUrl, String filter, String token, int page) throws Exception {
    
    String pagination = "?offset=" + page * 1000 + "&limit=" + 1000;

    //String tfilter = "&filter=role='guardian'";

    JsonNode data = skywardClient.get(apiUrl + pagination + "&filter=" + filter, token);
    //JsonNode data = skywardClient.get(apiUrl + pagination + tfilter , token);

    return data;


    
  }

}
