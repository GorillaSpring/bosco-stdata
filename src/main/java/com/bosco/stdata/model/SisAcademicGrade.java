package com.bosco.stdata.model;

import lombok.Data;

@Data
public class SisAcademicGrade {
    public String schoolYear;
    public String period;
    public String code;
    public String subject;
    public String score;
    public String csacode;

    //public String _class = "com.bosco.model.ScorableAcademicRecord";
}
