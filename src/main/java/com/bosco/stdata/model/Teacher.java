package com.bosco.stdata.model;

import lombok.Data;

@Data
public class Teacher {

    private String id;
    private String prefix;
	private String firstName;
	private String middleName; // or initial
	private String lastName;
	private String email;
    private String title;

	private String organizationId;
	private String role;
	private String userId;
  
    

}
