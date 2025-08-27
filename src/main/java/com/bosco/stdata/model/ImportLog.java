package com.bosco.stdata.model;

import lombok.Data;

@Data
public class ImportLog {

    private int importId;
    private String info;  // for logs
    private String error;  // for error logs only
    private String createdDateTime;
    

    
}
