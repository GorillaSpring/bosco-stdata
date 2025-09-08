package com.bosco.stdata.model;

import lombok.Data;

@Data
public class SisStaar {
    public String date;
    public String subject;
    public String grade;
    public String proficiency;
    public String _class = "com.bosco.model.ScorableAcademicRecord";
}
