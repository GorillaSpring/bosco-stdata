package com.bosco.stdata.model;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Student {
    private String id;

	private String firstName;
	private String lastName;			
	// Perhaps belongs in Referral packet, since it changes over time
	//private String age;
	private String dob;
	private String gender;
	private String studentId;
	private String school;
	private String schoolId;
	private String districtId;
	private String grade;

	// demographics  -- NOT IN Bosco web.
	private Boolean americanIndianOrAlaskaNative;
	private Boolean asian;
	private Boolean blackOrAfricanAmerican;
	private Boolean nativeHawaiianOrOtherPacificIslander;
	private Boolean white;
	private Boolean hispanicOrLatino;

	
	
	
    private List<Guardian> guardians = new ArrayList();
    private List<String> teacherIds = new ArrayList();

	

}
