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
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.model.*;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.UserFileService;
import com.bosco.stdata.utils.ImportHelper;

import jakarta.annotation.PostConstruct;

@Component
public class TestFiles {

    private final AppConfig appConfig;

    @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;
    

    private static TestFiles i;

    TestFiles(AppConfig appConfig) {
        this.appConfig = appConfig;
    }  // instance

    @PostConstruct
    public void init() {
        System.out.println("TestFiles - init()");
        i = this;
    }


    public static ImportResult Import(String importDefId) {

        Boolean isRoster = true;
        Boolean isSisData = false;
        
        ImportResult result = new ImportResult();
        

        try {

            ImportDefinition importDef = i.importRepo.getImportDefinition(importDefId);


            List<ImportSetting> importSettings = i.importRepo.getImportSettings(importDefId);

            int districtId = importDef.getDistrictId();
            


            
		    //String baseFileFolder = "C:/test/uplift/" + subFolder + "/";
            String baseFileFolder = ImportHelper.ValueForSetting(importSettings, "baseFolder");

            String archiveFolder =  ImportHelper.ValueForSetting(importSettings, "archiveFolder");


            int importId = i.importRepo.prepImport(districtId, importDefId, isRoster, isSisData,  "TestFiles " + baseFileFolder);


            result.importId = importId;
            result.districtId = districtId;
            


            // Before we start, lets make sure there are files in the baseFolder
            String[] files = {"guardians.csv", "schools.csv", "studentEnrollments.csv", "students.csv", "teacherEnrollments.csv", "teachers.csv"};
            if (!ImportHelper.CheckFilesExist(baseFileFolder, files)) {
                throw new FileNotFoundException("One or more import files missing!");
            }


            
        


            LocalDateTime startDateTime = LocalDateTime.now();
            
            System.out.println("Import Id is :  For District " + districtId);

		    UserFileService msp = new UserFileService();

            List<String[]> data;
            String [] colNames;
            String[] fr;
            int counter1 = 0;

            // System.out.println("Importing schools File");

            // List<String[]> data = msp.readCsvFile( baseFileFolder + "schools.csv");

            // int counter1 = 0;

            // String [] colNames = {"code", "name"};


            // String[] fr = data.removeFirst();
            // if (!ImportHelper.CheckColumnHeaders(fr, colNames))
            //     throw new Exception("File : schools.csv does not match column specs" );

          
            // for (String [] row : data) {
            //     if (!row[0].isBlank()) 
            //     {
            //         i.importRepo.saveSchool(row[0], row[1], row[0]);
            //         counter1++;
            //     }

            // }


            // i.importRepo.logInfo("Imported Schools : " + counter1);

            // System.out.println("Importing Students File");

            data = msp.readCsvFile( baseFileFolder + "students.csv");




            // studentId                        0
            // studentNumber                    1
            // lastname                         2
            // firstname                        3
            // dob                              4
            // gender                           5
            // schoolcode                       6
            // gradecode                        7



            fr = data.removeFirst();

            colNames = new String[]{"studentId", "studentNumber", "lastName", "fistName", "dob", "gender", "schoolCode", "gradeCode"};
            
            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : students.csv does not match column specs" );


            counter1 = 0;

            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {

               
                    //Student s = new Student(row[0], row[1], row[3], row[2], row[7], row[6]);
                    //Demographics d = new Demographics(row[0], row[4], row[5], false, false, false, false, false, false);
                   //Guardian g = new Guardian("Guardian_" + row[0], row[0],  row[10], row[11], row[12], row[9]);

                   // String sourceId, String studentNumber, String firstName, String lastName, String grade, String schoolSourceId

                   

                    i.importRepo.saveStudent(
                        row[0], row[1], row[3], row[2], row[7], row[6]
                    );


                    String dob = ImportHelper.DateToStdFormat(row[4]);

                    // ** THIS IS studentNumber now, NOT SOURCE ID.
                    // ** THIS DOES NOT UPDATE the import Status.
                    i.importRepo.saveStudentDemographics(
                        row[1], dob, row[5], false, false, false, false, false, false,
                        false, false, false
                    );

                    // 504
                    // if (row[7] == "Yes")
                    //     importRepo.saveStudentProperty(row[0], "is504", "1");

                    // // isEsl
                    // if (row[8] == "Yes")
                    //     importRepo.saveStudentProperty(row[0], "isEsl", "1");

                    // importRepo.saveGuardian(g);

                    counter1++;
                }
            };

            i.importRepo.logInfo("Imported Students : " + counter1);




            System.out.println("Importing teachers File");

            data = msp.readCsvFile( baseFileFolder + "teachers.csv");


            fr = data.removeFirst();

            // teacherSourceId	teacherId	lastName	firstName	email

            colNames = new String[]{"teacherSourceId", "teacherId", "lastName", "firstName", "email"};
            
            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : teachers.csv does not match column specs" );

            // teacherSourceId       0
            // teacherId            1
            // lastname             2
            // firstname            3
            // email                4


            counter1 = 0;
            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {
           
                    // sourceid, teacherId, firstname, lastname,  email
                    //Teacher t = new Teacher(row[0], row[1],  row[3], row[2], row[4]);

                    // String sourceId, String teacherId, String firstName, String lastName, String email
                    i.importRepo.saveTeacher(
                        row[0], row[1],  row[3], row[2], row[4]
                    );
                    counter1++;
                }
        
            
            };

            i.importRepo.logInfo("Imported Teachers : " + counter1);



               System.out.println("Importing guardian File");

            data = msp.readCsvFile( baseFileFolder + "guardians.csv");


            fr = data.removeFirst();

            // id	studentId	guardianId	type	lastName	firstName	email

        
            colNames = new String[]{"id", "studentId", "guardianId", "type", "lastName", "firstName", "email"};
            
            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : guardians.csv does not match column specs" );


            // sourceId     0
            // studentId    1
            // guardianId   2
            // type         3
            // lastname     4
            // firstname    5
            // email        6


            counter1 = 0;
            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {

                    // so now we need the actual studentID   ie DDDD.NNNNNN

                    // So for guardians, we may not have a unique source id in the spreadsheet.
                    // We don't actually need it.
                
                    //  sourceId, guardianId,  studentId,  firstName,  lastName,  email, type
                    //Guardian g = new Guardian(row[0], row[2], row[1],  row[5], row[4], row[6], row[3]);
                    i.importRepo.saveGuardian(
                        row[0], row[2], row[1],  row[5], row[4], row[6], row[3]
                    );
                    counter1++;
                }
            
            };

            i.importRepo.logInfo("Imported Guardians : " + counter1);


            System.out.println("Importing teacherenrollments File");

            data = msp.readCsvFile( baseFileFolder + "teacherenrollments.csv");

            // teacherId
            // classId

            fr = data.removeFirst();

            // teacherId	classId


            colNames = new String[]{"teacherId", "classId"};
            
            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : teacherenrollments.csv does not match column specs" );

            counter1 = 0;

            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {

                    i.importRepo.saveTeacherClass(row[0], row[1]);
                    counter1++;

                }

            
            };


            i.importRepo.logInfo("Imported Teachers Classes : " + counter1);


              System.out.println("Importing studentenrollments File");

            data = msp.readCsvFile( baseFileFolder + "studentenrollments.csv");

            // studentId
            // classId

            fr = data.removeFirst();

            colNames = new String[]{"studentId", "classId"};
            
            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : studentenrollments.csv does not match column specs" );

            counter1 = 0;

            //data.forEach(row -> {
            for (String [] row : data) {
                
                
                if (!row[0].isBlank()) 
                {

                    i.importRepo.saveStudentClass(row[0], row[1]);
                    counter1++;

                }

            
            };


            i.importRepo.logInfo("Imported Student classes : " + counter1);

            // build the stuent teachedr
            i.importRepo.buildStudentTeacher();

            


            // // Now we move the files to the archive Folder

            // //ImportHelper.MoveFiles(baseFileFolder, archiveFolder);

            // i.importRepo.logInfo("Moved Files to archive");


   
            i.importRepo.prepSendBosco(districtId, importDefId, isRoster, isSisData);


            // Check the Deltas here

            if (!importDef.getForceLoad() && isRoster) {
                String checkDeltas = i.importRepo.checkImportDeltas(districtId, importDefId);
                if (!checkDeltas.equals("OK")) {
                    throw new Exception("Check Import Delta failed: " + checkDeltas);
                }

            }



            // // this will mark the importId as the base.
            // i.importRepo.setImportBase(importDefId);


            // System.out.println ("Import ID is: " + importId);



            


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

}

