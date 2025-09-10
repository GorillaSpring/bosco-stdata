package com.bosco.stdata.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SisStudentData {

    // academicRecords
  //public List<SisMap> map = new ArrayList<>();

    public List<SisAcademicGrade> grades = new ArrayList<>();
    public List<SisMap> map = new ArrayList<>();
    public List<SisMclass> mclass = new ArrayList<>();
    public List<SisStaar> staar = new ArrayList<>();
    public List<SisDiscipline> discipline = new ArrayList<>();

//    public SisGrades grades = new SisGrades();
    //public SisMaps map = new SisMaps();
    //public SisMclasses mclass = new SisMclasses();
    //public SisStaars staar = new SisStaars();

    //public SisDisciplines discipline = new SisDisciplines();

    // Next is Telpas

    // public List<SisMap> maps = new ArrayList<>();


    // public List<XSisAcademicGrade> academicGrades = new ArrayList<>();
    // //public List<XSisMap> maps = new ArrayList<>();
    // public List<XSisMclass> mclasses = new ArrayList<>();
    // public List<XSisStaar> staars = new ArrayList<>();
    // public List<XSisDiscipline> disciplines = new ArrayList<>();

}
