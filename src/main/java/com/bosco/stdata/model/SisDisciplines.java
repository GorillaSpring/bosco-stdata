package com.bosco.stdata.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SisDisciplines {
     // list of records
    public List<SisDiscipline> records = new ArrayList<>();
    public String _class = "com.bosco.model.datasource.DisciplinaryCollection";


}
