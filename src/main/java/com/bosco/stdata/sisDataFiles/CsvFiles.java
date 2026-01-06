package com.bosco.stdata.sisDataFiles;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.digester.SystemPropertySource;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.controllers.AuthedApi;
import com.bosco.stdata.distictImports.BurlesonFiles;
import com.bosco.stdata.distictImports.BurlesonSisFiles;
import com.bosco.stdata.model.SisAttendance;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.BoscoClient;
import com.bosco.stdata.service.UserFileService;
import com.bosco.stdata.teaModel.AllenStudent;
import com.bosco.stdata.teaModel.CelinaCombo;
import com.bosco.stdata.teaModel.DibelsMClass;
import com.bosco.stdata.teaModel.DisciplineFileCelina;
import com.bosco.stdata.teaModel.DisciplineHelper;
import com.bosco.stdata.teaModel.DisciplineLedger;
import com.bosco.stdata.teaModel.FindUsers;
import com.bosco.stdata.teaModel.GradeCurrentYearBurleson;
import com.bosco.stdata.teaModel.GradeFileCelina;
import com.bosco.stdata.teaModel.GradeFileMelissa;
import com.bosco.stdata.teaModel.GradePriorYearBurleson;
import com.bosco.stdata.teaModel.MapCourseNameCsaCode;
import com.bosco.stdata.utils.ImportHelper;
import com.bosco.stdata.utils.MappingHelper;
import com.bosco.stdata.utils.TeaStaarFlatFileReader;

import jakarta.annotation.PostConstruct;

@Component
public class CsvFiles {

    private final BurlesonSisFiles burlesonSisFiles;

    private final BurlesonFiles burlesonFiles;

    private final AuthedApi authedApi;

    private final BoscoClient boscoClient;

    private final BoscoApi boscoApi;

    private final AppConfig appConfig;
    
   
    @Autowired
    ImportRepo importRepo;

    
    private static CsvFiles i;


    CsvFiles(AppConfig appConfig, BoscoApi boscoApi, BoscoClient boscoClient, AuthedApi authedApi, BurlesonFiles burlesonFiles, BurlesonSisFiles burlesonSisFiles) {
        this.appConfig = appConfig;
        this.boscoApi = boscoApi;
        this.boscoClient = boscoClient;
        this.authedApi = authedApi;
        this.burlesonFiles = burlesonFiles;
        this.burlesonSisFiles = burlesonSisFiles;
    }


   
    @PostConstruct
    public void init() {
        System.out.println("TeaFiles - init()");
        i = this;
    }

    //#region Allen Files

    

    public static void LoadAllenStudents (int districtId, String filePath, Boolean setNoEmails) throws Exception {


        File file = new File(filePath);
        String fileName = file.getName();
        System.out.println("Students - Filename: " + fileName); // Output: Filename: myFile.txt


        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

        FlatFileItemReader<AllenStudent> cr = tsfr.studentAllenReader(filePath);

        cr.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ Loading Allen Students ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;

        
        AllenStudent cc = cr.read();
            //String schoolYear = "2024-2025";  // should be able to get this from TermName

            //String termName = cc.getTermName();


        while (cc != null) {

            total++;

            // String sourceId, String studentNumber, String firstName, String lastName, String grade, String schoolSourceId

            // for now, schoolCode has an extra "0";

            String schoolCode = cc.schoolCode; // cc.schoolCode.substring(1);


            i.importRepo.saveStudent(cc.studentID, cc.studentID, cc.firstName, cc.lastName, cc.gradeCode, schoolCode);

                //i.importRepo.saveStudent(s);
                // studentNumber for row [0]

                    String dob = ImportHelper.DateToStdFormat(cc.dOB);

    // String studentNumber,             String dob,             String gender,             Boolean americanIndianOrAlaskaNative,             Boolean asian,            Boolean blackOrAfricanAmerican, 
    //              Boolean nativeHawaiianOrOtherPacificIslander,             Boolean white,            Boolean hispanicOrLatinoEthnicity

            i.importRepo.saveStudentDemographics(cc.studentID, dob, cc.gender, 
            cc.americanIndianOrAlaskaNative.equals("Yes"), 
            cc.asian.equals("Yes"), 
            cc.blackOrAfricanAmerican.equals("Yes"), 
            cc.nativeHawaiianOtherPacificIslander.equals("Yes"), 
            cc.white.equals("Yes"), 
            cc.isHispanicLatino.equals("Yes")

            );

            String gType;
            String email;
            

            if (!cc.guardianEmail.isEmpty()) {

                email = cc.guardianEmail;
                // To do if we need to scramble email , do it here.
                if (setNoEmails && email.length() >= 4) {
                    String trimedEmail = email.substring(0, email.length() - 4);
                    email = trimedEmail + "_no.no";
                }

                gType = MappingHelper.GuardianTypeFromStringAllen(cc.guardianType);
                i.importRepo.saveGuardian("G1_" + cc.studentID,  "G1_" + cc.studentID, cc.studentID, cc.guardianFirstName, cc.guardianLastName, email, gType);

            }


            if (!cc.guardian2Email.isEmpty()) {

                email = cc.guardian2Email;
                // To do if we need to scramble email , do it here.
                if (setNoEmails && email.length() >= 4) {
                    String trimedEmail = email.substring(0, email.length() - 4);
                    email = trimedEmail + "_no.no";
                }

                gType = MappingHelper.GuardianTypeFromStringAllen(cc.guardian2Type);
                i.importRepo.saveGuardian("G2_" + cc.studentID,  "G2_" + cc.studentID, cc.studentID, cc.guardian2FirstName, cc.guardian2LastName, email, gType);

            }


            if (!cc.guardian3Email.isEmpty()) {

                email = cc.guardian3Email;
                // To do if we need to scramble email , do it here.
                if (setNoEmails && email.length() >= 4) {
                    String trimedEmail = email.substring(0, email.length() - 4);
                    email = trimedEmail + "_no.no";
                }


                gType = MappingHelper.GuardianTypeFromStringAllen(cc.guardian3Type);
                i.importRepo.saveGuardian("G2_" + cc.studentID,  "G2_" + cc.studentID, cc.studentID, cc.guardian3FirstName, cc.guardian3LastName, email, gType);

            }                
                



             //i.importRepo.setMapCourseCsaCode(districtId, cc.getCourseName(), "");

             System.out.println("Student : " + cc.studentID + " : " + cc.firstName + " " + cc.lastName  +  "  -- " + cc.guardianType);
             System.out.println ("    -- " + cc.americanIndianOrAlaskaNative + "," + cc.asian + "," + cc.blackOrAfricanAmerican + "," + cc.nativeHawaiianOtherPacificIslander + "," + cc.white + "," + cc.isHispanicLatino);

             
            

            cc = cr.read();
        }

        //i.importRepo.logFile(fileName, "Grades", schoolYear, true, "Total: " + total + "  - Imported : " + count);

        
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    }
    
    

    //#endregion

    // public static void LoadFindUsers (String filePath) throws Exception {
    //     TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

    //     FlatFileItemReader<FindUsers> cr = tsfr.findUsers(filePath);

    //     cr.open(new ExecutionContext());

    //     System.out.println(("-----------------------"));
    //     System.out.println(("------ LoadFindUsers ------"));
    //     System.out.println (filePath);

    //     int count = 0;
    //     int total = 0;


    //     List<String> emails = new ArrayList<>();
        
    //     FindUsers cc = cr.read();
    //         //String schoolYear = "2024-2025";  // should be able to get this from TermName

    //         //String termName = cc.getTermName();


    //     while (cc != null) {

    //         if (emails.contains(cc.email)) {
    //             System.out.println("FOUND DUP: " + cc.id + " - " + cc.email);
    //         }
    //         else {
    //             emails.add(cc.email);
    //         }

            
    //         cc = cr.read();
    //     }


        
        
    //     System.out.println("  Total: " + total + "  - Imported : " + count);

    //     System.out.println(("-----------------------"));
    // }


    public static void LoadMapCourseNameCsaCode (String filePath) throws Exception {
        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

        FlatFileItemReader<MapCourseNameCsaCode> cr = tsfr.mapCourseNameCsaCodeReader(filePath);

        cr.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ Loading Map CourseName to CsaCode ------"));
        System.out.println (filePath);

        
        int count = 0;
        int total = 0;

        
        MapCourseNameCsaCode cc = cr.read();
            //String schoolYear = "2024-2025";  // should be able to get this from TermName

            //String termName = cc.getTermName();


        while (cc != null) {

            i.importRepo.setMapCourseCsaCode(cc.districtId, cc.couseName, cc.csaCode);

            
            cc = cr.read();
        }


        
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    }
    

    public static void LoadGradesPriorBurleson (int districtId, String filePath) throws Exception {

        File file = new File(filePath);
        String fileName = file.getName();
        System.out.println("Grades - Filename: " + fileName); // Output: Filename: myFile.txt

        String schoolYear = "N/A";

        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

        FlatFileItemReader<GradePriorYearBurleson> cr = tsfr.gradeBurlesonPriodYear(filePath);

        cr.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ Loading GradePriorYearBurleson Grades ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;


        Boolean scoreValid = true;
        int score = 0;

        
        
        GradePriorYearBurleson cc = cr.read();
            //String schoolYear = "2024-2025";  // should be able to get this from TermName

            //String termName = cc.getTermName();



        while (cc != null) {

            total++;

            // we need
            // public String studentSourceId;
            // public String studentNumber;
            // public String courseName;
            
            // public String schoolYear;

            // ublic String courseGradeSemester1;
            // public String courseGradeSemester2;
            // public String courseGradeFinal;
            


            //i.importRepo.setMapCourseCsaCode(districtId, cc.getCourseName(), "");

            
            // it will be one of the 3 but not all.
            // we could just do for each one.
            
            
            String csaCode = "";

            //System.out.println("Checking: " + cc.courseName);
            csaCode = i.importRepo.csaCodeForCourseName(districtId, cc.courseName);




            
            if (!csaCode.isBlank())  
            {
                schoolYear = MappingHelper.SchoolYearFromYear(cc.schoolYear);

                if (!cc.courseGradeSemester1.isBlank()) {


                    scoreValid = true;
                    try {

                        // so the scoreString may not be an int.
                        score = Integer.parseInt(cc.courseGradeSemester1.replace(".", ""));
                    }
                    catch (Exception ex) {
                        scoreValid = false;
                    }

                    if (scoreValid) {
                    
                        String period = "S1";
                        i.importRepo.sisGradeAdd (cc.studentNumber, schoolYear, period, "", cc.courseName, score, csaCode);

                        count++;
                    }

                }
                if (!cc.getCourseGradeSemester2().isEmpty()) {
                    
                    scoreValid = true;
                    try {

                        // so the scoreString may not be an int.
                        score = Integer.parseInt(cc.courseGradeSemester2.replace(".", ""));
                    }
                    catch (Exception ex) {
                        scoreValid = false;
                    }

                    if (scoreValid) {
                        String period = "S2";
                        i.importRepo.sisGradeAdd (cc.studentNumber, schoolYear, period, "", cc.courseName, score, csaCode);

                        count++;
                    }
                    

                }
                if (!cc.getCourseGradeFinal().isBlank()) {
                    


                    scoreValid = true;
                    try {

                        // so the scoreString may not be an int.
                        score = Integer.parseInt(cc.courseGradeFinal.replace(".", ""));
                    }
                    catch (Exception ex) {
                        scoreValid = false;
                    }

                    if (scoreValid) {
                        //String period = "Y" + schoolYear + " Final";
                        String period = "Y1";
                        i.importRepo.sisGradeAdd (cc.studentNumber, schoolYear, period, "", cc.courseName, score, csaCode);

                        count++;
                    }
                    

                }
            }

            
            


           
            // else {
            //     System.out.println ("Empty score");
            // }


            cc = cr.read();
        }

        i.importRepo.logFile(fileName, "Grades", schoolYear, true, "Total: " + total + "  - Imported : " + count);

        
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    }
    
    public static void LoadGradesCurrentBurleson (int districtId, String filePath) throws Exception {

        File file = new File(filePath);
        String fileName = file.getName();
        System.out.println("Grades - Filename: " + fileName); // Output: Filename: myFile.txt

        String schoolYear = "N/A";

        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

        FlatFileItemReader<GradeCurrentYearBurleson> cr = tsfr.gradeBurlesonCurrentYear(filePath);


        cr.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ Loading LoadGradesCurrentBurleson Grades ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;

        
        Boolean scoreValid = true;
        int score = 0;


        GradeCurrentYearBurleson cc = cr.read();
            //String schoolYear = "2024-2025";  // should be able to get this from TermName

            //String termName = cc.getTermName();


        while (cc != null) {

            total++;

            // we need
            // public String studentSourceId;
            // public String studentNumber;
            // public String courseName;
            
            // public String schoolYear;

            // ublic String courseGradeSemester1;
            // public String courseGradeSemester2;
            // public String courseGradeFinal;
            


            //i.importRepo.setMapCourseCsaCode(districtId, cc.getCourseName(), "");

            
            // it will be one of the 3 but not all.
            // we could just do for each one.
            
            
            String csaCode = "";
            csaCode = i.importRepo.csaCodeForCourseName(districtId, cc.courseName);

            if (!csaCode.isBlank())
            {

                

                // ok, lest see what we can figure out.

                String courseGrade = cc.getCourseGrade();

                if (!courseGrade.isEmpty()) {
                    // 1SW:[100]2SW:[99]

                    csaCode = i.importRepo.csaCodeForCourseName(districtId, cc.courseName);
                    schoolYear = MappingHelper.SchoolYearFromYear(cc.schoolYear);

                    String [] grades = courseGrade.split("]");

                    //System.out.println(cc.getStudentNumber() + " - " + cc.getCourseName() + " : " + courseGrade + "  (" + grades.length + ")" );

                    for (int gradeIndex=0; gradeIndex< grades.length; gradeIndex++) {
                    
                        //System.out.println("       -- [" + gradeIndex + "]  "  + grades[gradeIndex]);

                        String [] gradeCourse = grades[gradeIndex].split("\\[");


                        //System.out.println("       -- [" + gradeIndex + "]  "  + gradeCourse[0].replace(":", "")  + " : " + Integer.parseInt(gradeCourse[1]));

                        String periodFile = gradeCourse[0].replace(":", "") ;
                        
                        //int score = Integer.parseInt(gradeCourse[1]);

                        scoreValid = true;
                        try {

                        // so the scoreString may not be an int.
                            score = Integer.parseInt(gradeCourse[1].replace(".", ""));
                        }
                        catch (Exception ex) {
                            scoreValid = false;
                        }

                        if (scoreValid) {

                            ///String period = "Y" + schoolYear + " - " + periodFile;
                            
                            i.importRepo.sisGradeAdd (cc.studentNumber, schoolYear, periodFile, "", cc.getCourseName(), score, csaCode);

                            count++;


                        }
                    }

                }

            }



            // if (!cc.getCourseGradeSemester1().isEmpty()) {
                

            //         csaCode = i.importRepo.csaCodeForCourseName(districtId, cc.courseName.replace(",", ""));

            //         if (!csaCode.isBlank()) 
            //         {
            //             Boolean scoreValid = true;
            //             int score = 0;

            //             try {

            //                 // so the scoreString may not be an int.
            //                 score = Integer.parseInt(cc.getCourseGradeSemester1());
            //             }
            //             catch (Exception ex) {
            //                 scoreValid = false;
            //             }

            //             if (scoreValid) {

            //                 String studentNumber = cc.getStudentNumber();
            //                 String period = "Semester 1";
            //                 String subject = cc.getCourseName();
            //                 String code = "";  // we do not have codes.

            //                 //System.out.print("Getting : " + studentNumber);

            //                 // THIS NEEDS TO BE cacluated.
            //                 String schoolYear = MappingHelper.SchoolYearFromYear(cc.getSchoolYear());

            //                 //System.out.println ("  Got : " + schoolYear);

            //                 //i.importRepo.

            //                 // String studentNumber, String schoolYear, String period, String code, String subject, int score, String csaCode
            //                 i.importRepo.sisGradeAdd (studentNumber, schoolYear, period, code, subject, score, csaCode);

            //                 count++;
            //             }
            //             // else {
            //             //     System.out.println ("Invalid Score : " + scoreString);
            //             // }

            //         }


            // }

            
            


           
            // else {
            //     System.out.println ("Empty score");
            // }


            cc = cr.read();
        }


        i.importRepo.logFile(fileName, "Grades", schoolYear, true, "Total: " + total + "  - Imported : " + count);
        
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    }
    
    

    public static void LoadGradesMelissa (int districtId, String filePath) throws Exception {

        File file = new File(filePath);
        String fileName = file.getName();
        System.out.println("Grades - Filename: " + fileName); // Output: Filename: myFile.txt

        String schoolYear = "N/A";


        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

        FlatFileItemReader<GradeFileMelissa> cr = tsfr.gradeMelissaReader(filePath);

        cr.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ Loading Melissa Grades ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;

        
        GradeFileMelissa cc = cr.read();
            //String schoolYear = "2024-2025";  // should be able to get this from TermName

            //String termName = cc.getTermName();


        while (cc != null) {

            total++;

            // we need
            // public String studentSourceId;
            // public String studentNumber;
            // public String courseName;
            // public String courseId;
            // public String schoolYear;
            // public String term;
            // public String courseGrade;
            // public String changedDateTime;  // don;'t need
            


            // i.importRepo.setMapCourseCsaCode(districtId, cc.getCourseName(), "");

            
            String scoreString = cc.getCourseGrade().replace("*", "");
            


            if (!scoreString.isEmpty()) {

                // first see if it is something we load anyway

                String csaCode = i.importRepo.csaCodeForCourseName(districtId, cc.courseName);


                if (!csaCode.isBlank()) 
                {
                    Boolean scoreValid = true;
                    int score = 0;

                    try {

                        // so the scoreString may not be an int.
                        score = Integer.parseInt(scoreString);
                    }
                    catch (Exception ex) {
                        scoreValid = false;
                    }

                    if (scoreValid && (score > 0)) {

                        String studentNumber = cc.getStudentNumber();
                        String period = cc.getTerm();
                        String subject = cc.getCourseName();
                        String code = cc.getCourseId();

                        // THIS NEEDS TO BE cacluated.
                        schoolYear = MappingHelper.SchoolYearFromYear(cc.getSchoolYear());

                        

                        //i.importRepo.

                        // String studentNumber, String schoolYear, String period, String code, String subject, int score, String csaCode
                        i.importRepo.sisGradeAdd (studentNumber, schoolYear, period, code, subject, score, csaCode);

                        count++;
                    }
                    // else {
                    //     System.out.println ("Invalid Score : " + scoreString);
                    // }

                }
                    
                
            }
            // else {
            //     System.out.println ("Empty score");
            // }


            cc = cr.read();
        }

        i.importRepo.logFile(fileName, "Grades", schoolYear, true, "Total: " + total + "  - Imported : " + count);

        
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    }


      public static void LoadSpringtownDiscipline (int districtId, String filePath) throws Exception {
    

        File file = new File(filePath);
        String fileName = file.getName();
        System.out.println("Discipline - Filename: " + fileName); // Output: Filename: myFile.txt
        String schoolYear = "N/A";


        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

        FlatFileItemReader<DisciplineFileCelina> cr = tsfr.disiplineCelinaReader(filePath);

        cr.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ Loading Springtown Discipline ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;

        
        DisciplineFileCelina cc = cr.read();
            //String schoolYear = "2024-2025";  // should be able to get this from TermName

            //String termName = cc.getTermName();


        while (cc != null) {

            total++;


            
            
            //String grade = i.importRepo.gradeForStudentId(districtId + "." + cc.studentNumber);

            schoolYear = cc.numericYear;

            if (!cc.iSS.equals("0.0") || !cc.oSS.equals("0.0") || !cc.dAEP.equals("0.0")) {
                count++;
            
                System.out.println(cc.studentNumber + " " + cc.iSS + " " + cc.oSS + " " + cc.dAEP + " : " + schoolYear);

                i.importRepo.sisDiscipline(cc.studentNumber, cc.iSS, cc.oSS, cc.dAEP, "", schoolYear);
            }   
            // if (!grade.isEmpty()) {
            //     
            //     count++;
            // }
            // else {
            //     System.out.println("Did not find student : " + cc.studentNumber);
            // }


            cc = cr.read();
        }


        i.importRepo.logFile(fileName, "Discipline", schoolYear, true, "Total: " + total + "  - Imported : " + count);
        
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    
    }


    public static void LoadCelinaDiscipline (int districtId, String filePath) throws Exception {
    

        File file = new File(filePath);
        String fileName = file.getName();
        System.out.println("Discipline - Filename: " + fileName); // Output: Filename: myFile.txt
        String schoolYear = "N/A";


        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

        FlatFileItemReader<DisciplineFileCelina> cr = tsfr.disiplineCelinaReader(filePath);

        cr.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ Loading Celina Discipline ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;

        
        DisciplineFileCelina cc = cr.read();
            //String schoolYear = "2024-2025";  // should be able to get this from TermName

            //String termName = cc.getTermName();


        while (cc != null) {

            total++;


            
            
            //String grade = i.importRepo.gradeForStudentId(districtId + "." + cc.studentNumber);

            schoolYear = MappingHelper.SchoolYearFromYear(cc.numericYear);

            i.importRepo.sisDiscipline(cc.studentNumber, cc.iSS, cc.oSS, cc.dAEP, "", schoolYear);
            count++;
            
            


            cc = cr.read();
        }


        i.importRepo.logFile(fileName, "Discipline", schoolYear, true, "Total: " + total + "  - Imported : " + count);
        
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    
    }


    

    public static void LoadGradesNbIsd (int districtId, String filePath) throws Exception {


        File file = new File(filePath);
        String fileName = file.getName();
        System.out.println("Grades - Filename: " + fileName); // Output: Filename: myFile.txt



        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

        FlatFileItemReader<GradeFileCelina> cr = tsfr.gradeNbIsdReader(filePath);

        cr.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ Loading NBISD Grades ------"));
        System.out.println (filePath);

        String schoolYear = "N/A";


        int count = 0;
        int total = 0;

        
        GradeFileCelina cc = cr.read();
            //String schoolYear = "2024-2025";  // should be able to get this from TermName

            //String termName = cc.getTermName();


        // this is the same as celina, but csv instead of tab sv.


        while (cc != null) {

            total++;

            // we need
            // public String studentSourceId;
            // public String studentNumber;
            // public String courseName;
            // public String courseId;
            // public String schoolYear;
            // public String term;
            // public String courseGrade;
            // public String changedDateTime;  // don;'t need
            



            
            
            String scoreString = cc.getCourseGrade().replace("*", "");
            


            if (!scoreString.isEmpty()) {

                // first see if it is something we load anyway

                // System.out.print("CHECKING Course: " + cc.courseName);


                String csaCode = i.importRepo.csaCodeForCourseName(districtId, cc.courseName);
                    


                if (!csaCode.isBlank()) 
                {
                    Boolean scoreValid = true;
                    int score = 0;

                    try {

                        // so the scoreString may not be an int.
                        score = Integer.parseInt(scoreString);
                    }
                    catch (Exception ex) {
                        scoreValid = false;
                    }

                    if (scoreValid) {

                        // for NB, this is the student number;
                        String studentNumber = cc.getStudentSourceId();
                        String period = cc.getTerm();
                        String subject = cc.getCourseName();
                        String code = cc.getCourseId();

                        //System.out.print("Getting : " + studentNumber);

                        // THIS NEEDS TO BE cacluated.
                        //String schoolYear = MappingHelper.SchoolYearFromYear(cc.getSchoolYear());
                        schoolYear = cc.getSchoolYear();

                        //System.out.println ("  Got : " + schoolYear);

                        //i.importRepo.

                        // String studentNumber, String schoolYear, String period, String code, String subject, int score, String csaCode
                        i.importRepo.sisGradeAdd (studentNumber, schoolYear, period, code, subject, score, csaCode);

                        count++;
                    }
                    // else {
                    //     System.out.println ("Invalid Score : " + scoreString);
                    // }

                }
                    
                
            }
            // else {
            //     System.out.println ("Empty score");
            // }

            

            cc = cr.read();
        }

        i.importRepo.logFile(fileName, "Grades", schoolYear, true, "Total: " + total + "  - Imported : " + count);

        
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    }
    
    


    public static void LoadGradesCelina (int districtId, String filePath) throws Exception {


        File file = new File(filePath);
        String fileName = file.getName();
        System.out.println("Grades - Filename: " + fileName); // Output: Filename: myFile.txt

        String schoolYear = "N/A";

        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

        FlatFileItemReader<GradeFileCelina> cr = tsfr.gradeCelinaReader(filePath);

        cr.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ Loading Celina Grades ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;

        
        GradeFileCelina cc = cr.read();
            //String schoolYear = "2024-2025";  // should be able to get this from TermName

            //String termName = cc.getTermName();


        while (cc != null) {

            total++;

            // we need
            // public String studentSourceId;
            // public String studentNumber;
            // public String courseName;
            // public String courseId;
            // public String schoolYear;
            // public String term;
            // public String courseGrade;
            // public String changedDateTime;  // don;'t need
            


             //i.importRepo.setMapCourseCsaCode(districtId, cc.getCourseName(), "");

             
            
            String scoreString = cc.getCourseGrade().replace("*", "");
            


            if (!scoreString.isEmpty()) {

                // first see if it is something we load anyway

                // System.out.print("CHECKING Course: " + cc.courseName);

                String csaCode = i.importRepo.csaCodeForCourseName(districtId, cc.courseName.replace(",", ""));
                // }
                // catch (Exception ex) {
                //     System.out.println(cc.courseName);
                // }


                //System.out.println("  GOT: " + csaCode);


                if (!csaCode.isBlank()) 
                {
                    Boolean scoreValid = true;
                    int score = 0;

                    try {

                        // so the scoreString may not be an int.
                        score = Integer.parseInt(scoreString);
                    }
                    catch (Exception ex) {
                        scoreValid = false;
                    }

                    if (scoreValid) {

                        String studentNumber = cc.getStudentNumber();
                        String period = cc.getTerm();
                        String subject = cc.getCourseName();
                        String code = cc.getCourseId();

                        //System.out.print("Getting : " + studentNumber);

                        // THIS NEEDS TO BE cacluated.
                        schoolYear = MappingHelper.SchoolYearFromYear(cc.getSchoolYear());

                        //System.out.println ("  Got : " + schoolYear);

                        //i.importRepo.

                        // String studentNumber, String schoolYear, String period, String code, String subject, int score, String csaCode
                        i.importRepo.sisGradeAdd (studentNumber, schoolYear, period, code, subject, score, csaCode);

                        count++;
                    }
                    // else {
                    //     System.out.println ("Invalid Score : " + scoreString);
                    // }

                }
                    
                
            }
            // else {
            //     System.out.println ("Empty score");
            // }

            

            cc = cr.read();
        }

        i.importRepo.logFile(fileName, "Grades", schoolYear, true, "Total: " + total + "  - Imported : " + count);

        
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    }
    
    
    

    public static void LoadMapComboStudentAssessment (int districtId, String filePath, Boolean useStudentSourceId) throws Exception  {


        File file = new File(filePath);
        String fileName = file.getName();
        System.out.println("Map - Filename: " + fileName); // Output: Filename: myFile.txt

        // if (i.importRepo.logFileExists(fileName)) {
        //     System.out.println("   --- Already Imported ");
        //     return;
        // }

        String schoolYear = "N/A";

        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

        FlatFileItemReader<CelinaCombo> cr = tsfr.celinaComboItemReader(filePath);

        cr.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ LoadComboStudentAssessment ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;

        
        CelinaCombo cc = cr.read();
            //String schoolYear = "2024-2025";  // should be able to get this from TermName

            //String termName = cc.getTermName();


        while (cc != null) {

            total++;

            // we need
            // studentId
            // schoolYear
            // period                   Fall, Spring, Winter
            // subject
            // proficiency              Quintile 1...
            // proficiencyCode          Q1 ...
            // score
            // csaCode
            

            
            // THIS IS THE SOURCE ID!!


            String achievementQuintile = cc.getAchievementQuintile();
            if (!achievementQuintile.isEmpty()) {


                // For Celina and Melissa we can use the student.sourceId to get the student Number

                // For Celina this is Student_XXXX (sourceId)
                // For Melissa it is the id, but the source Id is the same.
                
                
                String studentId = cc.getStudentID();
                String studentNumber = "";

                if (useStudentSourceId) {
                    // System.out.println("Checking Student : " + studentId);
                    studentNumber = i.importRepo.studentNumberFromSourceId(studentId);

                    // if the student is null, we just don't load.
                    //if (studentNumber == null)
                        // System.out.println("  ---------------------------------  NULLL: " + studentId);
                }
                else
                    studentNumber = studentId;



                if (studentNumber != null) {

                



                    //String termName = cc.getTermName();
                    String course = cc.getCourse();
                    
                    String subject = MappingHelper.MapSubject(course);

                    if (subject != null) {
                        
                        int perc = Integer.parseInt(cc.getPercentCorrect());

                        int score = 0;
                        try {
                            score = Integer.parseInt(cc.testRITScore);
                        }
                        catch (Exception ex) {
                            System.out.println("Bad testRITScore: " + cc.testRITScore);
                        }
                        



                        //String testStartDate = cc.getTestStartDate();

                        String period = MappingHelper.MapPeriod(cc.getTermName());
                        String proficiency = MappingHelper.MapProficiency(cc.getAchievementQuintile());
                        String proficiencyCode = MappingHelper.MapProficiencyCode(cc.getAchievementQuintile());

                        schoolYear = MappingHelper.SchoolYearFromDate(cc.getTestStartDate());

                        String csaCode = MappingHelper.MapCsaCode(course);

                        

                        //i.importRepo.sisMapAdd(studentNumber, schoolYear, period, subject, proficiency, proficiencyCode, count, csaCode);


                        // String studentNumber, String schoolYear, String period, String subject, String proficiency, String proficiencyCode, int score, String csaCode
                        i.importRepo.sisMapAdd(studentNumber, schoolYear, period, subject, proficiency, proficiencyCode, score, csaCode);

                        //System.out.println("(" + studentNumber + ":" +studentId +  ", " + schoolYear + ", " + period + ", " + subject + ", " + proficiency + ", " + proficiencyCode + ", " + score + ", " + csaCode + ")");

                        //System.out.println("Student: " + studentId + "  - Term: " + termName + "  - Subject : " + subject + "  - Level: " + level + "  - Score: " + score);

                        //System.out.println("   --- StartDate: " + testStartDate + " - SchoolYear: " + schoolYear +  "  - Period: " + period + "  - Proficiency : " + proficiency + " (" + proficiencyCode +  ")  - Score: " + score);

                        count++;
                    }
                }
            }
            else {
                System.out.println ("Empty achievementQuintile");
            }


            cc = cr.read();
        }


        i.importRepo.logFile (fileName, "Map", schoolYear, false, "Total: " + total + "  - Imported : " + count);


        
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));

    }



    public static Boolean GenericMClass_Lexie_Validate(int districtId, String filePath, String schoolYear, String benchmarkPeriod, int studentNumberCol, int proficiencyCol, int scoreCol ) {
         int rowsRead = 0;
        int rowsLoaded = 0;

        System.out.println("----------------- START -------------");
        System.out.println(filePath);

        try {
            UserFileService msp = new UserFileService();

            List<String[]> data;

            String[] fr;

            data = msp.readCsvFile(filePath);

            fr = data.removeFirst();

            System.out.println("Starting Data");

            Boolean rowValid = true;

            for (String [] row : data) {

                rowsRead++;
                rowValid = true;

                String proficiency = row[proficiencyCol];

                if (proficiency.isEmpty())
                    rowValid = false;

                String studentNumber = row[studentNumberCol];

                String stringScore = row[scoreCol];

                if (stringScore.isEmpty()) {
                    System.out.println ("Empty Score " + studentNumber);

                    rowValid = false;
                }


                // now we can validate.

                
                if (rowValid) {

                    // first make sure the student exists
                    Boolean studentExists = i.importRepo.studentExists(districtId + "." + studentNumber);

                    if (studentExists) {



                        int intScore = 0;

                        try {
                            intScore = Integer.parseInt(stringScore);
                        }
                        catch (Exception ex) {
                            // Bad Score
                            System.out.println("BAD SCORE : " + stringScore + " - " + studentNumber);
                            intScore =0;
                        }

                        //System.out.println("ROW: " + benchmarkPeriod + " : " + schoolYear + " : " + proficiency + " : " + studentNumber);


                        
                        if (intScore > 0) {

                            String period = MappingHelper.Dibels8_period(benchmarkPeriod);


                            String subject = "Reading";  // constant
                            String csaCode = "R";   // constant

                                
                            String proficiencyCode = MappingHelper.MClass_proficiencyCode (proficiency);

                            rowsLoaded++;

                            // make sure the student exists

                            //i.importRepo.sisMclassAdd(studentNumber, schoolYear, period, subject, proficiency, proficiencyCode, intScore, csaCode);

                        }
                    }
                    // else - Student does not exist
                }
                else {
                    System.out.println ("Invalid Row " + studentNumber);
                
                }



            }

            System.out.println( "   ROWS: " + rowsRead + "  Loaded: " + rowsLoaded);

            System.out.println("----------------- END -------------");

            return true;

        }
        catch (Exception ex) {
            System.out.println("----------------- FAILED -------------");

            System.out.println(filePath);
            System.out.println("FAILD VALIDATE : ");
            System.out.println(ex.getMessage());
            return false;
        }

    }



    public static Boolean GenericMClass_Lexie_Load(int districtId, String filePath, String schoolYear, String benchmarkPeriod, int studentNumberCol, int proficiencyCol, int scoreCol ) {
         int rowsRead = 0;
        int rowsLoaded = 0;

        System.out.println("----------------- START -------------");
        System.out.println(filePath);

        try {
            UserFileService msp = new UserFileService();

            List<String[]> data;

            String[] fr;

            data = msp.readCsvFile(filePath);

            fr = data.removeFirst();

            System.out.println("Starting Data");

            Boolean rowValid = true;

            for (String [] row : data) {

                rowsRead++;
                rowValid = true;

                String proficiency = row[proficiencyCol];

                if (proficiency.isEmpty())
                    rowValid = false;

                String studentNumber = row[studentNumberCol];

                String stringScore = row[scoreCol];

                if (stringScore.isEmpty()) {
                    System.out.println ("Empty Score " + studentNumber);

                    rowValid = false;
                }


                // now we can validate.

                
                if (rowValid) {

                    // first make sure the student exists
                    Boolean studentExists = i.importRepo.studentExists(districtId + "." + studentNumber);

                    if (studentExists) {



                        int intScore = 0;

                        try {
                            intScore = Integer.parseInt(stringScore);
                        }
                        catch (Exception ex) {
                            // Bad Score
                            System.out.println("BAD SCORE : " + stringScore + " - " + studentNumber);
                            intScore =0;
                        }

                        //System.out.println("ROW: " + benchmarkPeriod + " : " + schoolYear + " : " + proficiency + " : " + studentNumber);


                        
                        if (intScore > 0) {

                            String period = MappingHelper.Dibels8_period(benchmarkPeriod);


                            String subject = "Reading";  // constant
                            String csaCode = "R";   // constant

                                
                            String proficiencyCode = MappingHelper.MClass_proficiencyCode (proficiency);

                            rowsLoaded++;

                            // make sure the student exists

                            i.importRepo.sisMclassAdd(studentNumber, schoolYear, period, subject, proficiency, proficiencyCode, intScore, csaCode);

                        }
                    }
                    // else - Student does not exist
                }
                else {
                    System.out.println ("Invalid Row " + studentNumber);
                
                }



            }

            System.out.println( "   ROWS: " + rowsRead + "  Loaded: " + rowsLoaded);

            System.out.println("----------------- END -------------");

            return true;

        }
        catch (Exception ex) {
            System.out.println("----------------- FAILED -------------");

            System.out.println(filePath);
            System.out.println("FAILD VALIDATE : ");
            System.out.println(ex.getMessage());
            return false;
        }

    }


    // Want Period
    // School Year col or Date col.
    // proficiency
    // student number.

    public static Boolean GenericMClass_Validate(int districtId, String filePath, int periodCol, int schoolYearCol, int dateCol, int proficiencyCol, int scoreCol, int studentNumberCol ) {
        

        int rowsRead = 0;
        int rowsLoaded = 0;

        System.out.println("----------------- START -------------");
        System.out.println(filePath);

        try {
            UserFileService msp = new UserFileService();

            List<String[]> data;

            String[] fr;

            data = msp.readCsvFile(filePath);

            fr = data.removeFirst();

            System.out.println("Starting Data");

            Boolean rowValid = true;

            for (String [] row : data) {

                rowsRead++;
                rowValid = true;
                String benchmarkPeriod = row[periodCol].trim();
                String schoolYear = "";
                if (schoolYearCol == -1) {
                    // get from date
                    //System.out.println("Checking Data: " + row[dateCol]);

                    if (row[dateCol].isEmpty())
                        rowValid = false;
                    else
                        schoolYear =  MappingHelper.SchoolYearFromDate(row[dateCol]);
                }
                else {
                    schoolYear = row[schoolYearCol];
                }
                String proficiency = row[proficiencyCol];

                if (proficiency.isEmpty())
                    rowValid = false;

                String studentNumber = row[studentNumberCol];

                String stringScore = row[scoreCol];

                if (stringScore.isEmpty()) {
                    System.out.println ("Empty Score " + studentNumber);

                    rowValid = false;
                }


                // now we can validate.

                
                if (rowValid) {

                    // first make sure the student exists
                    Boolean studentExists = i.importRepo.studentExists(districtId + "." + studentNumber);

                    if (studentExists) {



                        int intScore = 0;

                        try {
                            intScore = Integer.parseInt(stringScore);
                        }
                        catch (Exception ex) {
                            // Bad Score
                            System.out.println("BAD SCORE : " + stringScore + " - " + studentNumber);
                            intScore =0;
                        }

                        //System.out.println("ROW: " + benchmarkPeriod + " : " + schoolYear + " : " + proficiency + " : " + studentNumber);


                        
                        if (intScore > 0) {

                            String period = MappingHelper.Dibels8_period(benchmarkPeriod);


                            String subject = "Reading";  // constant
                            String csaCode = "R";   // constant

                                
                            String proficiencyCode = MappingHelper.MClass_proficiencyCode (proficiency);

                            rowsLoaded++;

                            // make sure the student exists

                            //i.importRepo.sisMclassAdd(studentNumber, schoolYear, period, subject, proficiency, proficiencyCode, intScore, csaCode);

                        }
                    }
                    // else - Student does not exist
                }
                else {
                    System.out.println ("Invalid Row " + studentNumber);
                
                }



            }

            System.out.println( "   ROWS: " + rowsRead + "  Loaded: " + rowsLoaded);

            System.out.println("----------------- END -------------");

            return true;

        }
        catch (Exception ex) {
            System.out.println("----------------- FAILED -------------");

            System.out.println(filePath);
            System.out.println("FAILD VALIDATE : ");
            System.out.println(ex.getMessage());
            return false;
        }


        // now see if we can get data from the rows.boscoClient

        
    }

    public static Boolean GenericMClass_Load(int districtId, String filePath, int periodCol, int schoolYearCol, int dateCol, int proficiencyCol, int scoreCol, int studentNumberCol ) {
        
        System.out.println(filePath);

        int rowsRead = 0;
        int rowsLoaded = 0;
        String schoolYear = "";

        try {
            UserFileService msp = new UserFileService();

            List<String[]> data;

            String[] fr;

            data = msp.readCsvFile(filePath);

            fr = data.removeFirst();


            Boolean rowValid = true;

            for (String [] row : data) {

                rowsRead++;
                rowValid = true;
                String benchmarkPeriod = row[periodCol].trim();
                schoolYear = "";
                if (schoolYearCol == -1) {
                    // get from date
                    //System.out.println("Checking Data: " + row[dateCol]);

                    if (row[dateCol].isEmpty())
                        rowValid = false;
                    else
                        schoolYear =  MappingHelper.SchoolYearFromDate(row[dateCol]);
                }
                else {
                    schoolYear = row[schoolYearCol];
                }
                String proficiency = row[proficiencyCol];

                if (proficiency.isEmpty())
                    rowValid = false;

                String studentNumber = row[studentNumberCol];

                String stringScore = row[scoreCol];

                if (stringScore.isEmpty()) {
                    //System.out.println ("Empty Score " + studentNumber);

                    rowValid = false;
                }


                // now we can validate.

                
                if (rowValid) {

                    // first make sure the student exists
                    Boolean studentExists = i.importRepo.studentExists(districtId + "." + studentNumber);

                    if (studentExists) {



                        int intScore = 0;

                        try {
                            intScore = Integer.parseInt(stringScore);
                        }
                        catch (Exception ex) {
                            // Bad Score
                            //System.out.println("BAD SCORE : " + stringScore + " - " + studentNumber);
                            intScore =0;
                        }

                        //System.out.println("ROW: " + benchmarkPeriod + " : " + schoolYear + " : " + proficiency + " : " + studentNumber);


                        
                        if (intScore > 0) {

                            String period = MappingHelper.Dibels8_period(benchmarkPeriod);


                            String subject = "Reading";  // constant
                            String csaCode = "R";   // constant

                                
                            String proficiencyCode = MappingHelper.MClass_proficiencyCode (proficiency);

                            // make sure the student exists

                            rowsLoaded++;
                            i.importRepo.sisMclassAdd(studentNumber, schoolYear, period, subject, proficiency, proficiencyCode, intScore, csaCode);

                        }
                    }
                    // else - Student does not exist
                }
                // else {
                //     System.out.println ("Invalid Row " + studentNumber);
                
                // }



            }

            
            File file = new File(filePath);
            String fileName = file.getName();

            i.importRepo.logFile(fileName, "MClass", schoolYear, true, "Total : " + rowsRead + "  Loaded: " + rowsLoaded);

            return true;

        }
        catch (Exception ex) {
            System.out.println("----------------- FAILED -------------");

            System.out.println(filePath);
            System.out.println("FAILD VALIDATE : ");
            System.out.println(ex.getMessage());
            return false;
        }


        // now see if we can get data from the rows.boscoClient

        
    }




    private static String ActionMapToDiscipline (String action) {
        String disciplineType = 
        switch(action) {

            case "Continue Other District DAEP", 
                "DAEP Placement (Student Not Expelled)",
                "Continue Prior Year DAEP            Sp.Ed.",
                "Continue Prior Year DAEP" -> "daep";

            case "Out-of-School Suspension (3 Day Limit)" -> "oss";
            case "In-School Suspension" -> "iss";
            default -> "";    

        
        };

        return disciplineType;
    }

    

    public static float daysToFloat (String daysString) {
        float days = 0f;

        try {
            days = Float.parseFloat(daysString);
        }
        catch (Exception ex) {
            // do nothing.
        }

        return days;
    }


    public static void LoadAttendanceLedgerUplift (int districtId, String filePath) throws Exception {

        // for a student + date, we build up the perid as a list of them.
        // this may be one one line, or may be in multiple lines.
        SisAttendance sa;

        // we should be able to determin the school year by the date, but maybe better to read it in.
        HashMap<String, SisAttendance> studentAttendance = new HashMap<>();


        int rowsRead = 0;
        int rowsLoaded = 0;


        // "StudentSourceID","StudentNumber","DateOfAttendance","AttendanceCode","SchoolYear"
        System.out.println("----------------- START -------------");
        System.out.println(filePath);

        try {
            UserFileService msp = new UserFileService();

            List<String[]> data;

            String[] fr;

            data = msp.readCsvFile(filePath);

            fr = data.removeFirst();

            System.out.println("Starting Data");

            Boolean rowValid = true;

            for (String [] row : data) {

                rowsRead++;
                rowValid = true;

                String studentNumber = row[0];     // 020281
                String event = row[1].equals("Tardy") ? "T" : "A";  // Tardy OR Absence
                String date = row[2];           // 08/11/2025
                String rowPeriod = row[3];      // 3  OR blank  (for abesent)
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

                    String schoolYear = MappingHelper.SchoolYearFromDate(date);

                    sa = new SisAttendance();
                    sa.date = date;
                    sa.event = event;
                    sa.schoolYear = schoolYear;
                    sa.period = rowPeriod;
                    studentAttendance.put(key, sa);
                }

             



            }

            // now lest see what we got.

            rowsLoaded = 0;
            for (String key : studentAttendance.keySet()) {
                sa = studentAttendance.get(key);
                rowsLoaded++;
                

                String studentNumber = key.split(":")[0];

                // System.out.println("Key: " + key + " - " + studentNumber);


                String formattedDate = ImportHelper.DateToStdFormat(sa.date);

                System.out.println(studentNumber + " : " + formattedDate + " " + sa.schoolYear + " : " + sa.period);

                i.importRepo.sisAttendanceAdd(studentNumber, sa.event, sa.schoolYear, formattedDate, sa.period);


            
            }

            // 

            System.out.println( "   ROWS: " + rowsRead + "  Loaded: " + rowsLoaded);

            System.out.println("----------------- END -------------");

        }
        catch (Exception ex) {
            System.out.println("EXCEPTION");
            System.out.println(ex.getMessage());
            System.out.println("EXCEPTION");
        }



        // so once we have our map.

        // foreach key in map
        // we can get student + date from the key
        // now we can get the perod from the data
        // we still need the schoolYear
    }

    public static void LoadAttendanceLedgerMelissa (int districtId, String filePath) throws Exception {

        // for a student + date, we build up the perid as a list of them.
        // this may be one one line, or may be in multiple lines.
        SisAttendance sa;

        // we should be able to determin the school year by the date, but maybe better to read it in.
        HashMap<String, SisAttendance> studentAttendance = new HashMap<>();


         int rowsRead = 0;
        int rowsLoaded = 0;


        // "StudentSourceID","StudentNumber","DateOfAttendance","AttendanceCode","SchoolYear"
        System.out.println("----------------- START -------------");
        System.out.println(filePath);

        try {
            UserFileService msp = new UserFileService();

            List<String[]> data;

            String[] fr;

            data = msp.readCsvFile(filePath);

            fr = data.removeFirst();

            System.out.println("Starting Data");

            Boolean rowValid = true;

            for (String [] row : data) {

                rowsRead++;
                rowValid = true;

                String studentNumber = row[1];     // 020281
                String date = row[2];           // 8/22/2025
                String rowPeriod = row[3];      // 4-9 or HRM


                // ok, lets try to buld them up.

                String key = studentNumber + ":" + date;
                //System.out.println("Key : " + key);

                if (studentAttendance.containsKey(key)) {
                    //System.out.println(" -- Found key");
                    sa = studentAttendance.get(key);
                    sa.period += ", " + rowPeriod;
                }
                else {

                    String schoolYear = MappingHelper.SchoolYearFromYear(row[4]) ;    // 2026 => 2025-2026

                    sa = new SisAttendance();
                    sa.date = date;
                    sa.event = "A";
                    sa.schoolYear = schoolYear;
                    sa.period = rowPeriod;
                    studentAttendance.put(key, sa);
                }

             



            }

            // now lest see what we got.

            rowsLoaded = 0;
            for (String key : studentAttendance.keySet()) {
                sa = studentAttendance.get(key);
                rowsLoaded++;
                

                String studentNumber = key.split(":")[0];

                // System.out.println("Key: " + key + " - " + studentNumber);


                String formattedDate = ImportHelper.DateToStdFormat(sa.date);

                System.out.println(studentNumber + " : " + formattedDate + " " + sa.schoolYear + " : " + sa.period);

                i.importRepo.sisAttendanceAdd(studentNumber, sa.event, sa.schoolYear, formattedDate, sa.period);


            
            }

            // 

            System.out.println( "   ROWS: " + rowsRead + "  Loaded: " + rowsLoaded);

            System.out.println("----------------- END -------------");

        }
        catch (Exception ex) {
            System.out.println("EXCEPTION");
            System.out.println(ex.getMessage());
            System.out.println("EXCEPTION");
        }



        // so once we have our map.

        // foreach key in map
        // we can get student + date from the key
        // now we can get the perod from the data
        // we still need the schoolYear
    }

    public static void LoadDisciplineLedger (int districtId, String filePath) throws Exception {

        File file = new File(filePath);
        String fileName = file.getName();
        System.out.println("MClass - Filename: " + fileName); // Output: Filename: myFile.txt


        String schoolYear = "N/A";

        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

        FlatFileItemReader<DisciplineLedger> cr = tsfr.disciplineLedgerReader(filePath);

        cr.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ disciplineLedgerReader ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;
        int totalCompleted = 0;



        //HashMap<String, Integer> actions = new HashMap<>();


        HashMap<String, DisciplineHelper> studentDisipline = new HashMap<>();
        // for (String value : theParams) {
        //     String decodedValue = URLDecoder.decode(value, StandardCharsets.UTF_8);
        //     String[] split = decodedValue.split("=");
        //     if (split.length == 2) {
        //         result.put(split[0], split[1]);
        //     } else {
        //         result.put("filter", decodedValue.substring(7));
        //     }
        // }


        DisciplineLedger cc = cr.read();
            //String schoolYear = "2024-2025";  // should be able to get this from TermName

            //String termName = cc.getTermName();

        schoolYear = cc.schoolYear;

        float days = 0f;
        DisciplineHelper disciplineHelper;
        while (cc != null) {

            total++;

            // lets see what we got

            String disciplineType = ActionMapToDiscipline(cc.action);

            

            switch (disciplineType) {
                // case "":
                //     // for validation.
                //     if (actions.containsKey(cc.action)) {
                //         int v = actions.get(cc.action);
                //         actions.put(cc.action, v + 1);
                //     }
                //     else {
                //         actions.put(cc.action, 1);
                //     }

                //     break;
            
                case "daep":

                    days = daysToFloat(cc.days);


                    if (studentDisipline.containsKey(cc.studentSourceID)) {
                        disciplineHelper = studentDisipline.get(cc.studentSourceID);
                        disciplineHelper.daep += days;
                        studentDisipline.put(cc.studentSourceID, disciplineHelper);
                    }
                    else {
                        disciplineHelper = new DisciplineHelper();
                        disciplineHelper.daep = days;
                        disciplineHelper.iss = 0;
                        disciplineHelper.oss = 0;

                        studentDisipline.put(cc.studentSourceID, disciplineHelper);

                    }
                    System.out.println("DAEP: " + cc.studentSourceID + " " + cc.days);
                    break;
                case "iss":
                      days = daysToFloat(cc.days);


                    if (studentDisipline.containsKey(cc.studentSourceID)) {
                        disciplineHelper = studentDisipline.get(cc.studentSourceID);
                        disciplineHelper.iss += days;
                        studentDisipline.put(cc.studentSourceID, disciplineHelper);
                    }
                    else {
                        disciplineHelper = new DisciplineHelper();
                        disciplineHelper.daep = 0;
                        disciplineHelper.iss = days;
                        disciplineHelper.oss = 0;

                        studentDisipline.put(cc.studentSourceID, disciplineHelper);

                    }
                    System.out.println("ISS: " + cc.studentSourceID + " " + cc.days);
                    break;
                case "oss":
                      days = daysToFloat(cc.days);


                    if (studentDisipline.containsKey(cc.studentSourceID)) {
                        disciplineHelper = studentDisipline.get(cc.studentSourceID);
                        disciplineHelper.oss += days;
                        studentDisipline.put(cc.studentSourceID, disciplineHelper);
                    }
                    else {
                        disciplineHelper = new DisciplineHelper();
                        disciplineHelper.daep = 0;
                        disciplineHelper.iss = 0;
                        disciplineHelper.oss = days;

                        studentDisipline.put(cc.studentSourceID, disciplineHelper);

                    }
                    System.out.println("OSS: " + cc.studentSourceID + " " + cc.days);
                    break;
                default:
                     //System.out.println("DEFAULT--" + cc.action);
                    
                    break;
            }

           // System.out.println("Student: " + cc.studentSourceID + "  Type: " + cc.action + " [" + cc.days + "]") ;

         

            cc = cr.read();

        }

        // now lets see what we have

    //     System.out.println("NOT USED ONES");

    //    for (String key : actions.keySet()) {
    //         System.out.println("Action: " + key + " : " + actions.get(key));
    //     }

        //System.out.println("----   What we got ----");
        for (String stnum : studentDisipline.keySet()) {
            DisciplineHelper dh = studentDisipline.get(stnum);

            // let see if any are floats
            
            //System.out.println (stnum + "  : " + dh.daep + " : " + dh.iss + " : " + dh.oss);

            if ((dh.iss + dh.oss + dh.daep) > 0.0f) {
                count++;

                //i.importRepo.sisDiscipline(stnum, Integer.toString((int)dh.iss) , Integer.toString((int)dh.oss), Integer.toString((int)dh.daep), "",  schoolYear);
                i.importRepo.sisDiscipline(stnum, floatToStringTrimZeros(dh.iss)  , floatToStringTrimZeros(dh.oss) , floatToStringTrimZeros(dh.daep), "",  schoolYear);
            }
        }

        
        i.importRepo.logFile (fileName, "Discipline", schoolYear, false, "Total: " + total + " - completed: " + totalCompleted +  "  - Imported : " + count);

        
        
        System.out.println("  Total: " + total + " - completed: " + totalCompleted +  "  - Imported : " + count);

        System.out.println(("-----------------------"));
    }


    private static String floatToStringTrimZeros(float f)
    {
        BigDecimal bd = new BigDecimal(f);
        bd.stripTrailingZeros();
        return bd.toPlainString();
    }

    // studentNumber, schoolYear, period, subject, proficiency, proficiencyCode, score, csaCode

     public static void LoadMClassDibels8 (int districtId, String filePath, Boolean useFileStudentId) throws Exception  {

        File file = new File(filePath);
        String fileName = file.getName();
        System.out.println("MClass - Filename: " + fileName); // Output: Filename: myFile.txt


        String schoolYear = "N/A";

        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

        FlatFileItemReader<DibelsMClass> cr = tsfr.dibelesMClassReader(filePath);

        cr.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ dibelesMClassReader ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;
        int totalCompleted = 0;

        
        DibelsMClass cc = cr.read();
            //String schoolYear = "2024-2025";  // should be able to get this from TermName

            //String termName = cc.getTermName();


        while (cc != null) {

            total++;


            String completionStatus = cc.getCompletionStatus();
            String assessment = cc.getAssessment();   // Should be mCLASS only!!
            String assessmentEdition = cc.getAssessmentEdition();  // shuld be DIBELS 8th Edition ONLY

            
            //String level = cc.getCompositeLevel();
            ///String testScore = cc.getCompositeScore();


            // String studentIdS = cc.getSecondaryStudentID();
            // String studentIdP = cc.getStudentPrimaryID();

            // xx String studentId = cc.getSecondaryStudentID();

            if (completionStatus.equals("Complete")) {
                totalCompleted++;

                String firstName = cc.getStudentFirstName();
                String lastName = cc.getStudentLastName();
                String dob = cc.getDateofBirth();


                // we need to get data into mm/dd/yyyy format)

                if (!dob.isEmpty()) {

                    // the patter must be yyyy-MM-dd
                    // that is it already.

                    
                    // System.out.println ("Student: " + lastName + ", " + firstName + ", [" + dob + "] " + newDob + " - " + ld.toString() );

                    String studentNumber = i.importRepo.studentNumberFromDemographics(districtId, firstName, lastName, dob);

                    

                    if (studentNumber != null) {
                        //System.out.println("       --- "  + studentNumber);


                        schoolYear = cc.getSchoolYear();         /// shoudl be correct.

                        String benchmarkPeriod = cc.getBenchmarkPeriod();   // Map this " BOY or Fall" -> Fall

                        String period = MappingHelper.Dibels8_period(benchmarkPeriod);


                        String subject = "Reading";  // constant
                        String csaCode = "R";   // constant

                    
                        String proficiency = cc.getCompositeLevel();

                        String proficiencyCode = MappingHelper.MClass_proficiencyCode (proficiency);
                        if (proficiencyCode != "") {

                            count++;

                            String testScore = cc.getCompositeScore();
                            // shoudl we check?
                            int score = Integer.parseInt(testScore);

                            //String studentNumber, 
                            // String schoolYear, 
                            // String period, 
                            // String subject, 
                            // String proficiency, 
                            // String proficiencyCode, 
                            // int score, 
                            // String csaCode

                            // String studentNumber, String schoolYear, String period, String subject, String proficiency, String proficiencyCode, int score, String csaCode
                            i.importRepo.sisMclassAdd(studentNumber, schoolYear, period, subject, proficiency, proficiencyCode, score, csaCode);

                            //System.out.println ("sisMclassAdd(" + studentNumber + ", " + schoolYear + ", " + period + ", " + subject + ", " + proficiency + "," + proficiencyCode + ", " + score + ", " + csaCode + ");");
                        }
                        
                    }
                }
                else {


                    System.out.println ("NOT FOUND Student: " + lastName + ", " + firstName + ", " + dob );

                    
                }
            } // End completion status

          

            cc = cr.read();
        }


        i.importRepo.logFile (fileName, "Map", schoolYear, false, "Total: " + total + " - completed: " + totalCompleted +  "  - Imported : " + count);

        
        
        System.out.println("  Total: " + total + " - completed: " + totalCompleted +  "  - Imported : " + count);

        System.out.println(("-----------------------"));

    }

}


