package com.bosco.stdata.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

//import lombok.Data;

 
public class SisDisciplineCounts {
    private int ISS = 0;
    private int OSS = 0;
    private int OR = 0;
    private int CCV = 0;
    private int DAEP = 0;



    public int getISS() {
        return ISS;
    }
    @JsonProperty("ISS")
    public void setISS(int iSS) {
        ISS = iSS;
    }
    
    @JsonProperty("OSS")
    public int getOSS() {
        return OSS;
    }
    public void setOSS(int oSS) {
        OSS = oSS;
    }
    @JsonProperty("OR")
    public int getOR() {
        return OR;
    }
    public void setOR(int oR) {
        OR = oR;
    }

    @JsonProperty("CCV")
    public int getCCV() {
        return CCV;
    }
    public void setCCV(int cCV) {
        CCV = cCV;
    }
    @JsonProperty("DAEP")
    public int getDAEP() {
        return DAEP;
    }
    public void setDAEP(int dAEP) {
        DAEP = dAEP;
    }

    
}
