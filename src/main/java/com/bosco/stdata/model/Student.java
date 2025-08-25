package com.bosco.stdata.model;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Student {
    private String id;

	private String firstName;
	private String middleName;
	private String lastName;
	private String suffixName;
	// Perhaps belongs in Referral packet, since it changes over time
	//private String age;
	private String dob;
	private String gender;
	private String ethnicity;
	private String studentId;
	private String school;
	private String schoolId;
	private String districtId;
	// Perhaps belongs in Referral packet, since it changes over time
	private String schoolYear;
	// Perhaps belongs in Referral packet, since it changes over time
	private String teacher;
	// Perhaps belongs in Referral packet, since it changes over time
	private String grade;


	// Educational Placement of 0 means it is not set or is unknown
	private int educationalPlacement;
	// From SIS and/or Surveys

    private List<Guardian> guardians = new ArrayList();
    private List<String> teacherIds = new ArrayList();
}
