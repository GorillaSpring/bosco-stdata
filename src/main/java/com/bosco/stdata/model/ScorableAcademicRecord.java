package com.bosco.stdata.model;

public class ScorableAcademicRecord extends AcademicRecord {



    public String date;
	/*
	 * i.e. English, Math, etc
	 */
	public String subject;
	/*
	 * Allow to simplify subjects, especially for long, repetetive names
	 */
	public String code;

	/*
	 * Allow letter grade or numeric - NOT as of 05/06/24
	 */
	public String score;
	/*
	 * Semester, Quarter, 9weeks, ...
	 */
	public String period;
	public String percentile;
	public String proficiency;
	public String proficiencyCode;
}
