package com.bosco.stdata.distictImports;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bosco.stdata.model.*;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.UserFileService;
import com.bosco.stdata.utils.ImportHelper;

import jakarta.annotation.PostConstruct;

@Component
public class UpliftFiles {
    @Autowired
    ImportRepo importRepo;

    
    @Autowired 
    BoscoApi boscoApi;
    

    private static UpliftFiles i;  // instance

    @PostConstruct
    public void init() {
        System.out.println("UpliftFiles - init()");
        i = this;
    }


    private static String MapProficiencyCode (String one, String two) {
        return "Q1";
    }

    private static String Map_proficiencyCode (String proficiency) {
        // this is simply Quintile 2  => Q2
         String proficiencyCode = switch (proficiency) {
            case "Quintile 1" -> "Q1";
            case "Quintile 2" -> "Q2";
            case "Quintile 3" -> "Q3";
            case "Quintile 4" -> "Q4";
            case "Quintile 5" -> "Q5";
            default -> "??";
        };

        return proficiencyCode;

    }

     private static String MClass_proficiencyCode (String proficiency) {
        // this is simply Quintile 2  => Q2
         String proficiencyCode = switch (proficiency) {
            case "Above Benchmark" -> "A";
            case "At Benchmark" -> "AB";
            case "Below Benchmark" -> "BB";
            case "Well Below Benchmark" -> "WB";
            default -> "??";
        };

        return proficiencyCode;

    }

     

    private static String GeneralCsaCode (String subject) {
        String lowerSubject = subject.toLowerCase();
        if (lowerSubject.startsWith("math"))
            return "M";
        if (lowerSubject.startsWith("read"))
            return "R";
        if (lowerSubject.startsWith("written"))
            return "W";
        if (lowerSubject.startsWith("eng"))
            return "R,W";
        if (lowerSubject.startsWith("lang"))
            return "L";
        if (lowerSubject.startsWith("math"))
            return "M";
        if (lowerSubject.startsWith("science"))
            return "C";
        if (lowerSubject.startsWith("social"))
            return "S";
        if (lowerSubject.startsWith("adaptive"))
            return "A";
        if (lowerSubject.startsWith("behavior"))
            return "B";
        if (lowerSubject.startsWith("cognitive"))
            return "G";
        
        return "NOT_FOUND";
    }

    private static String MapMclass_CsaCodeFromCourseName (String courseName) {
        // This will return "" for N/A - IE ignore
        // It will return "NOT_FOUND"  if not found!

         String csaCode = switch (courseName) {
            case "English II MYP" -> "R,W";
            case "Algebra II MYP" -> "M";
            case "Algebra II" -> "M";
            case "Algebra I" -> "M";
            case "Chemistry MYP" -> "C";
            case "US Government" -> "S";
            case "US Hist Since Recon MYP (YL)" -> "S";
            case "IB Lang A: Lang & Lit SL2" -> "R,W";
            case "IB Math: AI SL2" -> "M";
            case "IB Theory of Knowledge 12 (Sem)" -> "";   // This is a known DO NOT LOAD.
            case "IB Theory of Knowledge 11 (Sem)" -> "";   // This is a known DO NOT LOAD.
            case "IB Global Politics HL2" -> "S";
            case "IB History of Americas HL2" -> "S";
            case "IB History of Americas HL1" -> "S";
            case "IB Lang A: Lang & Lit HL2" -> "R,W";
            case "IB Lang A: Lang & Lit SL1" -> "R,W";
            case "IB Environ Sys & Soc SL1" -> "C,S";
            case "IB Global Politics Higher Level 1" -> "S";
            case "IB Math: AI SL1" -> "M";
            case "IB Biology Standard Level 1" -> "C";
            case "IB Psychology HL1" -> "S";
            case "Geometry MYP" -> "M";
            case "IB Lang A: Lang & Lit HL1" -> "R,W";
            case "Precalculus" -> "M";
            case "English I MYP" -> "R,W";
            case "Biology MYP" -> "C";
            case "Biology" -> "C";
            case "World History Studies MYP" -> "S";
            case "Ind Study in Math I (YL)" -> "M";
            case "English Lang Arts & Read, Grade 8 MYP" -> "R,W";
            case "Algebra I MYP MS" -> "M";
            case "Science, Grade 8 MYP" -> "C";
            case "US History, Grade 8 MYP" -> "S";
            case "English Lang Arts & Read, Grade 6 MYP" -> "R,W";
            case "Math, Grade 6 MYP" -> "M";
            case "Science, Grade 6 MYP" -> "C";
            case "Algebra I MYP" -> "M";
            case "Reading I" -> "R,W";
            case "English Lang Arts & Read, Grade 7 MYP" -> "R,W";
            case "English II" -> "R,W";
            case "Math, Grade 8 MYP" -> "M";
            case "Science, Grade 7 MYP" -> "C";
            case "Math, Grade 7 MYP" -> "M";
            case "Texas History, Grade 7 MYP" -> "S";
            case "English Language Arts & Reading, Grade 3" -> "R,W";
            case "Social Studies, Grade 3" -> "S";
            case "Math, Grade 7" -> "M";
            case "English Language Arts & Reading, Grade 5" -> "R,W";
            case "Science, Grade 5" -> "C";
            case "Social Studies, Grade 5" -> "S";
            case "World History, Grade 6 MYP" -> "S";
            case "English Language Arts & Reading, Grade 4" -> "R,W";
            case "Science, Grade 4" -> "C";
            case "Social Studies, Grade 4" -> "S";
            case "Math, Grade 4" -> "M";
            case "Math, Grade 3" -> "M";
            case "Math, Grade 5" -> "M";

            case "Geometry" -> "M";

            case "English III" -> "R,W";
            case "Environmental Systems" -> "C";
            case "World Geo Studies" -> "S";
            case "English IV" -> "R,W";
            case "Anatomy and Physiology" -> "C";
            case "World Geo Studies MYP" -> "S";
            case "English Language Arts & Reading, Grade 2" -> "R,W";
            case "Math, Grade 2" -> "M";
            case "Science, Grade 2" -> "C";
            case "Social Studies, Grade 2" -> "S";
            case "English Language Arts & Reading, KG" -> "R,W";
            case "Math, Kindergarten" -> "M";
            case "Science, Kindergarten" -> "C";
            case "Social Studies, Kindergarten" -> "S";
            case "English Language Arts & Reading, Grade 1" -> "R,W";
            case "Math, Grade 1" -> "M";
            case "Science, Grade 1" -> "C";
            case "Social Studies, Grade 1" -> "S";

            case "Physics" -> "C";
            case "Science, Grade 3" -> "C";

            case "Chemistry" -> "C";
            case "US Hist Since Recon" -> "S";
            case "World Hist Studies" -> "S";
            case "Geometry MYP MS" -> "M";

            case "Personal Financial Lit & Econ (Sem)" -> "";



            default -> "NOT_FOUND";
        };

        return csaCode;


    }

    // this should be generic.  Just need to be sure of the date or pass in the pattern .
    private static String SchoolYearFromDate (String dateString) {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
         try {
            LocalDate date = LocalDate.parse(dateString, formatter);
            Month month = date.getMonth();
            if (month == Month.SEPTEMBER || month == Month.OCTOBER || month == Month.NOVEMBER || month == Month.DECEMBER)
                return date.getYear() + " - " + date.getYear() + 1;
            else
                return date.getYear() - 1 + " - " + date.getYear();
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
            return "";
        }
    }

    private static String Staar_SubjectFromCode (String code) {

        String subject = switch (code) {
            case "M" -> "Math";
            case "R" -> "Reading";
            case "S" -> "Science";
            case "A1" -> "Algebra I";
            case "B" -> "Biology";
            case "E1" -> "English I";
            case "E2" -> "English II";
            case "USH" -> "US History";
            case "SS" -> "Social Studies";
            default -> "NOT_FOUND";
        };

        return subject;


    }

    private static String Staar_CsaCodeFromCode (String code) {

        String csaCode = switch (code) {
            case "M", "A1" -> "M";
            case "R" -> "R";
            case "S", "B" -> "C";
            case "E1", "E2" -> "L";
            case "USH", "SS" -> "S";
            default -> "NOT_FOUND";
        };

        return csaCode;
    }

    private static String Staar_ProficiencyCodeFromProficiency (String proficiency) {

        String pc = switch (proficiency) {
            case "Did Not Meet" -> "DN";
            case "Approaches" -> "AP";
            case "Masters" -> "MA";
            case "Meets" -> "MT";
            default -> "NOT_FOUND";
        };

        return pc;


    }
    

    public static ImportResult Import(String importDefId) {

        ImportResult result = new ImportResult();

        try {

             ImportDefinition importDef = i.importRepo.getImportDefinition(importDefId);

            int baseImportId = importDef.getBaseImportId();


            List<ImportSetting> importSettings = i.importRepo.getImportSettings(importDefId);

            int districtId = importDef.getDistrictId();
            Boolean setNoEmails = importDef.getSetNoEmails();

            int importId = i.importRepo.prepImport(districtId, "Import for " + importDefId);

            

            result.importId = importId;
            result.districtId = districtId;
            result.baseImportId = baseImportId;

            
		    //String baseFileFolder = "C:/test/uplift/" + subFolder + "/";
            String baseFileFolder = ImportHelper.ValueForSetting(importSettings, "baseFolder");

            String archiveFolder =  ImportHelper.ValueForSetting(importSettings, "archiveFolder");



              // Before we start, lets make sure there are files in the baseFolder
              // additional users NO
              // additional_users.csv  NO
            String[] files = {"academics_grades.csv", "attendance.csv", "campuses.csv", "discipline.csv", "mCLASS and MAP.csv", "state_assessment.csv", 
            "students.csv", "teacherstudentassignements.csv", "TELPAS.csv", "users.csv"};
            if (!ImportHelper.CheckFilesExist(baseFileFolder, files)) {
                throw new FileNotFoundException("One or more import files missing!");
            }

        


            LocalDateTime startDateTime = LocalDateTime.now();
            
            System.out.println("Import Id is : " + importId + " For District " + districtId);

		    UserFileService msp = new UserFileService();

            System.out.println("Importing campuses File");

            List<String[]> data = msp.readCsvFile( baseFileFolder + "campuses.csv");

            int counter1 = 0;
            int counter2 = 0;

            String[] fr = data.removeFirst();


            String [] colNames = {"schoolcode", "schoolname"};

            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : campuses.csv does not match column specs" );

          
          
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {
                    i.importRepo.saveSchool(row[0], row[1], row[0]);
                    counter1++;
                }

            }


            i.importRepo.logInfo("Imported Schools : " + counter1);

            System.out.println("Importing Students File");

            data = msp.readCsvFile( baseFileFolder + "students.csv");


            // studentId                        0
            // lastname                         1
            // firstname                        2
            // dob                              3
            // gender                           4
            // schoolcode                       5
            // gradecode                        6
            // is504                            7    ***
            // englishlanguagelearner           8     ***
            // guardiantype                     9    ****
            // guardianfirstname                10
            // guardianlastname                 11
            // guardianemail                    12



            fr = data.removeFirst();
            colNames = new String[]{"studentid", "lastname", "firstname", "dob", "gender", "schoolcode", "gradecode", "is504", "englishlanguagelearner", "guardiantype", "guardianfirstname", "guardianlastname", "guardianemail"};

            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : students.csv does not match column specs" );

            counter1 = 0;

            //data.forEach(row -> {
            for (String [] row : data) {

                if (!row[0].isBlank()) 
                {

                    Boolean is504 = row[7].equals("Yes");
                    Boolean isEsl = row[8].equals("Yes");

                    String email = row[12];
                    // To do if we need to scramble email , do it here.
                    if (setNoEmails && email.length() >= 4) {
                        String trimedEmail = email.substring(0, email.length() - 4);
                        email = trimedEmail + "_no.no";
                    }



                    //String sourceId, String studentId, String firstName, String lastName, String grade, String schoolCode

                    i.importRepo.saveStudent(row[0], row[0], row[2], row[1], row[6], row[5]);

                    //i.importRepo.saveStudent(s);
                    i.importRepo.saveStudentDemographics(row[0], row[3], row[4], false, false, false, false, false, false,
                    isEsl, is504, false
                    
                    );

                    // 504
                    // if (row[7] == "Yes")
                    //     i.importRepo.saveStudentProperty(row[0], "is504", "1");

                    // // isEsl
                    // if (row[8] == "Yes")
                    //     i.importRepo.saveStudentProperty(row[0], "isEsl", "1");

                    
                    // String sourceId, String guardianId, String studentId, String firstName, String lastName, String email, String type
                    i.importRepo.saveGuardian("Guardian_" + row[0], "G_" + row[0],  row[0],  row[10], row[11], email, row[9]);

                    
                    //i.importRepo.saveGuardian(g);

                    counter1++;
                }
                
            };

            i.importRepo.logInfo("Imported Students : " + counter1);




            System.out.println("Importing Users File");

            data = msp.readCsvFile( baseFileFolder + "users.csv");


            fr = data.removeFirst();
            colNames = new String[]{"userid", "lastname", "firstname", "email", "schoolcode", "userlevel"};

            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : users.csv does not match column specs" );


            // userid       0
            // lastname     1
            // firstname    2
            // email        3
            // schoolcode   4
            // userlevel    5


            counter1 = 0;
            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {

                    String email = row[3];

                    if (setNoEmails && email.length() >= 4) {
                        String trimedEmail = email.substring(0, email.length() - 4);
                        email = trimedEmail + "_no.no";
                    }

                    
                    // sourceid, teacherId, firstname, lastname,  email
                    //Teacher t = new Teacher(row[0], row[0], row[2], row[1], row[3]);
                    i.importRepo.saveTeacher(
                        row[0], row[0], row[2], row[1], email
                    );
                    counter1++;
                }
            };

            i.importRepo.logInfo("Imported Teachers : " + counter1);


            System.out.println("Importing teacherstudentassignements File");

            data = msp.readCsvFile( baseFileFolder + "teacherstudentassignements.csv");

            // studentid
            // userid

            fr = data.removeFirst();

            colNames = new String[]{"studentid", "userid"};

            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : teacherstudentassignements.csv does not match column specs" );

            counter1 = 0;

            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {

                    i.importRepo.saveStudentTeacher(row[0], row[1]);
                    counter1++;

                }

            
            };


            i.importRepo.logInfo("Imported Student Teachers : " + counter1);



            System.out.println("Importing SIS data");


            i.importRepo.sisPrepData();

            // WE NEED TO RE DO THESE ONES.
            // mClass and map
             System.out.println("Importing mCLASS and MAP File");

            data = msp.readCsvFile( baseFileFolder + "mCLASS and MAP.csv");


            fr = data.removeFirst();
            colNames = new String[]{"StudentID", "Test", "SchoolYear", "TermName", "Subject", "Level", "TestScore"};

            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : mCLASS and MAP.csv does not match column specs" );

            // studentId            0
            // test                 1       MAP or MCLASS
            // schoolYear           2       2022-2023
            // term (period)        3
            // subject              4
            // level (proficiency)  5     Quintile 2   (MAP)   or Well Below Benchmark (mClass)
            // testScore  (score)   6       44




            counter1 = 0;
            counter2 = 0;
            
            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {
                    // they combine map and mclass

                    String proficiencyCode = "";
                    String csaCode = GeneralCsaCode(row[4]);

                    if (csaCode.equals("NOT_FOUND")) {
                        i.importRepo.logError("Found Map or Mclass with unkown subject " + row[4]);
                    }
                    else {

                        int score = Integer.parseInt (row[6]);

                        switch (row[1]) {
                            case "MAP":

                                proficiencyCode = Map_proficiencyCode(row[5]);
                                // So proficiency is "Quintile 2".   We could map that to something better here!


                                // TODO : Get proficiencyCode and csaCode
                                i.importRepo.sisMapAdd(row[0], row[2], row[3], row[4], row[5], proficiencyCode, score, csaCode);

                                counter1++;
                                break;
                            case "MCLASS":
                                // TODO : Get proficiencyCode and csaCode
                                proficiencyCode = MClass_proficiencyCode(row[5]);
                                i.importRepo.sisMclassAdd(row[0], row[2], row[3], row[4], row[5], proficiencyCode,  score, csaCode);

                                counter2++;
                                break;

                        
                            default:
                                System.out.println("Unknown Type in file " + row[1]);
                                break;
                        }
                    }
                }
            };

            i.importRepo.logInfo("Imported MAP : " + counter1);
            i.importRepo.logInfo("Imported mClass : " + counter2);

            
            


            // Academic Grades
                // mClass and map
            System.out.println("Importing academics_grades File");

            data = msp.readCsvFile( baseFileFolder + "academics_grades.csv");


            fr = data.removeFirst();

            colNames = new String[]{"studentid", "coursenumber", "coursename", "coursegrade", "schoolyear", "term"};

            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : academics_grades.csv does not match column specs" );


            // studentId            0
            // coursenumber (code)  1
            // coursename (subject) 2
            // coursegrade (score)  3
            // schoolyear           4      2024-2025
            // term (period)        5




            counter1 = 0;
            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) {
                    // do not import if grade is blank.
                    if (!row[3].isBlank()) {
                        int score = Integer.parseInt (row[3]);
                        String csaCode = MapMclass_CsaCodeFromCourseName(row[2]);

                        ///System.out.println("Got Code : " + csaCode);

                        if (!csaCode.isEmpty())
                        {
                            if (csaCode.equals("NOT_FOUND")) {
                                // this is an EXCEPTION.  Do not load, but log
                                i.importRepo.logError("Academic Grade - Found Unknown Course : " + row[2]);

                                
                            }
                            else {
                                // String studentNumber, String schoolYear, String period, String code, String subject, int score, String csaCode
                                i.importRepo.sisGradeAdd(row[0], row[4], row[5], row[1], row[2], score, csaCode);

                                counter1++;

                            }

                        }
                    

                    }
                }
            };

            i.importRepo.logInfo("Imported academics_grades : " + counter1);


            

            System.out.println("Importing state_assessment File");

            data = msp.readCsvFile( baseFileFolder + "state_assessment.csv");


            fr = data.removeFirst();
            colNames = new String[]{"studentid", "testdate", "stateassessmentsubject", "gradeduringassessment", "stateassessmentscore"};

            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : state_assessment.csv does not match column specs" );

            // studentId    0
            // testdate
            // stateassessmentsubject - code        2
            // gradeduringassessment- grade         3
            // stateassessmentscore- proficiency    4




            counter1 = 0;
            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) {


                    // calculate the year based on the date.
                    String schoolYear = SchoolYearFromDate(row[1]);

                    String proficiencyCode = Staar_ProficiencyCodeFromProficiency(row[4]);
                    String csaCode = Staar_CsaCodeFromCode(row[2]);
                    String subject = Staar_SubjectFromCode(row[2]);


                    if (
                        proficiencyCode.equals("NOT_FOUND")   
                        ||
                        csaCode.equals("NOT_FOUND")
                        ||
                        subject.equals("NOT_FOUND")

                    )
                    {
                        i.importRepo.logError("Star Mapping lookup failed: " + row[2] + " - [" + proficiencyCode + "] [" + csaCode + "] [" + csaCode + "]" );

                        System.out.println("Star Mapping lookup failed: " + row[2] + " - [" + proficiencyCode + "] [" + csaCode + "] [" + csaCode + "]" );
                    }


                    //String studentNumber, String testDate, String schoolYear, String subject, String code, String grade, String proficiency, String proficiencyCode, String csaCode


                    i.importRepo.sisStaarAdd(row[0], row[1], schoolYear, subject, row[2], row[3], row[4], proficiencyCode, csaCode);
                    counter1++;
                }
            };

            i.importRepo.logInfo("Imported state_assessments : " + counter1);


            System.out.println("Importing discipline File");

            data = msp.readCsvFile( baseFileFolder + "discipline.csv");


            fr = data.removeFirst();

            colNames = new String[]{"studentid", "issdays", "ossdays", "aepdays", "schoolyear"};

            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : discipline.csv does not match column specs" );


            // studentId    0
            // issdays
            // ossdaysw
            // apedays
            // schoolyear




            counter1 = 0;
            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) {


                    // TODO: THIS IS AN ISSUE TO DEAL WITH LATER
                    String grade = "8"; 
                    // TODO: We need to lookup the grade for the student!

                    // String studentNumber, String issDays, String ossDays, String aepDays, String grade, String schoolYear
                    i.importRepo.sisDiscipline(row[0], row[1], row[2], row[3], grade, row[4]);
                    counter1++;
                }
            };

            i.importRepo.logInfo("Imported discipline : " + counter1);


            System.out.println("Sys Post Data");
            i.importRepo.sisPostData();


            // Now we move the files to the archive Folder

            //ImportHelper.MoveFiles(baseFileFolder, archiveFolder);

            i.importRepo.logInfo("Moved Files to archive");

               // do the diff

			if (baseImportId == 0) {
				i.importRepo.logInfo("This is the BASE Import");
                i.importRepo.setAllNewImports();
            }
            else {
                i.importRepo.logInfo("Doing Diff with " + baseImportId);

                i.importRepo.diffImports(baseImportId);
            }

            // validation on the data.
            // check number of diffs vs the cutoff.


            // this will mark the importId as the base.
            i.importRepo.setImportBase(importDefId);




        
            LocalDateTime endDateTime = LocalDateTime.now();
    
            Duration duration = Duration.between(startDateTime, endDateTime);

            
            i.importRepo.logInfo("Import " + importDefId + " (" + importId + ") Complete in : " + duration.toSeconds() + " Seconds" );
            System.out.println ("Import ID is: " + importId);

            i.boscoApi.sendImportToBosco(importId, baseImportId);

            result.success = true;


        }
        catch (Exception ex) {
            i.importRepo.logError(ex.toString());

            result.errorMessage = ex.toString();
            result.success = false;

            System.out.println(ex.toString());
        }

        return result;


	}



}
