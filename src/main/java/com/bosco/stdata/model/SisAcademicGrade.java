package com.bosco.stdata.model;

import lombok.Data;

@Data
public class SisAcademicGrade {
    public String schoolYear;
    public String term;
    public String courseNumber;
    public String courseName;
    public int grade;

}
