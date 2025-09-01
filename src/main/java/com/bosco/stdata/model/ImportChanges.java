package com.bosco.stdata.model;

import lombok.Data;

@Data
public class ImportChanges {

    public int baseStudentCount;
    public int importStudentCount;
    public int importStudentChanged;

}
