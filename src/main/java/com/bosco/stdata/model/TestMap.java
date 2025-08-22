package com.bosco.stdata.model;

import lombok.Data;

@Data
public class TestMap {

    public int districtId;
    public String studentNumber;
    public String schoolYear;
    public String term;
    public String subject;
    public String level;
    public int testScore;


    public TestMap() {}

    public TestMap(int districtId, String studentNumber, String schoolYear, String term, String subject, String level, int testScore) {
        this.districtId = districtId;
        this.studentNumber = studentNumber;
        this.schoolYear = schoolYear;
        this.term = term;
        this.subject = subject;
        this.level = level;
        this.testScore = testScore;
    }
}
