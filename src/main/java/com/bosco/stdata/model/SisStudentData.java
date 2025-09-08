package com.bosco.stdata.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SisStudentData {
    public String _id;  // student id  DDD.SSSSS

    // academicRecords

    public SisAcademicRecords academicRecords = new SisAcademicRecords();

     public String _class = "com.bosco.model.datasource.SISData";

    // public List<SisMap> maps = new ArrayList<>();


    // public List<XSisAcademicGrade> academicGrades = new ArrayList<>();
    // //public List<XSisMap> maps = new ArrayList<>();
    // public List<XSisMclass> mclasses = new ArrayList<>();
    // public List<XSisStaar> staars = new ArrayList<>();
    // public List<XSisDiscipline> disciplines = new ArrayList<>();

}
