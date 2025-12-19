package com.bosco.stdata.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SisStudentData {

  // this is what is sent to bosco-web

  public List<SisGrades> grades = new ArrayList<>();
  public List<SisMap> map = new ArrayList<>();
  public List<SisMclass> mclass = new ArrayList<>();
  public List<SisStaar> staar = new ArrayList<>();
  
  public List<SisTelpas> telpas = new ArrayList<>();
  
  public List<SisAttendance> attendance = new ArrayList<>();

  public List<SisDiscipline> discipline = new ArrayList<>();







}
