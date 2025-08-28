package com.bosco.stdata.model;


import lombok.Data;

@Data
public class School {

    private String districtId;

	/*
	 * PRE, PRIMARY, MIDDLE, SECONDARY (, TERTIARY)
	 */
	private String level;

    // WE DO NOT GET FOR Uplift or Celina.
	/*
	 * Different schools can have different grades in their levels
	 */


	 private String schoolCode;

	private String grades;

	

    private String name;

}
