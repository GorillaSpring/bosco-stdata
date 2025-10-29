package com.bosco.stdata.sisDataFiles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.teaModel.CelinaCombo;
import com.bosco.stdata.teaModel.DibelsMClass;
import com.bosco.stdata.teaModel.DisciplineFileCelina;
import com.bosco.stdata.teaModel.FindUsers;
import com.bosco.stdata.teaModel.GradeFileCelina;
import com.bosco.stdata.teaModel.GradeFileMelissa;
import com.bosco.stdata.teaModel.MapCourseNameCsaCode;
import com.bosco.stdata.utils.MappingHelper;
import com.bosco.stdata.utils.TeaStaarFlatFileReader;

import jakarta.annotation.PostConstruct;

@Component
public class CsvFiles {

    private final BoscoApi boscoApi;

    private final AppConfig appConfig;
    
   
    @Autowired
    ImportRepo importRepo;

    
    private static CsvFiles i;


    CsvFiles(AppConfig appConfig, BoscoApi boscoApi) {
        this.appConfig = appConfig;
        this.boscoApi = boscoApi;
    }


   
    @PostConstruct
    public void init() {
        System.out.println("TeaFiles - init()");
        i = this;
    }

    public static void LoadFindUsers (String filePath) throws Exception {
        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        

        FlatFileItemReader<FindUsers> cr = tsfr.findUsers(filePath);

        cr.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ LoadFindUsers ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;


        List<String> emails = new ArrayList<>();
        
        FindUsers cc = cr.read();
            //String schoolYear = "2024-2025";  // should be able to get this from TermName

            //String termName = cc.getTermName();


        while (cc != null) {

            if (emails.contains(cc.email)) {
                System.out.println("FOUND DUP: " + cc.id + " - " + cc.email);
            }
            else {
                emails.add(cc.email);
            }

            
            cc = cr.read();
        }


        i.importRepo.logTea(filePath, "  Total: " + total + "  - Imported : " + count);
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    }


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


        i.importRepo.logTea(filePath, "  Total: " + total + "  - Imported : " + count);
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    }
    

    public static void LoadGradesMelissa (int districtId, String filePath) throws Exception {
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
                        String schoolYear = MappingHelper.SchoolYearFromYear(cc.getSchoolYear());

                        

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


        i.importRepo.logTea(filePath, "  Total: " + total + "  - Imported : " + count);
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    }

    public static void LoadCelinaDiscipline (int districtId, String filePath) throws Exception {
    
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


            
            
            String grade = i.importRepo.gradeForStudentId(districtId + "." + cc.studentNumber);

            String schoolYear = MappingHelper.SchoolYearFromYear(cc.numericYear);
            
            
            if (!grade.isEmpty()) {
                i.importRepo.sisDiscipline(cc.studentNumber, cc.iSS, cc.oSS, cc.dAEP, grade, schoolYear);
                count++;
            }
            else {
                System.out.println("Did not find student : " + cc.studentNumber);
            }


            cc = cr.read();
        }


        i.importRepo.logTea(filePath, "  Total: " + total + "  - Imported : " + count);
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    
    }



    public static void LoadGradesCelina (int districtId, String filePath) throws Exception {
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
            


            // i.importRepo.setMapCourseCsaCode(districtId, cc.getCourseName(), "");

            
            String scoreString = cc.getCourseGrade().replace("*", "");
            


            if (!scoreString.isEmpty()) {

                // first see if it is something we load anyway

                // System.out.print("CHECKING Course: " + cc.courseName);

                String csaCode = "";
                

                    csaCode = i.importRepo.csaCodeForCourseName(districtId, cc.courseName.replace(",", ""));
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
                        String schoolYear = MappingHelper.SchoolYearFromYear(cc.getSchoolYear());

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


        i.importRepo.logTea(filePath, "  Total: " + total + "  - Imported : " + count);
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));
    }
    
    
    

    public static void LoadComboStudentAssessment (int districtId, String filePath, Boolean useStudentSourceId) throws Exception  {

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
                        
                        int score = Integer.parseInt(cc.getPercentCorrect());

                        //String testStartDate = cc.getTestStartDate();

                        String period = MappingHelper.MapPeriod(cc.getTermName());
                        String proficiency = MappingHelper.MapProficiency(cc.getAchievementQuintile());
                        String proficiencyCode = MappingHelper.MapProficiencyCode(cc.getAchievementQuintile());

                        String schoolYear = MappingHelper.SchoolYearFromDate(cc.getTestStartDate());

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


        i.importRepo.logTea(filePath, "  Total: " + total + "  - Imported : " + count);
        
        System.out.println("  Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));

    }



     public static void LoadDibels8 (int districtId, String filePath, Boolean useFileStudentId) throws Exception  {

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


                        String schoolYear = cc.getSchoolYear();         /// shoudl be correct.

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


        i.importRepo.logTea(filePath, "  Total: " + total + " - completed: " + totalCompleted +  "  - Imported : " + count);
        
        System.out.println("  Total: " + total + " - completed: " + totalCompleted +  "  - Imported : " + count);

        System.out.println(("-----------------------"));

    }

}


