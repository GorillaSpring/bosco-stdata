package com.bosco.stdata.model;

import lombok.Data;

@Data
public class SisDiscipline {
    public SisDisciplineCounts counts = new SisDisciplineCounts();
    public String schoolYear;


    public String grade;  // no data for this!
    //public String grade;  this is kids grade (ie 8th)

     //public String _class = "com.bosco.model.CountableAcademicRecord";
    

}
