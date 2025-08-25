package com.bosco.stdata.model;

import java.util.ArrayList;
import java.util.List;

public class SisStudentData {
    public String id;  // student id  DDD.SSSSS
    public List<SisAcademicGrade> academicGrades = new ArrayList<>();
    public List<SisMap> maps = new ArrayList<>();
    public List<SisMclass> mclasses = new ArrayList<>();
    public List<SisStaar> staars = new ArrayList<>();
    public List<SisDiscipline> disciplines = new ArrayList<>();

}
