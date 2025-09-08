package com.bosco.stdata.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SisStaars {
     // list of records
    public List<SisStaar> records = new ArrayList<>();
    public String type = "staar";
}
