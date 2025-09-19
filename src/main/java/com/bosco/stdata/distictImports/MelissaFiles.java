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
public class MelissaFiles {
    @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;

private static MelissaFiles i;  // instance

    @PostConstruct
    public void init() {
        System.out.println("TestFiles - init()");
        i = this;
    }


    public static ImportResult Import(String importDefId) {

        ImportResult result = new ImportResult();
        

        try {

            ImportDefinition importDef = i.importRepo.getImportDefinition(importDefId);

            //int baseImportId = importDef.getBaseImportId();

            List<ImportSetting> importSettings = i.importRepo.getImportSettings(importDefId);

            int districtId = importDef.getDistrictId();

            Boolean setNoEmails = importDef.getSetNoEmails();

            i.importRepo.prepImport(districtId, "Import for " + importDefId);

            result.importId = 0;
            result.districtId = districtId;
            result.baseImportId = 0;
            
		    //String baseFileFolder = "C:/test/uplift/" + subFolder + "/";
            String baseFileFolder = ImportHelper.ValueForSetting(importSettings, "baseFolder");

            String archiveFolder =  ImportHelper.ValueForSetting(importSettings, "archiveFolder");




            
            // NO DATA"
            // LastFIIE_Date
            // ReEvaluationDueDate
            // AnnualARDDate
            // Annual504_Date

            // we have and need to deal with:

            // EntryIEP_Date   5   (may be "")
            // 
            // 

            
            


            // Before we start, lets make sure there are files in the baseFolder
            String[] files = {"guardians.csv", "schools.csv", "student_enrollments.csv", "students.csv", "user_enrollments.csv", "users.csv", "educational_placement.csv"};
            if (!ImportHelper.CheckFilesExist(baseFileFolder, files)) {
                throw new FileNotFoundException("One or more import files missing!");
            }


            
        


            LocalDateTime startDateTime = LocalDateTime.now();
            
            System.out.println("Import  For District " + districtId);

		    UserFileService msp = new UserFileService();



            
            // System.out.println("Importing schools File");

            // List<String[]> data = msp.readCsvFile( baseFileFolder + "schools.csv");

            // int counter1 = 0;

            // String [] colNames = {"SchoolCode", "SchoolName"};


            // // there are 2 for these files!
            // data.removeFirst();
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

            System.out.println("Importing Students File");

            List<String[]> data = msp.readCsvFile( baseFileFolder + "students.csv");





            // this does not have the extra row

            //data.removeFirst();

            String [] fr = data.removeFirst();

            String [] colNames = new String[]{"StudentSourceID", "StudentNumber", "LastName", "FirstName", "DOB", "Gender", "SchoolCode", "GradeCode" , 
                            "IsBilingual", "IsHispanicLatino", "AmericanIndianOrAlaskaNative", "Asian", "BlackOrAfricanAmerican", "NativeHawaiianOtherPacificIslander", "White"};
            
            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : students.csv does not match column specs" );


            int counter1 = 0;

            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {

                    // so we need to parse "Yes" and "No" to true or false.

                    Boolean americanIndianOrAlaskaNative = row[10].equals("Yes");
                    Boolean asian = row[11].equals("Yes");
                    Boolean blackOrAfricanAmerican  = row[12].equals("Yes");// ,  12
                    Boolean nativeHawaiianOrOtherPacificIslander = row[13].equals("Yes");//,  13
                    Boolean white =  row[14].equals("Yes"); //,   14
                    Boolean hispanicOrLatinoEthnicity =  row[9].equals("Yes");// 9


                    // We get this in the educational_placement file too.
                    Boolean isBilingual = row[8].equals("Yes");

               
                    //Student s = new Student(row[0], row[1], row[3], row[2], row[7], row[6]);
                    //Demographics d = new Demographics(row[0], row[4], row[5], false, false, false, false, false, false);
                   //Guardian g = new Guardian("Guardian_" + row[0], row[0],  row[10], row[11], row[12], row[9]);


                   //String sourceId, String studentId, String firstName, String lastName, String grade, String schoolSourceId

                    // String sourceId, String studentNumber, String firstName, String lastName, String grade, String schoolSourceId

                    i.importRepo.saveStudent(
                        row[0], row[1], row[3], row[2], row[7], row[6]
                    );

                    // student number now.
                    i.importRepo.saveStudentDemographics(
                        row[1], row[4], row[5], americanIndianOrAlaskaNative, asian, blackOrAfricanAmerican,nativeHawaiianOrOtherPacificIslander, white, hispanicOrLatinoEthnicity,
                        false, false, isBilingual
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




            System.out.println("Importing users File");

            data = msp.readCsvFile( baseFileFolder + "users.csv");


            data.removeFirst();
            fr = data.removeFirst();

            // teacherSourceId	teacherId	lastName	firstName	email

            colNames = new String[]{"UserSourceID", "UserID", "LastName", "FirstName", "Email",
                    "SchoolCode", "UserType"
                    };
            
            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : users.csv does not match column specs" );

            // we have SchoolCode and UserType here.  Ignore?

            counter1 = 0;
            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {
           
                    // sourceid, teacherId, firstname, lastname,  email
                    //Teacher t = new Teacher(row[0], row[1],  row[3], row[2], row[4]);

                    String email = row[4];
                    if (setNoEmails && email.length() >= 4) {
                        String trimedEmail = email.substring(0, email.length() - 4);
                        email = trimedEmail + "_no.no";
                    }


                    // String sourceid, String teacherId, String firstname, String lastname, String email
                    // String sourceId, String teacherId, String firstName, String lastName, String email
                    i.importRepo.saveTeacher(
                        row[0], row[1],  row[3], row[2], email
                    );
                    counter1++;
                }
        
            
            };

            i.importRepo.logInfo("Imported users : " + counter1);



            System.out.println("Importing guardian File");

            data = msp.readCsvFile( baseFileFolder + "guardians.csv");


            data.removeFirst();
            fr = data.removeFirst();

            // id	studentId	guardianId	type	lastName	firstName	email

        
            colNames = new String[]{"StudentSourceID", "StudentNumber", "GuardianIdentifier", "GuardianType", "GuardianLastName", "GuardianFirstName", "GuardianEmail"};
            
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

                    // So for guardians, we may not have a unique source id in the spreadsheet.
                    // We don't actually need it.
                
                    //  sourceId, guardianId,  studentId,  firstName,  lastName,  email, type
                    //Guardian g = new Guardian(row[0], row[2], row[1],  row[5], row[4], row[6], row[3]);

                    String email = row[6];
                    if (setNoEmails && email.length() >= 4) {
                        String trimedEmail = email.substring(0, email.length() - 4);
                        email = trimedEmail + "_no.no";
                    }

                    // String sourceId, String guardianId, String studentId, String firstName, String lastName, String email, String type
                    // String sourceId, String guardianId, String studentSourceId, String firstName, String lastName, String email, String type
                    i.importRepo.saveGuardian(
                        row[0], row[2], row[1],  row[5], row[4], email, row[3]
                    );
                    counter1++;
                }
            
            };

            i.importRepo.logInfo("Imported Guardians : " + counter1);


            System.out.println("Importing user_enrollments File");

            data = msp.readCsvFile( baseFileFolder + "user_enrollments.csv");

            // teacherId
            // classId

            data.removeFirst();
            fr = data.removeFirst();

            // teacherId	classId


            colNames = new String[]{"UserSourceID", "UserID", "CourseName", "CourseID"};
            
            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : user_enrollments.csv does not match column specs" );

            counter1 = 0;

            //data.forEach(row -> {
            for (String [] row : data) {
                if (!row[0].isBlank()) 
                {
                    // skip 000000 teachers
                    if (!row[0].equals("000000")) {

                        i.importRepo.saveTeacherClass(row[0], row[3]);
                        counter1++;
                    }

                }

            
            };


            i.importRepo.logInfo("Imported Teachers Classes : " + counter1);


            System.out.println("Importing student_enrollments File");

            data = msp.readCsvFile( baseFileFolder + "student_enrollments.csv");

            // studentId
            // classId

            data.removeFirst();
            fr = data.removeFirst();

            colNames = new String[]{"StudentSourceID", "StudentNumber", "CourseName", "CourseID"};
            
            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : student_enrollments.csv does not match column specs" );

            counter1 = 0;

            //data.forEach(row -> {
            for (String [] row : data) {
                
                
                if (!row[0].isBlank()) 
                {

                    i.importRepo.saveStudentClass(row[0], row[3]);
                    counter1++;

                }

            
            };


            i.importRepo.logInfo("Imported Student classes : " + counter1);
            



            System.out.println("Importing educational_placement File");

            data = msp.readCsvFile( baseFileFolder + "educational_placement.csv");

            // studentId
            // classId

            data.removeFirst();
            fr = data.removeFirst();

            colNames = new String[]{"StudentSourceID", "StudentNumber", "IsEsl", "IsBilingual", "IsSpecialEd", "EntryIEP_Date", "LastFIIE_Date", "ReEvaluationDueDate", "Is504", "AnnualARDDate", "Annual504_Date"};

            // NO DATA"
            // LastFIIE_Date
            // ReEvaluationDueDate
            // AnnualARDDate
            // Annual504_Date

            // we have and need to deal with:

            // EntryIEP_Date   5   (may be "")
            // 
            // 

            
            if (!ImportHelper.CheckColumnHeaders(fr, colNames))
                throw new Exception("File : educational_placement.csv does not match column specs" );


            counter1 = 0;

            //data.forEach(row -> {
            for (String [] row : data) {
                
                
                if (!row[0].isBlank()) 
                {

                    // 504
                    if (row[8] == "Yes")
                        i.importRepo.saveStudentProperty(row[1], "is504", "1");

                    // isEsl
                    if (row[2] == "Yes")
                        i.importRepo.saveStudentProperty(row[1], "isEsl", "1");

                    // IsBilingual
                    if (row[3] == "Yes")
                        i.importRepo.saveStudentProperty(row[1], "isBilingual", "1");

                    // IsSpecialEd
                    if (row[4] == "Yes")
                        i.importRepo.saveStudentProperty(row[1], "isSpecialEd", "1");

                    // EntryIEP_Date
                    if (!row[5].isEmpty())
                        i.importRepo.saveStudentPropertyString(row[1], "entryIepDate", row[5]);

                    counter1++;

                }

            
            };


            i.importRepo.logInfo("Imported educational_placement s : " + counter1);
            

              

            // build the stuent teachedr
            i.importRepo.buildStudentTeacher();

            


            // Now we move the files to the archive Folder

            //ImportHelper.MoveFiles(baseFileFolder, archiveFolder);

            i.importRepo.logInfo("Moved Files to archive");


     




            // // this will mark the importId as the base.
            // i.importRepo.setImportBase(importDefId);

            i.importRepo.postImport();
        
            LocalDateTime endDateTime = LocalDateTime.now();
    
            Duration duration = Duration.between(startDateTime, endDateTime);

            
            i.importRepo.logInfo("Import " + importDefId + "  Complete in : " + duration.toSeconds() + " Seconds" );

            


            


            i.boscoApi.sendImportToBosco(districtId);

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

