package com.bosco.stdata.sisDataFiles;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.teaModel.BoscoProficiency;
import com.bosco.stdata.teaModel.Star2024;
import com.bosco.stdata.teaModel.Star2024EOC;
import com.bosco.stdata.teaModel.Telpas2024;
import com.bosco.stdata.utils.MappingHelper;
import com.bosco.stdata.utils.TeaStaarFlatFileReader;

import jakarta.annotation.PostConstruct;

@Component
public class TeaFiles {

    
    @Autowired
    ImportRepo importRepo;

    
    private static TeaFiles i;


    

    @PostConstruct
    public void init() {
        System.out.println("TeaFiles - init()");
        i = this;
    }

  
    // private static String SchoolYearFromAdminDate (String adminDate) throws Exception {
    //     // this should work for ALL TEA files.celinaComboItemReader
    //     String schoolYear = 
    //     switch (adminDate) {
    //         case "0525", "1525", "1625", "0425" -> "2024-2025";
    //         case "0324", "0424", "1524", "0524", "1324", "1624" -> "2023-2024";
    //         case "0323", "0423", "1523", "0523", "1323", "1623" -> "2022-2023";
    //         default -> throw new Exception("Unknown Admin Date in TEA File: " + adminDate);

    //     };
    //     return schoolYear;


    // }



    // Star ONLY
    public static void LoadStar(int districtId, String filePath) throws Exception {




        //For ALT the pli's are

            // 00 = Excluded
            // 0L = Developing Low
            // 0H = Developing High
            // 2M = Satisfactory
            // 3M = Accomplished
        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();



         FlatFileItemReader<Star2024> s24 = tsfr.star2024Reader(filePath);

        s24.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ STAR 2024 ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;

        
        Star2024 t = s24.read();

            // so here we can  calculate School Year if need be.celinaFiles

            


        String schoolYear = MappingHelper.SchoolYearFromAdminDate(t.getAdminDate());


        while (t != null) {

            total++;
            //System.out.println(t);

            //i.importRepo.logTeaStar2024(testName, t);

            if (t.getStudentId().isEmpty()) {
                System.out.println("Blank Student Id found");
            }
            else {

                count++;

                //int grade = Integer.parseInt(t.getGradeLevel());
                String grade = t.getGradeLevel();
                String studentId = t.getStudentId();
                if (t.getScoreReadingLanguageArts().equals("S")) {
                    // now we do for each that we find
                    if (!t.getPliReadingLanguageArts().isEmpty() && !t.getPliReadingLanguageArts().equals("00")) {
                        // we load it
                        BoscoProficiency bp = MappingHelper.StaarProficiency(t.getPliReadingLanguageArts());
                        if (bp == null) {
                            System.out.println ("GOT NULL");
                        }

                        String csaCode = "R";
                        String subject = "Reading";
                        String code = "R";
                        

                        i.importRepo.sisStaarAdd(studentId, schoolYear, subject, code, grade, bp.proficiency, bp.proficiencyCode, csaCode);
                        // So now we can crate the record.

                    }

                        
                }

                if (t.getScoreMath().equals("S")) {
                    // now we do for each that we find
                    if (!t.getPliMath().isEmpty() && !t.getPliMath().equals("00")) {
                        // we load it
                        BoscoProficiency bp = MappingHelper.StaarProficiency(t.getPliMath());
                        if (bp == null) {
                            System.out.println ("GOT NULL");
                        }

                        String csaCode = "M";

                        
                        String subject = "Math";
                        String code = "M";
                        
                        i.importRepo.sisStaarAdd(studentId, schoolYear, subject, code, grade, bp.proficiency, bp.proficiencyCode, csaCode);

                        //i.importRepo.logTea(testName, "StudentID: " + studentId + " - SchoolYear: 2024-2025 - Subject: Math -   Code:???  - Grade: " + grade + " - Proficiency: " + bp.proficiency + "  (" + bp.proficiencyCode + ")  - CsaCode: " + csaCode);

                    }
                }

                if (t.getScoreSocialStudies().equals("S")) {
                    // now we do for each that we find
                    if (!t.getPliSocialStudies().isEmpty() && !t.getPliSocialStudies().equals("00")) {
                        // we load it
                        BoscoProficiency bp = MappingHelper.StaarProficiency(t.getPliSocialStudies());
                        if (bp == null) {
                            System.out.println ("GOT NULL");
                        }

                        String csaCode = "S";

                        String subject = "Social Studies";
                        String code = "SS";
                        
                        i.importRepo.sisStaarAdd(studentId, schoolYear, subject, code, grade, bp.proficiency, bp.proficiencyCode, csaCode);

                        //i.importRepo.logTea(testName, "StudentID: " + studentId + " - SchoolYear: 2024-2025 - Subject: Social Studies -   Code:???  - Grade: " + grade + " - Proficiency: " + bp.proficiency + "  (" + bp.proficiencyCode + ")  - CsaCode: " + csaCode);
                    }
                }

                if (t.getScoreScience().equals("S")) {
                    // now we do for each that we find
                    if (!t.getPliScience().isEmpty() && !t.getPliScience().equals("00")) {
                        // we load it
                        BoscoProficiency bp = MappingHelper.StaarProficiency(t.getPliScience());
                        if (bp == null) {
                            System.out.println ("GOT NULL");
                        }

                        String csaCode = "C";

                        String subject = "Science";
                        String code = "S";
                        
                        // String studentNumber, String schoolYear, String subject, String code, String grade, String proficiency, String proficiencyCode, String csaCode

                        i.importRepo.sisStaarAdd(studentId, schoolYear, subject, code, grade, bp.proficiency, bp.proficiencyCode, csaCode);



                        //i.importRepo.logTea(testName, "StudentID: " + studentId + " - SchoolYear: 2024-2025 - Subject: Science : -   Code:???  - Grade: " + grade + " - Proficiency: " + bp.proficiency + "  (" + bp.proficiencyCode + ")  - CsaCode: " + csaCode);
                    }
                }

              

                

            }

            t = s24.read();
        }
            

        i.importRepo.logTea(filePath, "School Year: " + schoolYear + "  - Total: " + total + "  - Imported : " + count);

        System.out.println(" -- Read " + count + "  Students");

        System.out.println("-----------------------");
    }


    // Star EOC and EOC Alt
    public static void LoadStarEOC(int districtId, String filePath) throws Exception {

        // this works for both EOC and EOC Alt (2024-2025)

        // CONFIRM


        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();


         FlatFileItemReader<Star2024EOC> s24 = tsfr.star2024EOCReader(filePath);

         s24.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ STAR EOC (AND EOC Alt) 2024-2025 ------"));
        System.out.println (filePath);


        int count = 0;
        int total = 0;

       
        Star2024EOC t = s24.read();

        // again, we can get school year if need be
        //String schoolYear = "2024-2025";


        String schoolYear = MappingHelper.SchoolYearFromAdminDate(t.getAdminDate());

        

        while (t != null) {

            total++;
            //System.out.println(t);

            //i.importRepo.logTeaStar2024(testName, t);

            if (t.getStudentId().isEmpty()) {
                System.out.println("Blank Student Id found");
            }
            else {

                

                //int grade = Integer.parseInt(t.getGradeLevel());
                String grade = t.getGradeLevel();
                String studentId = t.getStudentId();

                

                if (t.getScoreCode().equals("S")) {
                    // now we do for each that we find
                    if (!t.getPli().isEmpty() && !t.getPli().equals("00")) {
                        // we load it

                        count++;

                        String log = "";

                        BoscoProficiency bp = MappingHelper.StaarProficiency(t.getPli());
                        if (bp == null) {
                            System.out.println ("GOT NULL");
                        }

                        String csaCode = "ERROR";
                        String subject = "ERROR";
                        String code = "ERROR";
                        String courseCode = t.getCourseCode();


                        switch (courseCode) {
                            case "A1":
                                    csaCode = "M";
                                    //subject = "Algebra I";
                                    subject = "Math";
                                    code = "A1";
                                break;
                            case "E1":
                                    csaCode = "R";
                                    //subject = "English I";
                                    subject = "Reading";
                                    code = "E1";
                                    
                                break;
                            case "E2":
                                    csaCode = "R";
                                    //subject = "English II";                                        
                                    subject = "Reading";
                                    code = "E2";
                                break;
                            case "BI":
                                    csaCode = "C";
                                    //subject = "Biology";
                                    subject = "Science";
                                    code = "B";
                                    
                                break;
                            case "US":
                                    csaCode = "S";
                                    //subject = "U.S. History";
                                    subject = "Social Studies";
                                    code = "USH";
                                    
                                break;
                            default:
                                System.out.println("Found invalid courseCode " + courseCode);
                                throw new Exception("Found invalid courseCode " + courseCode);
                        }

                        // String studentNumber, String schoolYear, String subject, String code, String grade, String proficiency, String proficiencyCode, String csaCode

                        i.importRepo.sisStaarAdd(studentId, schoolYear, subject, code, grade, bp.proficiency, bp.proficiencyCode, csaCode);

                        //log = "EOC - StudentID: " + studentId + " - SchoolYear: 2024-2025 - Subject: " + subject + " -   Code: " + courseCode + " (???)  - Grade: " + grade + " - Proficiency: " + bp.proficiency + "  (" + bp.proficiencyCode + ")  - CsaCode: " + csaCode;

                        

                        // So now we can crate the record.


                        //i.importRepo.logTea(testName, log);
                    }
                }

            
                // do same for math, etc.

            }

            t = s24.read();
        }
        
        System.out.println(" -- Read " + count + "  Students");

        i.importRepo.logTea(filePath, "School Year: " + schoolYear + "  - Total: " + total + "  - Imported : " + count);

        System.out.println(("-----------------------"));

        
    }


    // Telpas Only
    public static void LoadTelpas (int districtId, String filePath) throws Exception {
    
        
        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        FlatFileItemReader<Telpas2024> s24 = tsfr.telpas2024Reader(filePath);

        s24.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ TELPAS 2024 ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;
        
        Telpas2024 t = s24.read();

        String schoolYear = MappingHelper.SchoolYearFromAdminDate(t.getAdminDate());

        


        while (t != null) {

            total++;
            //System.out.println(t);

            //i.importRepo.logTeaStar2024(testName, t);

            if (t.getStudentId().isEmpty()) {
                System.out.println("Blank Student Id found");
            }
            else {

                String grade = t.getGradeLevel();
                String studentId = t.getStudentId();

                String compositeScore = t.getCompositeRating();
                
                // so think no rating is do not load!
                if (!compositeScore.isEmpty() && !compositeScore.equals("0"))
                {

                    String proficiency = MappingHelper.Telpas_proficiency(compositeScore);

                    //Boolean allEmpty = true;

                    int listeningScore = 0;
                    if (!t.getListeningScore().isEmpty()) {
                        listeningScore = Integer.parseInt(t.getListeningScore());
                      //  allEmpty = false;
                    }
                    int speakingScore = 0;
                    if (!t.getSpeakingScore().isEmpty()) {
                        speakingScore = Integer.parseInt(t.getSpeakingScore());
                        // allEmpty = false;
                    }

                    int readingScore = 0;
                    if (!t.getReadingScore().isEmpty()) {
                        readingScore = Integer.parseInt(t.getReadingScore());
                        // allEmpty = false;
                    }

                    int writingScore = 0;
                    if (!t.getWritingScore().isEmpty()){
                        writingScore = Integer.parseInt(t.getWritingScore());
                        // allEmpty = false;
                    }

                    // We alre loading reguadless as of Sept 16
                        // save it
                    count++;

                    i.importRepo.sisTelpasAdd(studentId, schoolYear, grade, proficiency, listeningScore, speakingScore, readingScore, writingScore);

                    // else {
                    //     System.out.println("NONE");
                    // }


                    // so here we don't load if they are all 0.


                    // int listeningScore = Integer.parseInt(t.getListeningScore());
                    // int speakingScore = Integer.parseInt(t.getSpeakingScore());
                    // int readingScore = Integer.parseInt(t.getReadingScore());
                    // int writingScore = Integer.parseInt(t.getWritingScore());

                    // That should be everthing we need!
                    //String log = "TELPAS - StudentID: " + studentId + " - SchoolYear: " + schoolYear + " - Proficiency: " + proficiency + " -   listeningScore: " + listeningScore + " -   speakingScore: " + speakingScore + " -   readingScore: " + readingScore + " -   writingScore: " + writingScore +  "  - Grade: " + grade + "  - CsaCode: BLANK";

                    //i.importRepo.logTea(testName, log);

                }
                else {
                    System.out.println("Empty or 0 compositeScore");
                }



            
                // do same for math, etc.

            }

            t = s24.read();
        }
            

        i.importRepo.logTea(filePath, "School Year: " + schoolYear + "  - Total: " + total + "  - Imported : " + count);
      

        System.out.println(" -- Read " + count + "  Students");

        System.out.println(("-----------------------"));

    }

    // Telpas Alt
    public static void LoadTelpasAlt (int districtId, String filePath) throws Exception {
    
        
        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        FlatFileItemReader<Telpas2024> s24 = tsfr.telpasAltReader(filePath);

        s24.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ TELPAS ALT ------"));
        System.out.println (filePath);

        int count = 0;
        int total = 0;

        
        Telpas2024 t = s24.read();

        String schoolYear = MappingHelper.SchoolYearFromAdminDate(t.getAdminDate());



        while (t != null) {

            total++;
            //System.out.println(t);

            //i.importRepo.logTeaStar2024(testName, t);

            if (t.getStudentId().isEmpty()) {
                System.out.println("Blank Student Id found");
            }
            else {

                String grade = t.getGradeLevel();
                String studentId = t.getStudentId();

                String compositeScore = t.getCompositeRating();
                
                // so think no rating is do not load!
                if (!compositeScore.isEmpty() && !compositeScore.equals("0"))
                {

                    String proficiency = MappingHelper.Telpas_alt_proficiency(compositeScore);

                    //Boolean allEmpty = true;

                    int listeningScore = 0;
                    if (!t.getListeningScore().isEmpty()) {
                        listeningScore = Integer.parseInt(t.getListeningScore());
                        //allEmpty = false;
                    }
                    int speakingScore = 0;
                    if (!t.getSpeakingScore().isEmpty()) {
                        speakingScore = Integer.parseInt(t.getSpeakingScore());
                        //allEmpty = false;
                    }

                    int readingScore = 0;
                    if (!t.getReadingScore().isEmpty()) {
                        readingScore = Integer.parseInt(t.getReadingScore());
                        // allEmpty = false;
                    }

                    int writingScore = 0;
                    if (!t.getWritingScore().isEmpty()){
                        writingScore = Integer.parseInt(t.getWritingScore());
                        // allEmpty = false;
                    }

                    // save it
                    count++;
                    i.importRepo.sisTelpasAdd(studentId, schoolYear, grade, proficiency, listeningScore, speakingScore, readingScore, writingScore);



                    // so here we don't load if they are all 0.


                    // int listeningScore = Integer.parseInt(t.getListeningScore());
                    // int speakingScore = Integer.parseInt(t.getSpeakingScore());
                    // int readingScore = Integer.parseInt(t.getReadingScore());
                    // int writingScore = Integer.parseInt(t.getWritingScore());

                    // That should be everthing we need!
                    //String log = "TELPAS - StudentID: " + studentId + " - SchoolYear: " + schoolYear + " - Proficiency: " + proficiency + " -   listeningScore: " + listeningScore + " -   speakingScore: " + speakingScore + " -   readingScore: " + readingScore + " -   writingScore: " + writingScore +  "  - Grade: " + grade + "  - CsaCode: BLANK";

                    //i.importRepo.logTea(testName, log);

                }
                else {
                    System.out.println("Empty or 0 compositeScore");
                }



            
                // do same for math, etc.

            }

            t = s24.read();
        }
            

            
        i.importRepo.logTea(filePath, "School Year: " + schoolYear + "  - Total: " + total + "  - Imported : " + count);

        System.out.println(" -- Read " + count + "  Students");

        System.out.println(("-----------------------"));

    }
}


 

