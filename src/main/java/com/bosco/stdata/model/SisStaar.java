package com.bosco.stdata.model;

import lombok.Data;

@Data
public class SisStaar {
    public String schoolYear;

    public String subject;
    public String grade;
    public String proficiency;
    
    public String code;
    public String proficiencyCode;
    public String csacode;


    // not sure if needed
    public String date;

    
    //public String _class = "com.bosco.model.ScorableAcademicRecord";
}
