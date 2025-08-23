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

            // TODO: lets do for the list of expected files;
            Path filePath = Paths.get(baseFileFolder + "campuses.csv");
            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("Import File not found : " + filePath);
            }

            
        


            LocalDateTime startDateTime = LocalDateTime.now();
            
            System.out.println("Import Id is : " + importId + " For District " + districtId);

		    UserFileService msp = new UserFileService();

            System.out.println("Importing campuses File");

            List<String[]> data = msp.readCsvFile( baseFileFolder + "campuses.csv");

            int counter1 = 0;

            String[] fr = data.removeFirst();

          
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {
                    i.importRepo.saveSchool(row[0], row[1]);
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
            counter1 = 0;

            //data.forEach(row -> {
            for (String [] row : data) {

                if (!row[0].isBlank()) 
                {

                    Student s = new Student(row[0], row[0], row[2], row[1], row[6], row[5]);

                    Demographics d = new Demographics(row[0], row[3], row[4], false, false, false, false, false, false);

                    //  sourceId, guardianId,  studentId,  firstName,  lastName,  email, type
                    Guardian g = new Guardian("Guardian_" + row[0], "G_" + row[0],  row[0],  row[10], row[11], row[12], row[9]);

                    i.importRepo.saveStudent(s);
                    i.importRepo.saveStudentDemographics(d);

                    // 504
                    if (row[7] == "Yes")
                        i.importRepo.saveStudentProperty(row[0], "is504", "1");

                    // isEsl
                    if (row[8] == "Yes")
                        i.importRepo.saveStudentProperty(row[0], "isEsl", "1");

                    i.importRepo.saveGuardian(g);

                    counter1++;
                }
                
            };

            i.importRepo.logInfo("Imported Students : " + counter1);




            System.out.println("Importing Users File");

            data = msp.readCsvFile( baseFileFolder + "users.csv");


            fr = data.removeFirst();


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
                    Teacher t = new Teacher(row[0], row[0], row[2], row[1], row[3]);
                    i.importRepo.saveTeacher(t);
                    counter1++;
                }
            };

            i.importRepo.logInfo("Imported Teachers : " + counter1);


            System.out.println("Importing teacherstudentassignements File");

            data = msp.readCsvFile( baseFileFolder + "teacherstudentassignements.csv");

            // studentid
            // userid

            fr = data.removeFirst();
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



            // mClass and map
             System.out.println("Importing mCLASS and MAP File");

            data = msp.readCsvFile( baseFileFolder + "mCLASS and MAP.csv");


            fr = data.removeFirst();

            // studentId    0
            // test         1
            // schoolYear   2       2022-2023
            // term         3
            // subject      4
            // level        5
            // testScore    6       44




            counter1 = 0;
            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {
                    String [] years = row[2].split("-");

                    if (row[1].equals("MAP")) {
                        int score = Integer.parseInt (row[6]);
                        // int districId, String studentNumber, String schoolYear, String term, String subject, String level, int score
                        //TestMap m = new TestMap(districtId, row[0], row[2], row[3], row[4], row[5], score);

                        // (                studentNumber,  schoolYear, String term, String subject, String level, int score
                        i.importRepo.saveStudentMap(row[0], years[0], row[3], row[4], row[5], score);

                        counter1++;
                    }
                }
            };

            i.importRepo.logInfo("Imported MAP : " + counter1);

            // Now we move the files to the archive Folder

            //ImportHelper.MoveFiles(baseFileFolder, archiveFolder);

            i.importRepo.logInfo("Moved Files to archive");

               // do the diff

            i.importRepo.diffImports(baseImportId);

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
