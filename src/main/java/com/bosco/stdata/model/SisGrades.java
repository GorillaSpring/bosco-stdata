package com.bosco.stdata.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SisGrades {

    // list of records
    public List<SisAcademicGrade> records = new ArrayList<>();
}
