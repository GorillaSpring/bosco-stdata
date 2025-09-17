package com.bosco.stdata.distictImports;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

//import org.springframework.batch.core.repository.persistence.ExecutionContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.FlatFileItemReader<com.bosco.stdata.teaModel.CelinaCombo>;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.model.ImportDefinition;
import com.bosco.stdata.model.ImportResult;
import com.bosco.stdata.model.ImportSetting;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.BoscoClient;
import com.bosco.stdata.sisDataFiles.CsvFiles;
import com.bosco.stdata.teaModel.BoscoProficiency;
import com.bosco.stdata.teaModel.CelinaCombo;
import com.bosco.stdata.teaModel.FixedTest;
import com.bosco.stdata.teaModel.Person;
import com.bosco.stdata.teaModel.Star2024;
import com.bosco.stdata.teaModel.Star2024EOC;
import com.bosco.stdata.teaModel.Telpas2024;
import com.bosco.stdata.utils.TeaStaarFlatFileReader;

import jakarta.annotation.PostConstruct;


@Component
public class Testing {

    private final BoscoClient boscoClient;

    private final AppConfig appConfig;

    // private final teaModel.CelinaCombo> celinaComboItemReader;

    // private final CelinaFiles celinaFiles;

    // private final BoscoClient boscoClient;

    // private final AppConfig appConfig;

    // this is just for testing while we are doing dev.
    // not a real import

    @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;
    

    private static Testing i;


    Testing(AppConfig appConfig, BoscoClient boscoClient) {
        this.appConfig = appConfig;
        this.boscoClient = boscoClient;
    }


    @PostConstruct
    public void init() {
        System.out.println("Testing - init()");
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
            default -> throw new Exception("Unknown MapProficiency " + achievementQuintile);

        };

        return pro;
        
    }

     private static String MapProficiencyCode (String achievementQuintile) throws Exception {
        String pro = 
        switch(achievementQuintile) {
            case "Low" -> "Q1";
            case "LoAvg" -> "Q2";
            case "Avg" -> "Q3";
            case "HiAvg" -> "Q4";            
            case "High" -> "Q5";
            default -> throw new Exception("Unknown MapProficiencyCode " + achievementQuintile);

        };

        return pro;
        
    }

     private static String MapCsaCode (String subject) throws Exception {
        String pro = 
        switch(subject) {
            case "Science" -> "C";
            case "Language Arts" -> "R";
            case "Mathematics" -> "M";
            default -> throw new Exception("Unknown MapCsaCode " + subject);

        };

        return pro;
        
    }

    private static String MapPeriod (String termName) {
        // Spring 2024-2025  => Spring.

        return termName.split(" ")[0];
    }
    
    
    
  

    public static void Test(String importDefId) {
        System.out.println("Test Starting");

        // try to create a file reader


        try {



            // 4830120

            // 4830120

            // Celina
            // So this one is Student_1234
            CsvFiles.LoadDibels8(4830120, "C:/test/importBase/tea/2022_2023_2024_2025 mClass/dibels8_BM_2021-2022_BOY_MOY_EOY_grades-KG-01-02-03-04-05-06_2025-09-08_19-56-59.csv", false);

            CsvFiles.LoadDibels8(4830120, "C:/test/importBase/tea/2022_2023_2024_2025 mClass/dibels8_BM_2022-2023_BOY_MOY_EOY_grades-KG-01-02-03-04-05-06_2025-09-08_19-57-46.csv", false);
            CsvFiles.LoadDibels8(4830120, "C:/test/importBase/tea/2022_2023_2024_2025 mClass/dibels8_BM_2023-2024_BOY_MOY_EOY_grades-KG-01-02-03-04-05-06_2025-09-08_19-58-45.csv", false);
            CsvFiles.LoadDibels8(4830120, "C:/test/importBase/tea/2022_2023_2024_2025 mClass/dibels8_BM_2024-2025_BOY_MOY_EOY_grades-KG-01-02-03-04-05-06_2025-09-08_19-59-22.csv", false);

            // // Melissa
            // // these appare to have correct student id's
            // CsvFiles.LoadComboStudentAssessment(99, "C:/test/importBase/tea/24-25 MAP/Fall 2024 district scores.csv", false);
            // CsvFiles.LoadComboStudentAssessment(99, "C:/test/importBase/tea/24-25 MAP/Spring 25 district scores.csv", false);
            // CsvFiles.LoadComboStudentAssessment(99, "C:/test/importBase/tea/24-25 MAP/Winter 25 district scores.csv", false);

            // CsvFiles.LoadComboStudentAssessment(99, "C:/test/importBase/tea/23-24 MAP/2024_Winter_Map_ComboStudentAssessment (1).csv", false);
            // CsvFiles.LoadComboStudentAssessment(99, "C:/test/importBase/tea/23-24 MAP/Fall23_24MAPComboStudentAssessment.csv", false);
            // CsvFiles.LoadComboStudentAssessment(99, "C:/test/importBase/tea/23-24 MAP/Spring2024_MAP_ComboStudentAssessment.csv", false);


            // // this one has an empty level, but we skip.
            // CsvFiles.LoadComboStudentAssessment(99, "C:/test/importBase/tea/22-23 MAP/MAP_Fall_22_23ComboStudentAssessment.csv", false);
            // CsvFiles.LoadComboStudentAssessment(99, "C:/test/importBase/tea/22-23 MAP/MOY_MAP_22_23_ComboStudentAssessment.csv", false);
            // CsvFiles.LoadComboStudentAssessment(99, "C:/test/importBase/tea/22-23 MAP/Spring_22_23_ComboStudentAssessment.csv", false);

            // CsvFiles.LoadComboStudentAssessment(99, "C:/test/importBase/tea/21-22 MAP/Spring_2022_MAP_ComboStudentAssessment.csv", false);
            // CsvFiles.LoadComboStudentAssessment(99, "C:/test/importBase/tea/21-22 MAP/Winter_21_22_ComboStudentAssessment (1).csv", false);
            // CsvFiles.LoadComboStudentAssessment(99, "C:/test/importBase/tea/21-22 MAP/Winter_21_22_ComboStudentAssessment.csv", false);


        }
         catch (Exception ex) {
            System.out.println("EXCEPTION : " + ex.getMessage());
            System.out.println(ex.getStackTrace());
        };

        
        System.out.println(("-----------------------"));

        // this will simply create teh log and get an import id.

        // the districtId and importId will be set in the repo too.
        

        // TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();


        // String fileName = "ComboStudentAssessment_10.csv";
        // FlatFileItemReader<CelinaCombo> cr = tsfr.celinaComboItemReader("C:/test/importBase/tea/" + fileName);

        // cr.open(new ExecutionContext());

        // System.out.println(("-----------------------"));
        // System.out.println(("------ ComboStudentAssessment_10 ------"));

        // int count = 0;
        // try {

        //     CelinaCombo cc = cr.read();
        //     //String schoolYear = "2024-2025";  // should be able to get this from TermName

        //     //String termName = cc.getTermName();


        //     while (cc != null) {


        //         // we need
        //         // studentId
        //         // schoolYear
        //         // period                   Fall, Spring, Winter
        //         // subject
        //         // proficiency              Quintile 1...
        //         // proficiencyCode          Q1 ...
        //         // score
        //         // csaCode
                

        //         String studentId = cc.getStudentID();
        //         String termName = cc.getTermName();
        //         String subject = cc.getSubject();
        //         String level = cc.getAchievementQuintile();
        //         String score = cc.getPercentCorrect();

        //         String testStartDate = cc.getTestStartDate();

        //         String period = MapPeriod(cc.getTermName());
        //         String proficiency = MapProficiency(cc.getAchievementQuintile());
        //         String proficiencyCode = MapProficiencyCode(cc.getAchievementQuintile());

        //         String schoolYear = TeaStaarFlatFileReader.SchoolYearFromDate(cc.getTestStartDate());

        //         String csaCode = MapCsaCode(subject);

                

        //         i.importRepo.sisMapAdd(studentId, schoolYear, period, subject, proficiency, proficiencyCode, count, csaCode);

        //         System.out.println("Student: " + studentId + "  - Term: " + termName + "  - Subject : " + subject + "  - Level: " + level + "  - Score: " + score);

        //         System.out.println("   --- StartDate: " + testStartDate + " - SchoolYear: " + schoolYear +  "  - Period: " + period + "  - Proficiency : " + proficiency + " (" + proficiencyCode +  ")  - Score: " + score);

        //         count++;

        //         cc = cr.read();
        //     }

        // }
        // catch (Exception ex) {
        //     System.out.println("EXCEPTION : " + ex.getMessage());
        //     System.out.println(ex.getStackTrace());
        // };

        // now read and see.

        // NEXT is Telpas


        


        // String testName="SF_0524_3-8_043908_MELISSA ISD_V01";

        // FlatFileItemReader<Star2024> s24 = tsfr.star2024Reader("C:/test/importBase/tea/SF_0524_3-8_043908_MELISSA ISD_V01.txt");

        //  String testName="SF_1525_EOC_043903_CELINA_ISD_V02";
        //  FlatFileItemReader<Star2024EOC> s24 = tsfr.star2024EOCReader("C:/test/importBase/tea/SF_1525_EOC_043903_CELINA_ISD_V02.txt");



        //  String testName="SF_1525_EOCALT_043903_CELINA_ISD_V01";
        //  FlatFileItemReader<Star2024EOC> s24 = tsfr.star2024EOCReader("C:/test/importBase/tea/SF_1525_EOCALT_043903_CELINA_ISD_V01.txt");




        // System.out.println(" -- Read " + count + "  Students");

        // System.out.println(("-----------------------"));


    

    }


    public static ImportResult Import(String importDefId) {
        System.out.println("TESTING HERE");

        ImportResult result = new ImportResult();

        result.success = true;

        return result;
    }

}
