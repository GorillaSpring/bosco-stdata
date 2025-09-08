package com.bosco.stdata.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SisMaps {
     // list of records
    public List<SisMap> records = new ArrayList<>();
    public String type = "map";
}
