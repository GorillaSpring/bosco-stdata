package com.bosco.stdata.sisDataFiles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.teaModel.CelinaCombo;
import com.bosco.stdata.teaModel.DibelsMClass;
import com.bosco.stdata.utils.MappingHelper;
import com.bosco.stdata.utils.TeaStaarFlatFileReader;

import jakarta.annotation.PostConstruct;

@Component
public class CsvFiles {

    private final AppConfig appConfig;
    
   
    @Autowired
    ImportRepo importRepo;

    
    private static CsvFiles i;


    CsvFiles(AppConfig appConfig) {
        this.appConfig = appConfig;
    }


   
    @PostConstruct
    public void init() {
        System.out.println("TeaFiles - init()");
        i = this;
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
                    if (studentNumber == null)
                        System.out.println("  ---------------------------------  NULLL: " + studentId);
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
                // else {


                //     System.out.println ("Student: " + lastName + ", " + firstName + ", " );

                //     System.out.println("Empty DOB");
                // }
            } // End completion status

          

            cc = cr.read();
        }


        i.importRepo.logTea(filePath, "  Total: " + total + " - completed: " + totalCompleted +  "  - Imported : " + count);
        
        System.out.println("  Total: " + total + " - completed: " + totalCompleted +  "  - Imported : " + count);

        System.out.println(("-----------------------"));

    }

}


