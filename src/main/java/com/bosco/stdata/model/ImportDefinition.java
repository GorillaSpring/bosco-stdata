package com.bosco.stdata.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ImportDefinition {
    private String id;
    private String importType;
    private int districtId;
    private Boolean active;
    private Boolean forceLoad;
    private int baseImportId;
    private LocalDateTime lastRunDate;


    // here we can add a flag to scramble the email

    private Boolean setNoEmails;
}
