package com.bosco.stdata.model;

import lombok.Data;

@Data
public class SisMclass {
    public String schoolYear;
    public String period;
    public String subject;
    public String proficiency;
    public String score;

    public String proficiencyCode;
    public String csacode;

    //public String _class = "com.bosco.model.ScorableAcademicRecord";
}
