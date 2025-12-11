package com.bosco.stdata.teaModel;
import lombok.Data;

@Data
public class DisciplineLedger {
    // StudentSourceID,StudentNumber,Action Description,Days,SchoolYear

    public String studentSourceID;
    public String studentNumber;
    public String action;              // this is Action Description
    public String days;
    public String schoolYear;

}


