package com.bosco.stdata.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

//import lombok.Data;

 
public class SisDisciplineCounts {
    private String ISS = "0";
    private String OSS = "0";
    private String OR = "0";
    private String CCV = "0";
    private String DAEP = "0";



    public String getISS() {
        return ISS;
    }
    @JsonProperty("ISS")
    public void setISS(String iSS) {
        ISS = iSS;
    }
    
    @JsonProperty("OSS")
    public String getOSS() {
        return OSS;
    }
    public void setOSS(String oSS) {
        OSS = oSS;
    }
    @JsonProperty("OR")
    public String getOR() {
        return OR;
    }
    public void setOR(String oR) {
        OR = oR;
    }

    @JsonProperty("CCV")
    public String getCCV() {
        return CCV;
    }
    public void setCCV(String cCV) {
        CCV = cCV;
    }
    @JsonProperty("DAEP")
    public String getDAEP() {
        return DAEP;
    }
    public void setDAEP(String dAEP) {
        DAEP = dAEP;
    }

    
}
