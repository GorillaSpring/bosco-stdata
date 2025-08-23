package com.bosco.stdata.utils;


public class ClassLinkOneRosterResponse {

 private int statusCode;
    private String response;

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponse() {
        return response;
    }

    public ClassLinkOneRosterResponse(int statusCode, String response) {
        this.statusCode = statusCode;
        this.response = response;
    }
}