package com.bosco.stdata.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SisMclasses {
    // list of records
    public List<SisMclass> records = new ArrayList<>();
    public String type = "mclass";
}
