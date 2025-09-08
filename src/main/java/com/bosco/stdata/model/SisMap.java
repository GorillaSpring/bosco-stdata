package com.bosco.stdata.model;

import lombok.Data;

@Data
public class SisMap {

    public String schoolYear;
    public String period;
    public String subject;
    public String proficiency;
    public String score;

    public String _class = "com.bosco.model.ScorableAcademicRecord";
}
