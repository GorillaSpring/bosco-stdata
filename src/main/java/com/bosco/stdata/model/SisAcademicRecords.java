package com.bosco.stdata.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public
class SisAcademicRecords {
    
    //public List<SisMap> map = new ArrayList<>();
    public SisGrades grades = new SisGrades();
    public SisMaps map = new SisMaps();
    public SisMclasses mclass = new SisMclasses();
    public SisStaars staar = new SisStaars();

    public SisDisciplines discipline = new SisDisciplines();

}
