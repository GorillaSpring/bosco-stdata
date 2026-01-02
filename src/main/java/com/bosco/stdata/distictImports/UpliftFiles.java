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
public class UpliftFiles {

    private final AppConfig appConfig;
    @Autowired
    ImportRepo importRepo;

    
    @Autowired 
    BoscoApi boscoApi;
    

    private static UpliftFiles i;

    UpliftFiles(AppConfig appConfig) {
        this.appConfig = appConfig;
    }  // instance

    @PostConstruct
    public void init() {
        System.out.println("UpliftFiles - init()");
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
            Boolean setNoEmails = importDef.getSetNoEmails();


		    //String baseFileFolder = "C:/test/uplift/" + subFolder + "/";
            String baseFileFolder = ImportHelper.ValueForSetting(importSettings, "baseFolder");

            String archiveFolder =  ImportHelper.ValueForSetting(importSettings, "archiveFolder");

            
            int importId = i.importRepo.prepImport(districtId, importDefId, isRoster, isSisData,  "Uplift Files (Roster and Sis) " + baseFileFolder);

            result.importId = importId;
            result.districtId = districtId;

            LocalDateTime startDateTime = LocalDateTime.now();



            

              // Before we start, lets make sure there are files in the baseFolder
              // additional users NO
              // additional_users.csv  NO
              //  "attendance.csv",  THIS IS Yearly?
            String[] files = {"academics_grades.csv",  "campuses.csv", "discipline.csv", "mCLASS and MAP.csv", "state_assessment.csv", 
            "students.csv", "teacherstudentassignements.csv", "TELPAS.csv", "users.csv"};
            if (!ImportHelper.CheckFilesExist(baseFileFolder, files)) {
                throw new FileNotFoundException("One or more import files missing!");
            }

        


            
            List<String[]> data;

            String[] fr;


            String [] colNames;


            UserFileService msp = new UserFileService();

            System.out.println("Import  For District " + districtId);


             int counter1 = 0;
             int counter2 = 0;



            System.out.print ("Importing absenses");

            CsvFiles.LoadAttendanceLedgerUplift(districtId, baseFileFolder + "/full_attendance.csv");

            System.out.print ("DONE Importing absenses");


            // System.out.println("Importing campuses File");

            // data = msp.readCsvFile( baseFileFolder + "/campuses.csv");


            // fr = data.removeFirst();


            // colNames =  new String[]{"schoolcode", "schoolname"};

            // if (!ImportHelper.CheckColumnHeaders(fr, colNames))
            //     throw new Exception("File : campuses.csv does not match column specs" );

          
          
            // for (String [] row : data) {
            //     if (!row[0].isBlank()) 
            //     {
            //         i.importRepo.saveSchool(row[0], row[1], row[0]);
            //         counter1++;
            //     }

            // }


            // i.importRepo.logInfo("Imported Schools : " + counter1);

            System.out.println("Importing Students File");

            data = msp.readCsvFile( baseFileFolder + "/students.csv");


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
            colNames = new String[]{"studentid", "lastname", "firstname", "dob", "gender", "schoolcode", "gradecode", "is504", "englishlanguagelearner", 
            "issped", 
            "guardiantype", "guardianfirstname", "guardianlastname", "guardianemail"};

            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : students.csv does not match column specs" );

            counter1 = 0;

            // isSped is 9
            // guardiantype is now 10.

            //data.forEach(row -> {
            for (String [] row : data) {

                if (!row[0].isBlank()) 
                {

                    Boolean is504 = row[7].equals("Yes");
                    Boolean isEsl = row[8].equals("Yes");
                    Boolean isSped = row[9].equals("Yes");

                    String email = row[13];
                    // To do if we need to scramble email , do it here.
                    if (setNoEmails && email.length() >= 4) {
                        String trimedEmail = email.substring(0, email.length() - 4);
                        email = trimedEmail + "_no.no";
                    }



                    //String sourceId, String studentId, String firstName, String lastName, String grade, String schoolCode

                    // String sourceId, String studentNumber, String firstName, String lastName, String grade, String schoolSourceId

                    String grade = ImportHelper.GradeFromGradeCode(row[6]);

                    i.importRepo.saveStudent(row[0], row[0], row[2], row[1], grade, row[5]);

                    //i.importRepo.saveStudent(s);
                    // studentNumber for row [0]

                        String dob = ImportHelper.DateToStdFormat(row[3]);

                    i.importRepo.saveStudentDemographics(row[0], dob, row[4], false, false, false, false, false, false);

                    // TODO: Save isEsl, is504, isSped 

                    // if (isSped)
                    //     i.importRepo.saveStudentProperty(row[0], "isSpecialEd", "1");
                    // else
                    //     i.importRepo.saveStudentProperty(row[0], "isSpecialEd", "0");

                    // 504
                    // if (row[7] == "Yes")
                    //     i.importRepo.saveStudentProperty(row[0], "is504", "1");

                    // // isEsl
                    // if (row[8] == "Yes")
                    //     i.importRepo.saveStudentProperty(row[0], "isEsl", "1");

                    
                    // String sourceId, String guardianId, String studentId, String firstName, String lastName, String email, String type

                    // String sourceId, String guardianId, String studentSourceId, String firstName, String lastName, String email, String type
                    i.importRepo.saveGuardian("G_" + row[0], "G_" + row[0],  row[0],  row[11], row[12], email, row[10]);

                    
                    //i.importRepo.saveGuardian(g);

                    counter1++;
                }
                
            };

            i.importRepo.logInfo("Imported Students : " + counter1);




            System.out.println("Importing Users File");

            data = msp.readCsvFile( baseFileFolder + "/users.csv");


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


            System.out.println("Importing teacherstudentassignements File");

            data = msp.readCsvFile( baseFileFolder + "/teacherstudentassignements.csv");

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





            

            System.out.println("Importing SIS data");


            

            // WE NEED TO RE DO THESE ONES.
            // mClass and map
             System.out.println("Importing mCLASS and MAP File");

            data = msp.readCsvFile( baseFileFolder + "/mCLASS and MAP.csv");


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
                    String csaCode = MappingHelper.GeneralCsaCode(row[4]);

                    if (csaCode.equals("NOT_FOUND")) {
                        i.importRepo.logError("Found Map or Mclass with unkown subject " + row[4]);
                    }
                    else {

                        int score = Integer.parseInt (row[6]);

                        switch (row[1]) {
                            case "MAP":

                                proficiencyCode = MappingHelper.Map_proficiencyCode(row[5]);
                                // So proficiency is "Quintile 2".   We could map that to something better here!


                                // TODO : Get proficiencyCode and csaCode
                                i.importRepo.sisMapAdd(row[0], row[2], row[3], row[4], row[5], proficiencyCode, score, csaCode);

                                counter1++;
                                break;
                            case "MCLASS":
                                // TODO : Get proficiencyCode and csaCode
                                proficiencyCode = MappingHelper.MClass_proficiencyCode(row[5]);
                                if (proficiencyCode != "") {
                                    i.importRepo.sisMclassAdd(row[0], row[2], row[3], row[4], row[5], proficiencyCode,  score, csaCode);
                                    counter2++;
                                }
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

            data = msp.readCsvFile( baseFileFolder + "/academics_grades.csv");


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
                        //String csaCode = MappingHelper.MapMclass_CsaCodeFromCourseName(row[2]);



                        String csaCode = i.importRepo.csaCodeForCourseName(districtId, row[2]);

                        ///System.out.println("Got Code : " + csaCode);

                        if (!csaCode.isBlank())
                        {
                            // String studentNumber, String schoolYear, String period, String code, String subject, int score, String csaCode
                            i.importRepo.sisGradeAdd(row[0], row[4], row[5], row[1], row[2], score, csaCode);

                            counter1++;
                        }
                    

                    }
                }
            };

            i.importRepo.logInfo("Imported academics_grades : " + counter1);


            

            System.out.println("Importing state_assessment File");

            data = msp.readCsvFile( baseFileFolder + "/state_assessment.csv");


            fr = data.removeFirst();
            colNames = new String[]{"studentid", "testdate", "stateassessmentsubject", "gradeduringassessment", "stateassessmentscore"};

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
                    String schoolYear = MappingHelper.SchoolYearFromDate(row[1]);

                    String proficiencyCode = MappingHelper.Staar_ProficiencyCodeFromProficiency(row[4]);
                    String csaCode = MappingHelper.Staar_CsaCodeFromCode(row[2]);
                    String subject = MappingHelper.Staar_SubjectFromCode(row[2]);


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

            i.importRepo.logInfo("Imported state_assessments : " + counter1);



              System.out.println("Importing TELPAS File");

            data = msp.readCsvFile( baseFileFolder + "/TELPAS.csv");


            fr = data.removeFirst();
            colNames = new String[]{"year_id", "local_id", "grade_level", "AdministrationDate", "level", "listening_scale_score", "reading_scale_score", "speaking_scale_score", "writing_scale_score"};

            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : TELPAS.csv does not match column specs" );


            // 0 year_id
            // 1 local_id   (studentNumber)
            // 2 grade_level
            // 3 AdminDate  (use to get schoolYear)
            // 4 level  
            // 5 list_score       MAY BE NULL
            // 6 read_score       MAY BE NULL
            // 7 speacking score  MAY BE NULL
            // 8 writing score   May be BLANK"


            counter1 = 0;
            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) {


                    // calculate the year based on the date.
                    String schoolYear = MappingHelper.SchoolYearFromDate(row[3]);


                    String grade = row[2];
                    String studentId = row[1];

                    String proficiency = row[4];
                    

                        //Boolean allEmpty = true;

                    int listeningScore = 0;
                    if (!row[5].isEmpty() && !row[5].equals("NULL")) {
                        listeningScore = Integer.parseInt(row[5]);
                        //  allEmpty = false;
                    }
                    int speakingScore = 0;
                    if (!row[7].isEmpty() && !row[7].equals("NULL")) {
                        speakingScore = Integer.parseInt(row[7]);
                        // allEmpty = false;
                    }

                    int readingScore = 0;
                    if (!row[6].isEmpty() && !row[6].equals("NULL")) {
                        readingScore = Integer.parseInt(row[6]);
                        // allEmpty = false;
                    }

                    int writingScore = 0;
                    if (!row[8].isEmpty() && !row[8].equals("NULL")){
                        writingScore = Integer.parseInt(row[8]);
                        // allEmpty = false;
                    }

                    // We alre loading reguadless as of Sept 16
                        // save it
                    

                    i.importRepo.sisTelpasAdd(studentId, schoolYear, grade, proficiency, listeningScore, speakingScore, readingScore, writingScore);



                    counter1++;
                
                }
            };

            i.importRepo.logInfo("Imported TELPAS : " + counter1);



            System.out.println("Importing discipline File");

            data = msp.readCsvFile( baseFileFolder + "/discipline.csv");


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
                    //String grade = "8"; 
                    String grade = i.importRepo.gradeForStudentId(districtId + "." + row[0]);

                    // TODO: We need to lookup the grade for the student!

                    // String studentNumber, String issDays, String ossDays, String aepDays, String grade, String schoolYear
                    i.importRepo.sisDiscipline(row[0], row[1], row[2], row[3], grade, row[4]);
                    counter1++;
                }
            };

            i.importRepo.logInfo("Imported discipline : " + counter1);




      

               
            System.out.println("Sys Post Data");


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



               

            // We have done both imports and sisData

            // Now we move the files to the archive Folder
            if (!archiveFolder.isEmpty()) {

                ImportHelper.MoveFiles(baseFileFolder, archiveFolder);
            }
            else {
                System.out.println("NO Archive for " + importDefId);
            }

            i.importRepo.logInfo("Moved Files to archive");

               // do the diff





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



}
