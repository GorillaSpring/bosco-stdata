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
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.model.*;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.UserFileService;
import com.bosco.stdata.sisDataFiles.CsvFiles;
import com.bosco.stdata.utils.ImportHelper;
import com.bosco.stdata.utils.MappingHelper;
import com.bosco.stdata.utils.TeaStaarFlatFileReader;

import jakarta.annotation.PostConstruct;

@Component
public class AllenFiles {


    
    @Autowired
    ImportRepo importRepo;

    
    @Autowired 
    BoscoApi boscoApi;
    

    private static AllenFiles i;

    private static Boolean setNoEmails; 

    private static UserFileService msp;

    @PostConstruct
    public void init() {
        System.out.println("Allen Files - init()");
        i = this;
    }




   

    public static ImportResult Import(String importDefId) {

        Boolean isRoster = true;
        Boolean isSisData = true;

        ImportResult result = new ImportResult();

        try {

             ImportDefinition importDef = i.importRepo.getImportDefinition(importDefId);



            List<ImportSetting> importSettings = i.importRepo.getImportSettings(importDefId);

            int districtId = importDef.getDistrictId();
            setNoEmails = importDef.getSetNoEmails();


		    //String baseFileFolder = "C:/test/uplift/" + subFolder ;
            String baseFileFolder = ImportHelper.ValueForSetting(importSettings, "baseFolder");

            String archiveFolder =  ImportHelper.ValueForSetting(importSettings, "archiveFolder");

            
            int importId = i.importRepo.prepImport(districtId, importDefId, isRoster, isSisData,  "Allen Files (Roster) " + baseFileFolder);

            result.importId = importId;
            result.districtId = districtId;


            
            

              // Before we start, lets make sure there are files in the baseFolder
              // additional users NO
              // additional_users.csv  NO
              //  "attendance.csv",  THIS IS Yearly?
            // String[] files = {"academics_grades.csv",  "campuses.csv", "discipline.csv", "mCLASS and MAP.csv", "state_assessment.csv", 
            // "students.csv", "teacherstudentassignements.csv", "TELPAS.csv", "users.csv"};
            // if (!ImportHelper.CheckFilesExist(baseFileFolder, files)) {
            //     throw new FileNotFoundException("One or more import files missing!");
            // }

        


            LocalDateTime startDateTime = LocalDateTime.now();
            
            msp = new UserFileService();

            System.out.println("Import  For District " + districtId);


             int counter2 = 0;



         


    
        importSchools(baseFileFolder + "/campuses.csv");



            // USERS


        importUsers( baseFileFolder + "/users.csv");

        CsvFiles.LoadAllenStudents(districtId, baseFileFolder + "/students.csv", setNoEmails);

        importStudentTeachers(baseFileFolder + "/teacherstudentassignments.csv");

        // importSchools();
        // importAttendance();
        // importDiscipline();
        // importMap();
        // importStaar();
        // importGrades();

        // campuses  ** DO ABOVE and COMMENT

        importGrades(districtId, baseFileFolder + "/academics_grades_current_year.csv");
        importGrades(districtId, baseFileFolder + "/academics_grades_previous_years.csv");



        importAttendance(baseFileFolder + "/attendance_current_year.csv", false);
        importAttendance(baseFileFolder + "/attendance_history.csv", true);


        //System.out.println ("  **** TODO: attendance_history *****");

        //importAttendance(baseFileFolder + "attendance_history.csv");


        // attendance_current_year.csv
        //  attendance_history.csv      ** BAD **

        importDiscipline(baseFileFolder + "/discipline_current_year.csv");
        importDiscipline(baseFileFolder + "/discipline_history.csv");


        // discipline_current_year
        // discipline_history.csv

        importMap(baseFileFolder + "/MAP.csv");

        // MAP.csv
        importStaar(baseFileFolder + "/staar_current_year.csv");
        importStaar(baseFileFolder + "/staar_history.csv");

        // staar_current_year.csv
        // staar_history.csv


        // academics_grades_current_year.csv
        // academics_grades_previous_years.csv


            // System.out.println("Importing SIS data");

            // 
            
            


            



            

         


            

            // Get the mappings ready.adminController

           
            



           

           



           

           




            // // We have done both imports and sisData

            // // Now we move the files to the archive Folder

            // //ImportHelper.MoveFiles(baseFileFolder, archiveFolder);

            // i.importRepo.logInfo("Moved Files to archive");

            //    // do the diff




               
            // System.out.println("Sys Post Data");


            i.importRepo.prepSendBosco(districtId, importDefId, isRoster, isSisData);
            // NOW WE CAN CHECK CHANGES and BAIL IF NEED BE.


            if (!importDef.getForceLoad() && isRoster) {
                String checkDeltas = i.importRepo.checkImportDeltas(districtId, importDefId);
                if (!checkDeltas.equals("OK")) {
                    throw new Exception("Check Import Delta failed: " + checkDeltas);
                }

            }


        
           
            

             i.boscoApi.sendImportToBosco(districtId);

             i.importRepo.postSendBosco(districtId, importDefId, isRoster, isSisData);

             LocalDateTime endDateTime = LocalDateTime.now();
    
            Duration duration = Duration.between(startDateTime, endDateTime);

            i.importRepo.logInfo("Import " + importDefId + " () Complete in : " + duration.toSeconds() + " Seconds" );


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



    private static void importSchools(String filePath) throws Exception {
        List<String[]> data;
        String[] fr;
        String[] colNames;
        int counter1;
        System.out.println("Importing campuses File");

        data = msp.readCsvFile(filePath);


        fr = data.removeFirst();


        colNames =  new String[]{"CampusName",  "CampusCode" ,  "Grades"};

        counter1 = 0;

        if (!ImportHelper.CheckColumnHeaders(fr, colNames))
            throw new Exception("File : campuses.csv does not match column specs" );

        
        
        for (String [] row : data) {
            if (!row[0].isBlank()) 
            {
                i.importRepo.saveSchool(row[1], row[0], row[1]);
                counter1++;
            }

        }


        i.importRepo.logInfo("Imported Schools : " + counter1);




    


    }




    private static void importUsers(String filePath) throws Exception {
        List<String[]> data;
        String[] fr;
        String[] colNames;
        int counter1;
        System.out.println("Importing Users File");

        data = msp.readCsvFile(filePath);


        fr = data.removeFirst();
        colNames = new String[]{"UserID","LastName","FirstName","Email","SchoolCode","UserLevel","Position Assignment Desc"};

        if (!ImportHelper.CheckColumnHeaders(fr, colNames))
            throw new Exception("File : users.csv does not match column specs" );


         // userid       0
        // // lastname     1
        // // firstname    2
        // // email        3
        // // schoolcode   4
        // // userlevel    5
        //  Position Assignment Desc   6


        counter1 = 0;
        //data.forEach(row -> {
        for (String [] row : data) {
            if ( row.length > 1 && !row[1].isBlank()) 
            {

                //System.out.println(row[0] + " " + row[1] + " "+ row[2] + " "+ row[3] + " "+ row[4] + " "+ row[5] + " ");

                String email = row[3];

                if (!email.isBlank()) {
                    if (setNoEmails && email.length() >= 4) {
                        String trimedEmail = email.substring(0, email.length() - 4);
                        email = trimedEmail + "_no.no";
                    }

                    
                    // sourceid, teacherId, firstname, lastname,  email
                    //Teacher t = new Teacher(row[0], row[0], row[2], row[1], row[3]);


                    // This will generate the users file.  DONE already.

                    // if (!row[5].equals("T")) {
                    //     String schoolName = i.importRepo.schoolNameForCode(row[4]);
                    //     System.out.println(row[3] + "," + row[1] + "," + row[2] + "," + row[4] + "," + schoolName + "," + row[5] + ","+ row[6] );
                    //     // get email, school (by name)
                    // }

                    

                    // String sourceid, String teacherId, String firstname, String lastname, String email
                    // String sourceId, String teacherId, String firstName, String lastName, String email
                    i.importRepo.saveTeacher(
                        row[0], row[0], row[2], row[1], email, row[4]
                    );
                    counter1++;
                }
            }
        };

        i.importRepo.logInfo("Imported Teachers : " + counter1);
    }


    private static void importStudentTeachers(String filePath) throws Exception {
        List<String[]> data;
        String[] fr;
        String[] colNames;
        int counter1;

        System.out.println("Importing teacherstudentassignements File");

            data = msp.readCsvFile( filePath);

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
                    // we can not do this directly anymore
                    // need to add studentClass 
                    // then add teacherClass

                    // so, here the teacherId is actaully the class for them.
                    i.importRepo.saveTeacherClass(row[1], row[1]);
                    i.importRepo.saveStudentClass(row[0], row[1]);

                    //i.importRepo.saveStudentTeacher(row[0], row[1]);
                    counter1++;

                }

            
            };


            i.importRepo.logInfo("Imported Student Teachers : " + counter1);

            i.importRepo.buildStudentTeacher();


    }


    private static void importAttendance(String filePath, Boolean isHistory) throws Exception {
        List<String[]> data;
        String[] fr;
        String[] colNames;
        int counter1;
        int counter2;

        


        
        SisAttendance sa;
        HashMap<String, SisAttendance> studentAttendance = new HashMap<>();


        System.out.println ("Importing attendance_current_year");

        // for histroy.adminController
        // event = A
        // studentNumber = row[1]
        // date = row[2]   (may be different format, but should work.
        // rowPeriod = row[5]


        data = msp.readCsvFile(filePath);

        fr = data.removeFirst();

        System.out.println("Starting Data");

        Boolean rowValid = true;

        counter1 = 0;
        counter2 = 0;

        String studentNumber = "";
        String event = "A";
        String date = "";
        String rowPeriod = "";

        for (String [] row : data) {

            counter1++;
            rowValid = true;

            if (isHistory) {
                studentNumber = row[1];     // 020281
                event = "A";  // don't need to do this
                date = ImportHelper.DateToStdFormat(row[2]);           // 08/11/2025 
                rowPeriod = row[5];
            }
            else {
                studentNumber = row[0];     // 020281
                event = row[1].equals("Tardy") ? "T" : "A";  // Tardy OR Absence
                date = ImportHelper.DateToStdFormat(row[2]);           // 08/11/2025
                rowPeriod = row[3];      // 3  OR blank  (for abesent)

            }

            if (rowPeriod.isEmpty()) {
                rowPeriod = "Full Day";
            }


            // ok, lets try to buld them up.

            String key = studentNumber + ":" + date + ":" + event;
            //System.out.println("Key : " + key);

            if (studentAttendance.containsKey(key)) {
                //System.out.println(" -- Found key");
                sa = studentAttendance.get(key);
                sa.period += ", " + rowPeriod;
            }
            else {

                String schoolYear = MappingHelper.SchoolYearFromDateYMD(date);

                sa = new SisAttendance();
                sa.date = date;
                sa.event = event;
                sa.schoolYear = schoolYear;
                sa.period = rowPeriod;
                studentAttendance.put(key, sa);
            }

            



        }

        // now lest see what we got.

        counter2 = 0;
        for (String key : studentAttendance.keySet()) {
            sa = studentAttendance.get(key);
            counter2++;
            

            studentNumber = key.split(":")[0];

            // System.out.println("Key: " + key + " - " + studentNumber);


            String formattedDate = sa.date; // ImportHelper.DateToStdFormat(sa.date);

            //System.out.println(studentNumber + " : " + formattedDate + " " + sa.schoolYear + " : " + sa.period);

            i.importRepo.sisAttendanceAdd(studentNumber, sa.event, sa.schoolYear, formattedDate, sa.period);


        
        }

        // 

        System.out.println( "   ROWS: " + counter1 + "  Loaded: " + counter2);

        System.out.println("----------------- END -------------");

        System.out.println ("DONE Importing attendance_current_year");
    }

    private static void importDiscipline(String filePath) throws Exception {
        List<String[]> data;
        String[] fr;
        String[] colNames;
        int counter1;
        int counter2;

        System.out.println("Importing " + filePath);

        data = msp.readCsvFile( filePath);


        fr = data.removeFirst();

        colNames = new String[]{"StudentID" ,"ISSDays","OSSDays","AEPDays","SchoolYear"};

        if (!ImportHelper.CheckColumnHeaders(fr, colNames))
            throw new Exception("File : discipline.csv does not match column specs" );


        // studentId    0
        // issdays
        // ossdaysw
        // apedays
        // schoolyear



        counter1 = 0;
        String grade = "";   // we do not import this.
        //data.forEach(row -> {
        for (String [] row : data) {
            if (!row[0].isBlank()) {



                //String grade = i.importRepo.gradeForStudentId(districtId + "." + row[0]);

                // TODO: We need to lookup the grade for the student!

                // String studentNumber, String issDays, String ossDays, String aepDays, String grade, String schoolYear
                i.importRepo.sisDiscipline(row[0], row[1], row[2], row[3], grade, row[4]);
                counter1++;
            }
        };

        i.importRepo.logInfo("Imported " + filePath + " : " + counter1);
    }
    
    private static void importMap(String filePath) throws Exception {
        List<String[]> data;
        String[] fr;
        String[] colNames;
        int counter1;
        int counter2;
        
        System.out.println("Importing MAP");

        data = msp.readCsvFile( filePath);


        fr = data.removeFirst();
        colNames = new String[]{"student_id","test_type","school_year","term_name","subject","level","test_score"};

        if (!ImportHelper.CheckColumnHeaders(fr, colNames))
            throw new Exception("File : MAP.csv does not match column specs" );

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
                String proficiency = "";
                String csaCode = MappingHelper.GeneralCsaCode(row[4]);

                if (csaCode.equals("NOT_FOUND")) {
                    i.importRepo.logError("Found Map or Mclass with unkown subject " + row[4]);
                }
                else {

                    int score = Integer.parseInt (row[6]);

                    switch (row[1]) {
                        case "MAP":

                            if (!row[5].isEmpty()) {
                                proficiency = MappingHelper.MapProficiency(row[5]);
                                proficiencyCode = MappingHelper.Map_proficiencyCode(proficiency);


                                i.importRepo.sisMapAdd(row[0], row[2], row[3], row[4], proficiency, proficiencyCode, score, csaCode);

                                counter1++;
                            }
                            break;
                        case "MCLASS":
                            // TODO : Get proficiencyCode and csaCode
                            // THERE ARE NO MCLASSS IN FILE AT THE MOMENT
                            throw new Exception("FOUND MCLASS IN FILE");
                            // proficiencyCode = MappingHelper.MClass_proficiencyCode(row[5]);
                            // if (proficiencyCode != "") {
                            //     i.importRepo.sisMclassAdd(row[0], row[2], row[3], row[4], row[5], proficiencyCode,  score, csaCode);
                            //     counter2++;
                            // }
                            //break;

                    
                        default:
                            System.out.println("Unknown Type in file " + row[1]);
                            break;
                    }
                }
            }
        };

        i.importRepo.logInfo("Imported MAP : " + counter1);
        i.importRepo.logInfo("Imported mClass : " + counter2);
    }

    private static void importStaar(String filePath) throws Exception {
        List<String[]> data;
        String[] fr;
        String[] colNames;
        int counter1;
        int counter2;

            //This has NONE  :   staar_current_year.csv

        System.out.println("Importing staar_history File");

        data = msp.readCsvFile(filePath);


        fr = data.removeFirst();
        //colNames = new String[]{"StudentID","TestDate","StateAssessmentSubject","GradeDuringAssessment","StateAssessmentScore"};
        colNames = new String[]{"StudentID","TestDate","StateAssessmentSubject","GradeDuringAssessment","StateAssessmentScore"};

        if (!ImportHelper.CheckColumnHeaders(fr, colNames))
            throw new Exception("File : state_assessment.csv does not match column specs" );

        // studentId    0
        // testdate         *** NOT USING ANYMORE
        // stateassessmentsubject - code        2
        // gradeduringassessment- grade         3
        // stateassessmentscore- proficiency    4




        counter1 = 0;
        //data.forEach(row -> {
        for (String [] row : data) {
            if (!row[0].isBlank()) {


                // calculate the year based on the date.
                // Think this will fail.
                String schoolYear = MappingHelper.SchoolYearFromDateMDY(row[1]);

                //System.out.println();



                String proficiencyCode = MappingHelper.Staar_ProficiencyCodeFromProficiency(row[4]);
                String csaCode = MappingHelper.Staar_CsaCodeFromCode(row[2]);
                String subject = row[2];


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


                i.importRepo.sisStaarAdd(row[0], schoolYear, subject, row[2], row[3], row[4], proficiencyCode, csaCode);
                counter1++;
            }
        };

        i.importRepo.logInfo("Imported staar : " + counter1);
    }

    private static void importGrades(int districtId, String filePath) throws Exception {

        List<String[]> data;
        String[] fr;
        String[] colNames;
        int counter1;
        int counter2;
        
        //    Academic Grades
        System.out.println("Importing academics_grades_previous_years File");

        data = msp.readCsvFile(filePath);


        fr = data.removeFirst();

        colNames = new String[]{"STUDENTID", "COURSENUMBER","COURSENAME","COURSEGRADE","SCHOOLYEAR","TERM"};

        if (!ImportHelper.CheckColumnHeaders(fr, colNames))
            throw new Exception("File : academics_grades.csv does not match column specs" );


        // studentId            0
        // coursenumber (code)  1
        // coursename (subject) 2
        // coursegrade (score)  3
        // schoolyear           4      2024-2025
        // term (period)        5




        counter1 = 0;

        int score = 0;

        Boolean isValidScore = false;
        //data.forEach(row -> {
        for (String [] row : data) {
            if (!row[0].isBlank()) {
                // do not import if grade is blank.
                if (!row[3].isBlank()) {
                    try {
                        score = Integer.parseInt (row[3]);
                        isValidScore = true;
                    }
                    catch (Exception ex) {
                        // do nothing if not a valid score.
                        isValidScore = false;
                    }
                    //String csaCode = MappingHelper.MapMclass_CsaCodeFromCourseName(row[2]);

                    String csaCode = "";

                    //try {

                    csaCode = i.importRepo.csaCodeForCourseName(districtId, row[2]);
                        

                    // }
                    // catch (Exception ex) {
                    //     csaCode = "";
                    //     System.out.println( " Maping Code needed : " + row[2]);
                        

                    // }

                    ///System.out.println("Got Code : " + csaCode);

                    if ( isValidScore && !csaCode.isBlank())
                    {
                        
                            // String studentNumber, String schoolYear, String period, String code, String subject, int score, String csaCode
                            i.importRepo.sisGradeAdd(row[0], row[4], row[5], row[1], row[2], score, csaCode);

                            counter1++;

                        

                    }
                

                }
            }
        };

        i.importRepo.logInfo("Imported academics_grades : " + counter1);






    }

}
