package com.bosco.stdata.sisDataFiles;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.teaModel.CelinaCombo;

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

    private static String MapProficiency (String achievementQuintile) throws Exception {
        String pro = 
        switch(achievementQuintile) {
            case "Low" -> "Quintile 1";
            case "LoAvg" -> "Quintile 2";
            case "Avg" -> "Quintile 3";
            case "HiAvg" -> "Quintile 4";            
            case "High" -> "Quintile 5";
            default -> throw new Exception("Unknown MapProficiency");

        };

        return pro;
        
    }

    private static String MapSubject (String course) throws Exception {
         String subject = 
        switch(course) {
            case "Math K-12" -> "Math";
            case "Integrated Mathematics" -> "Math";
            case "Integrated Mathematics 1" -> "Math";
            case "Integrated Mathematics 2" -> "Math";
            case "Integrated Mathematics 3" -> "Math";
            case "Algebra 1" -> "Math";
            case "Algebra 2" -> "Math";
            case "Geometry" -> "Math";

            case "Science K-12" -> "Science";
            case "Life Sciences" -> "Science";
            case "Biology/Life Sciences" -> "Science";

            case "Reading" -> "Reading";
            case "Reading (Spanish)" -> "Reading";

            case "Language Usage" -> "Language";

            
            //case "" -> null;
            default -> throw new Exception("Unknown MapSubject :" + course);

        };

        return subject;
    }

     private static String MapProficiencyCode (String achievementQuintile) throws Exception {
        String pro = 
        switch(achievementQuintile) {
            case "Low" -> "Q1";
            case "LoAvg" -> "Q2";
            case "Avg" -> "Q3";
            case "HiAvg" -> "Q4";            
            case "High" -> "Q5";
            default -> throw new Exception("Unknown MapProficiencyCode");

        };

        return pro;
        
    }

     private static String MapCsaCode (String course) throws Exception {
        String pro = 
        switch(course) {

            case "Math K-12" -> "M";
            case "Integrated Mathematics" -> "M";
            case "Integrated Mathematics 1" -> "M";
            case "Integrated Mathematics 2" -> "M";
            case "Integrated Mathematics 3" -> "M";
            case "Algebra 1" -> "M";
            case "Algebra 2" -> "M";
            case "Geometry" -> "M";

            case "Science K-12" -> "C";
            case "Life Sciences" -> "C";
            case "Biology/Life Sciences" -> "C";

            case "Reading" -> "R";
            case "Reading (Spanish)" -> "R";

            case "Language Usage" -> "L";


            default -> throw new Exception("Unknown MapCsaCode :" + course);    

        
        };

        return pro;
        
    }

    private static String MapPeriod (String termName) {
        // Spring 2024-2025  => Spring.

        return termName.split(" ")[0];
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
                    System.out.println("Checking Student : " + studentId);
                    studentNumber = i.importRepo.studentNumberFromSourceId(studentId);
                    if (studentNumber != null)
                        System.out.println("   -- " + studentNumber);
                    else   
                        System.out.println("  ---------------------------------  NULLL ");
                }
                else
                    studentNumber = studentId;



                if (studentNumber != null) {

                



                    //String termName = cc.getTermName();
                    String course = cc.getCourse();
                    
                    String subject = MapSubject(course);

                    if (subject != null) {
                        
                        int score = Integer.parseInt(cc.getPercentCorrect());

                        //String testStartDate = cc.getTestStartDate();

                        String period = MapPeriod(cc.getTermName());
                        String proficiency = MapProficiency(cc.getAchievementQuintile());
                        String proficiencyCode = MapProficiencyCode(cc.getAchievementQuintile());

                        String schoolYear = TeaStaarFlatFileReader.SchoolYearFromDate(cc.getTestStartDate());

                        String csaCode = MapCsaCode(course);

                        

                        //i.importRepo.sisMapAdd(studentNumber, schoolYear, period, subject, proficiency, proficiencyCode, count, csaCode);


                        // String studentNumber, String schoolYear, String period, String subject, String proficiency, String proficiencyCode, int score, String csaCode
                        i.importRepo.sisMapAdd(studentNumber, schoolYear, period, subject, proficiency, proficiencyCode, score, csaCode);

                        System.out.println("(" + studentNumber + ":" +studentId +  ", " + schoolYear + ", " + period + ", " + subject + ", " + proficiency + ", " + proficiencyCode + ", " + score + ", " + csaCode + ")");

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

}


