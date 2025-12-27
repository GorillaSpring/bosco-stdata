package com.bosco.stdata.utils;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.bosco.stdata.teaModel.BoscoProficiency;

public class MappingHelper {

    public static String GuardianTypeFromTypeId (String typeId) {
        if (typeId.equals("01"))
            return "F";
        if (typeId.equals("02"))
            return "M";
        return "O";
    }

    public static String GuardianTypeFromString (String type) {
        if (type.startsWith("M"))
            return "M";
        if (type.startsWith("F"))
            return "F";
        return "O";
    }

    
    public static String GuardianTypeFromStringAllen (String type) {
        String gType = 
        switch (type) {
            case "Father"-> "F";
            case "Mother" -> "M";
            default -> "O";

        };
        return gType;
        }

    // SchoolYearFromTeaAdminDate  or Tea_SchoolYearFromAdminDate

    // LoadStar, LoadStarEOC, LoadTelpas, LoadTelpasAlt
    public static String SchoolYearFromAdminDate (String adminDate) throws Exception {
        // this should work for ALL TEA files.celinaComboItemReader
        String schoolYear = 
        switch (adminDate) {
            case "0325", "0525", "1525", "1625", "0425" -> "2024-2025";
            case "0324", "0424", "1524", "0524", "1324", "1624" -> "2023-2024";
            case "0323", "0423", "1523", "0523", "1323", "1623" -> "2022-2023";
            case "1322" -> "2021-2022";
            default -> throw new Exception("Unknown Admin Date in TEA File: " + adminDate);

        };
        return schoolYear;


    }

    public static String SchoolYearFromYear (String year) throws Exception {
        // this will make "2025" -> "2024-2025"

        int intYear = Integer.parseInt(year);

        return (intYear - 1) + "-" + year;


    }

    public static String PeroidFromSixWeeks (String swString) {
        // 1SW => Six Weeks 1

        if (swString.isBlank()) 
            return ""; 

        char firstChar = swString.charAt(0);

        if (Character.isDigit(firstChar)) {
            return "Six Week " + firstChar;
        }
        return swString;

    }


    // LoadStar, LoadStarEOC
    public static BoscoProficiency StaarProficiency (String teaPL) throws Exception {

        BoscoProficiency boscoProficiency = 
        switch (teaPL) {
            case "0L", "0H" -> new BoscoProficiency("Did Not Meet Grade Level", "DN");
            case "1L", "1H" -> new BoscoProficiency("Approaches Grade Level", "AP");
            case "2M" -> new BoscoProficiency("Meets Grade Level", "MT");
            case "3M" -> new BoscoProficiency("Masters Grade Level", "MA");
            default -> throw new Exception("Unknown boscoProficiency");
                
        };

        return boscoProficiency;

    }

    // LoadTelpas
    public static String Telpas_proficiency (String compositeScore) throws Exception {
        String pro = 
        switch(compositeScore) {
            case "1" -> "Beginning";
            case "2" -> "Intermediate";
            case "3" -> "Advanced";
            case "4" -> "Advanced High";
            default -> throw new Exception("Unknown Telpas_proficiency");

        };

        return pro;
        
    }


    // LoadTelpasAlt
    public static String Telpas_alt_proficiency (String compositeScore) throws Exception {
        String pro = 
        switch(compositeScore) {
            case "1" -> "Awareness";
            case "2" -> "Imitation";
            case "3" -> "Early Independence";
            case "4" -> "Developing Independence";
            case "5" -> "Basic Fluency";
            default -> throw new Exception("Unknown Telpas_alt_proficiency");

        };

        return pro;
        
    }


    // LoadComboStudentAssessment  CsvFiles

    public static String MapProficiency (String achievementQuintile) throws Exception {
        String pro = 
        switch(achievementQuintile) {
            case "Low", "1. Low" -> "Quintile 1";
            case "LoAvg", "LowAvg", "2. LowAvg" -> "Quintile 2";
            case "Avg", "3. Avg" -> "Quintile 3";
            case "HiAvg", "4. HiAvg" -> "Quintile 4";            
            case "High", "5. High" -> "Quintile 5";
            default -> throw new Exception("Unknown MapProficiency : " + achievementQuintile);

        };

        return pro;
        
    }

    // LoadComboStudentAssessment
    public static String MapSubject (String course) throws Exception {
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

    // LoadComboStudentAssessment
    public static String MapProficiencyCode (String achievementQuintile) throws Exception {
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


    // LoadComboStudentAssessment
    public static String MapCsaCode (String course) throws Exception {
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

    // LoadComboStudentAssessment
    public static String MapPeriod (String termName) {
        // Spring 2024-2025  => Spring.

        return termName.split(" ")[0];
    }



    // TeaStaarFlatFileReader.

    // (Uplift - state assessment, Uplift - telpas,  CsvFiles -- LoadComboStudentAssessment

    public static String SchoolYearFromDateMDY (String dateString) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-dd-yyyy");
            LocalDate date = LocalDate.parse(dateString, formatter);
            Month month = date.getMonth();
            if (month == Month.AUGUST || month == Month.SEPTEMBER || month == Month.OCTOBER || month == Month.NOVEMBER || month == Month.DECEMBER)
                return date.getYear() + "-" + (date.getYear() + 1);
            else
                return date.getYear() - 1 + "-" + date.getYear();

    }

     public static String SchoolYearFromDateYMD (String dateString) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateString, formatter);
            Month month = date.getMonth();
            if (month == Month.AUGUST || month == Month.SEPTEMBER || month == Month.OCTOBER || month == Month.NOVEMBER || month == Month.DECEMBER)
                return date.getYear() + "-" + (date.getYear() + 1);
            else
                return date.getYear() - 1 + "-" + date.getYear();

    }
         
    public static String SchoolYearFromDate (String dateString) throws Exception {
        
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
         try {
            LocalDate date = LocalDate.parse(dateString, formatter);
            Month month = date.getMonth();
            if (month == Month.AUGUST || month == Month.SEPTEMBER || month == Month.OCTOBER || month == Month.NOVEMBER || month == Month.DECEMBER)
                return date.getYear() + "-" + (date.getYear() + 1);
            else
                return date.getYear() - 1 + "-" + date.getYear();
        } catch (DateTimeParseException e) {
            // lets try different format.
            formatter = DateTimeFormatter.ofPattern("M/d/yy");
            LocalDate date = LocalDate.parse(dateString, formatter);
            Month month = date.getMonth();
            if (month == Month.AUGUST || month == Month.SEPTEMBER || month == Month.OCTOBER || month == Month.NOVEMBER || month == Month.DECEMBER)
                return date.getYear() + "-" + (date.getYear() + 1);
            else
                return date.getYear() - 1 + "-" + date.getYear();


         }
    }

    // CsvFiles - LoadDibels8
    public static String Dibels8_period (String termName) throws Exception {
        String period = switch (termName) {
            case "BOY", "Fall" -> "Fall";
            case "MOY", "Winter" -> "Winter";
            case "EOY", "Spring" -> "Spring";
            default -> throw new Exception("Unknown Dibels8_period : " + termName);
             
        };

        return period;
    }


    // Uplift mClass & map, CsvFiles - LoadDibels8
    public static String MClass_proficiencyCode (String proficiency) throws Exception {
        
         String proficiencyCode = switch (proficiency) {
            case "Above Benchmark" -> "AB";
            case "At Benchmark" -> "AT";
            case "Below Benchmark" -> "BB";
            case "Well Below Benchmark" -> "WB";
            case "Discontinue", "Tested out" -> "";   // This is DO NOT LOAD!
            default -> throw new Exception("Unknown MClass ProficiencyCode: " + proficiency);
        };

        return proficiencyCode;

    }


    // Uplift Files

    public static String Map_proficiencyCode (String proficiency) throws Exception {
        // this is simply Quintile 2  => Q2
         String proficiencyCode = switch (proficiency) {
            case "Quintile 1" -> "Q1";
            case "Quintile 2" -> "Q2";
            case "Quintile 3" -> "Q3";
            case "Quintile 4" -> "Q4";
            case "Quintile 5" -> "Q5";
            default -> throw new Exception("Unknown Map_proficiencyCode: " + proficiency);
        };

        return proficiencyCode;

    }

  
     

    public static String GeneralCsaCode (String subject) throws Exception {
        String lowerSubject = subject.toLowerCase();
        if (lowerSubject.startsWith("math"))
            return "M";
        if (lowerSubject.startsWith("algebra"))
            return "M";
        if (lowerSubject.startsWith("read"))
            return "R";
        if (lowerSubject.startsWith("written"))
            return "R";
        if (lowerSubject.startsWith("eng"))
            return "R";
        if (lowerSubject.startsWith("lang"))
            return "L";
        if (lowerSubject.startsWith("math"))
            return "M";
        if (lowerSubject.startsWith("science"))
            return "C";
        if (lowerSubject.startsWith("social"))
            return "S";
        if (lowerSubject.startsWith("adaptive"))
            return "A";
        if (lowerSubject.startsWith("behavior"))
            return "B";
        if (lowerSubject.startsWith("cognitive"))
            return "G";
        
        throw new Exception("Unknown GeneralCsaCode : " + subject);
    }

    // public static String MapMclass_CsaCodeFromCourseName (String courseName) throws Exception {
    //     // This will return "" for N/A - IE ignore
    //     // It will return "NOT_FOUND"  if not found!

    //      String csaCode = switch (courseName) {
    //         case "English II MYP" -> "R";
    //         case "Algebra II MYP" -> "M";
    //         case "Algebra II" -> "M";
    //         case "Algebra I" -> "M";
    //         case "Chemistry MYP" -> "C";
    //         case "US Government" -> "S";
    //         case "US Hist Since Recon MYP (YL)" -> "S";
    //         case "IB Lang A: Lang & Lit SL2" -> "R";
    //         case "IB Math: AI SL2" -> "M";
    //         case "IB Theory of Knowledge 12 (Sem)" -> "";   // This is a known DO NOT LOAD.
    //         case "IB Theory of Knowledge 11 (Sem)" -> "";   // This is a known DO NOT LOAD.
    //         case "IB Global Politics HL2" -> "S";
    //         case "IB History of Americas HL2" -> "S";
    //         case "IB History of Americas HL1" -> "S";
    //         case "IB Lang A: Lang & Lit HL2" -> "R";
    //         case "IB Lang A: Lang & Lit SL1" -> "R";
    //         case "IB Environ Sys & Soc SL1" -> "S";
    //         case "IB Global Politics Higher Level 1" -> "S";
    //         case "IB Math: AI SL1" -> "M";
    //         case "IB Biology Standard Level 1" -> "C";
    //         case "IB Psychology HL1" -> "S";
    //         case "Geometry MYP" -> "M";
    //         case "IB Lang A: Lang & Lit HL1" -> "R";
    //         case "Precalculus" -> "M";
    //         case "English I MYP" -> "R";
    //         case "Biology MYP" -> "C";
    //         case "Biology" -> "C";
    //         case "World History Studies MYP" -> "S";
    //         case "Ind Study in Math I (YL)" -> "M";
    //         case "English Lang Arts & Read, Grade 8 MYP" -> "R";
    //         case "Algebra I MYP MS" -> "M";
    //         case "Science, Grade 8 MYP" -> "C";
    //         case "US History, Grade 8 MYP" -> "S";
    //         case "English Lang Arts & Read, Grade 6 MYP" -> "R";
    //         case "Math, Grade 6 MYP" -> "M";
    //         case "Science, Grade 6 MYP" -> "C";
    //         case "Algebra I MYP" -> "M";
    //         case "Reading I" -> "R";
    //         case "English Lang Arts & Read, Grade 7 MYP" -> "R";
    //         case "English I", "English II" -> "R";
    //         case "Math, Grade 8 MYP" -> "M";
    //         case "Science, Grade 7 MYP" -> "C";
    //         case "Math, Grade 7 MYP" -> "M";
    //         case "Texas History, Grade 7 MYP" -> "S";
    //         case "English Language Arts & Reading, Grade 3" -> "R";
    //         case "Social Studies, Grade 3" -> "S";
    //         case "Math, Grade 7" -> "M";
    //         case "English Language Arts & Reading, Grade 5" -> "R";
    //         case "Science, Grade 5" -> "C";
    //         case "Social Studies, Grade 5" -> "S";
    //         case "World History, Grade 6 MYP" -> "S";
    //         case "English Language Arts & Reading, Grade 4" -> "R";
    //         case "Science, Grade 4" -> "C";
    //         case "Social Studies, Grade 4" -> "S";
    //         case "Math, Grade 4" -> "M";
    //         case "Math, Grade 3" -> "M";
    //         case "Math, Grade 5", "Math, Grade 8" -> "M";

    //         case "Geometry" -> "M";

    //         case "English III" -> "R";
    //         case "Environmental Systems" -> "C";
    //         case "World Geo Studies" -> "S";
    //         case "English IV" -> "R";
    //         case "Anatomy and Physiology" -> "C";
    //         case "World Geo Studies MYP" -> "S";
    //         case "English Language Arts & Reading, Grade 2" -> "R";
    //         case "Math, Grade 2" -> "M";
    //         case "Science, Grade 2" -> "C";
    //         case "Social Studies, Grade 2" -> "S";
    //         case "English Language Arts & Reading, KG" -> "R";
    //         case "Math, Kindergarten" -> "M";
    //         case "Science, Kindergarten" -> "C";
    //         case "Social Studies, Kindergarten" -> "S";
    //         case "English Language Arts & Reading, Grade 1" -> "R";
    //         case "Math, Grade 1" -> "M";
    //         case "Science, Grade 1" -> "C";
    //         case "Social Studies, Grade 1" -> "S";

    //         case "Physics" -> "C";
    //         case "Science, Grade 3" -> "C";

    //         case "Chemistry" -> "C";
    //         case "US Hist Since Recon" -> "S";
    //         case "World Hist Studies" -> "S";
    //         case "Geometry MYP MS" -> "M";

    //         case "Personal Financial Lit & Econ (Sem)" -> "";



    //          default -> throw new Exception("Unknown MapMclass_CsaCodeFromCourseName: " + courseName);
    //     };

    //     return csaCode;


    // }

   

    public static String Staar_SubjectFromCode (String code) throws Exception {

        String subject = switch (code) {
            case "M" -> "Math";
            case "R" -> "Reading";
            case "S" -> "Science";
            case "A1" -> "Algebra I";
            case "B" -> "Biology";
            case "E1" -> "English I";
            case "E2" -> "English II";
            case "USH" -> "US History";
            case "SS" -> "Social Studies";
            default -> throw new Exception("Unknown Staar_SubjectFromCode : " + code);
        };

        return subject;


    }

    public static String Staar_CsaCodeFromCode (String code) throws Exception {

        String csaCode = switch (code) {
            case "M", "A1", "Mathematics" -> "M";
            case "R" -> "R";
            case "S", "B", "Science" -> "C";
            case "E1", "E2", "Language Arts" -> "L";
            case "USH", "SS", "Social Studies" -> "S";
            default -> throw new Exception("Unknown Staar_CsaCodeFromCode : " + code);
        };

        return csaCode;
    }

    public static String Staar_ProficiencyCodeFromProficiency (String proficiency) throws Exception {

        String pc = switch (proficiency) {
            case "Did Not Meet" , "NM" -> "DN";
            case "Approaches", "A" -> "AP";
            case "Masters", "M" -> "MA";
            case "Meets", "ME" -> "MT";
            default -> throw new Exception("Unknown Staar ProficiencyCode : " + proficiency);
        };

        return pc;


    }
    
    

}
