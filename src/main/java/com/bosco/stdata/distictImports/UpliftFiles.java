package com.bosco.stdata.distictImports;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
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


    public static ImportResult Import(String importDefId) {

        ImportResult result = new ImportResult();

        try {

             ImportDefinition importDef = i.importRepo.getImportDefinition(importDefId);

            int baseImportId = importDef.getBaseImportId();


            List<ImportSetting> importSettings = i.importRepo.getImportSettings(importDefId);

            int districtId = importDef.getDistrictId();
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
                    i.importRepo.saveGuardian("Guardian_" + row[0], "G_" + row[0],  row[0],  row[10], row[11], row[12], row[9]);

                    
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

                    // sourceid, teacherId, firstname, lastname,  email
                    //Teacher t = new Teacher(row[0], row[0], row[2], row[1], row[3]);
                    i.importRepo.saveTeacher(
                        row[0], row[0], row[2], row[1], row[3]
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

            // studentId    0
            // test         1       MAP or MCLASS
            // schoolYear   2       2022-2023
            // term         3
            // subject      4
            // level        5
            // testScore    6       44




            counter1 = 0;
            counter2 = 0;
            
            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {
                    // they combine map and mclass

                    int score = Integer.parseInt (row[6]);

                    switch (row[1]) {
                        case "MAP":
                             i.importRepo.sisMapAdd(row[0], row[2], row[3], row[4], row[5], score);

                            counter1++;
                            break;
                        case "MCLASS":
                            i.importRepo.sisMclassAdd(row[0], row[2], row[3], row[4], row[5], score);

                            counter2++;
                            break;

                    
                        default:
                            System.out.println("Unknown Type in file " + row[1]);
                            break;
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


            // studentId    0
            // coursenumber 1
            // coursename   2
            // coursegrade  3
            // schoolyear   4      2024-2025
            // term         5




            counter1 = 0;
            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) {
                    // do not import if grade is blank.
                    if (!row[3].isBlank()) {
                        int grade = Integer.parseInt (row[3]);
                        // sisAcademicGradeAdd (String studentNumber, String schoolYear, String term, String courseNumber, String courseName, int grade)
                        i.importRepo.sisAcademicGradeAdd(row[0], row[4], row[5], row[1], row[2], grade);

                        counter1++;
                    

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
            // stateassessmentsubject
            // gradeduringassessment
            // stateassessmentscore




            counter1 = 0;
            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) {

                    i.importRepo.sisStaarAdd(row[0], row[1], row[2], row[3], row[4]);
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

                    i.importRepo.sisDiscipline(row[0], row[1], row[2], row[3], row[4]);
                    counter1++;
                }
            };

            i.importRepo.logInfo("Imported discipline : " + counter1);


            System.out.println("Sys Post Data");
            i.importRepo.sisPostData();


            // Now we move the files to the archive Folder

            ImportHelper.MoveFiles(baseFileFolder, archiveFolder);

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
