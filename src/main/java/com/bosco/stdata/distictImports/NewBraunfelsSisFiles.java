package com.bosco.stdata.distictImports;

import java.time.Year;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.model.ImportDefinition;
import com.bosco.stdata.model.ImportResult;
import com.bosco.stdata.model.ImportSetting;
import com.bosco.stdata.model.Student;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.sisDataFiles.CsvFiles;
import com.bosco.stdata.sisDataFiles.TeaFiles;
import com.bosco.stdata.utils.ImportHelper;

import jakarta.annotation.PostConstruct;

@Component

public class NewBraunfelsSisFiles {

    private final AppConfig appConfig;
   @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;

    private static NewBraunfelsSisFiles i;

    NewBraunfelsSisFiles(AppConfig appConfig) {
        this.appConfig = appConfig;
    }  // instance

    @PostConstruct
    public void init() {
        System.out.println("NewBraunfelsSisFiles - init()");
        i = this;
    }


    public static ImportResult Import(String importDefId) {

        Boolean isRoster = false;
        Boolean isSisData = true;
 
        ImportResult result = new ImportResult();


        try {
            ImportDefinition importDef = i.importRepo.getImportDefinition(importDefId);

            

            List<ImportSetting> importSettings = i.importRepo.getImportSettings(importDefId);

            int districtId = importDef.getDistrictId();
            String baseFileFolder = ImportHelper.ValueForSetting(importSettings, "baseFolder");

            String archiveFolder =  ImportHelper.ValueForSetting(importSettings, "archiveFolder");


            int importId = i.importRepo.prepImport(districtId, importDefId, isRoster, isSisData,  "Celina Sis files " + baseFileFolder);
            
            // WE don't prep an import!
            //int importId = i.importRepo.prepImport(districtId, "Import for " + importDefId);

            result.importId = importId;
            result.districtId = districtId;

            Boolean isVald;

            // This is 0 roes.


            // Now format 2
         
   // mClass_BOY_24-25.csv             2
    // mClass_BOY_Spanish_24-25.csv     2
    // mclass_EOY_24-25.csv             2
    // mclass_EOY_Span_24-25.csv        2
    // mClass_MOY_24-25.csv             2
    // mClass_MOY_Spanish_24-25.csv     2


            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "2026/mClass_25_26_BOY_English.csv", 5, -1, 10, 6, 7, 11 );
            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "2026/mClass_25_26_BOY_Spanish.csv", 5, -1, 10, 6,  7,11 );
            //System.out.println("VALD : " + isVald);


            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_BOY_24-25.csv", 45, 0, -1, 46, 47 , 98);
            
            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_BOY_Spanish_24-25.csv", 45, 0, -1, 46, 47 , 98);
            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mclass_EOY_24-25.csv", 45, 0, -1, 46, 47 , 98);
            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mclass_EOY_Span_24-25.csv", 45, 0, -1, 46, 47 , 98);
            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_MOY_24-25.csv", 45, 0, -1, 46, 47 , 98);
            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_MOY_Spanish_24-25.csv", 45, 0, -1, 46, 47 , 98);
            
            // These have DOB. So 1 extra.
            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_23_24_BOY_English.csv", 6, -1, 11, 7,  8,12);
            
            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_23_24_BOY_Spanish.csv", 6, -1, 11, 7, 8,12);
            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_23_24_EOY_English.csv", 6, -1, 11, 7, 8,12);
            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_23_24_EOY_Spanish.csv", 6, -1, 11, 7, 8,12);
            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_23_24_MOY_English.csv", 6, -1, 11, 7, 8,12);
            isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_23_24_MOY_Spanish.csv", 6, -1, 11, 7, 8,12);


            // LOADED IN DEV, TEST, PROD
             //CsvFiles.LoadMapCourseNameCsaCode(baseFileFolder + "NEW_nbisd_course_csaCode - UpdatedML.csv");


             // Lets work on the grade file now

             // DONE IN DEV, TEST
    // grades_S1_2023-2024.csv
    // grades_S1_2024-2025.csv
    // grades_S2_2023-2024.csv
    // grades_S2_2024-2025.csv

            CsvFiles.LoadGradesNbIsd(districtId, baseFileFolder + "Past2/grades_S1_2023-2024.csv");
            CsvFiles.LoadGradesNbIsd(districtId, baseFileFolder + "Past2/grades_S1_2024-2025.csv");
            CsvFiles.LoadGradesNbIsd(districtId, baseFileFolder + "Past2/grades_S2_2023-2024.csv");
            CsvFiles.LoadGradesNbIsd(districtId, baseFileFolder + "Past2/grades_S2_2024-2025.csv");

    
            
// FORMAT 1:
//  Campus ID,Student Last Name,Student First Name,Test Grade,Student DOB,Test Staff ID,Benchmark Period,Composite Level,Composite Score (8),Semester Growth,Year Growth,Date Tested (11),Student Local ID (12),Student Grade
//  Campus ID,Student Last Name,Student First Name,Test Grade,Student DOB,Test Staff ID,Benchmark Period,Composite Level,Composite Score    ,Semester Growth,Year Growth,Date Tested      ,Student Local ID,Student Grade
//  Campus ID,Student Last Name,Student First Name,Test Grade,Student DOB,Test Staff ID,Benchmark Period,Composite Level,Composite Score,Semester Growth,Year Growth,Date Tested,Student Local ID,Student Grade
//  Campus ID,Student Last Name,Student First Name,Test Grade,            Test Staff,   Benchmark Period,Composite Level,Composite Score,Semester Growth,Year Growth,Date Tested,Student Local ID,Student Grade



// Format 2:
//  School Year,State,Account Name,Municipality Name,Municipality Primary ID,District Name,District Primary ID,Internal Program,External Program,School Name,Primary School ID,Secondary School ID,Student Last Name,Student First Name,Student Middle Name,Enrollment Date,Enrollment Grade,Date of Birth,Gender,Race,Hispanic or Latino Ethnicity,Special Education,Disability,Specific Disability,Section 504,IEP Status,Economically Disadvantaged,Meal Status,Title 1,Migrant,English Proficiency,ELL Status,Home Language,Alternate Assessment,Approved Accommodations,Classed,Reporting Class Name,Reporting Class ID,Official Teacher Name,Official Teacher Staff ID,Assessing Teacher Name,Assessing Teacher Staff ID,Assessment,Assessment Edition,Assessment Grade,Benchmark Period,Composite Level,Composite Score,Composite - National Norm Percentile,Composite - Semester Growth,Composite - Year Growth,Letter Names (LNF) - Level,Letter Names (LNF) - Score,Letter Names (LNF) - National Norm Percentile,Letter Names (LNF) - Semester Growth,Letter Names (LNF) - Year Growth,Phonemic Awareness (PSF) - Level,Phonemic Awareness (PSF) - Score,Phonemic Awareness (PSF) - National Norm Percentile,Phonemic Awareness (PSF) - Semester Growth,Phonemic Awareness (PSF) - Year Growth,Letter Sounds (NWF-CLS) - Level,Letter Sounds (NWF-CLS) - Score,Letter Sounds (NWF-CLS) - National Norm Percentile,Letter Sounds (NWF-CLS) - Semester Growth,Letter Sounds (NWF-CLS) - Year Growth,Decoding (NWF-WRC) - Level,Decoding (NWF-WRC) - Score,Decoding (NWF-WRC) - National Norm Percentile,Decoding (NWF-WRC) - Semester Growth,Decoding (NWF-WRC) - Year Growth,Word Reading (WRF) - Level,Word Reading (WRF) - Score,Word Reading (WRF) - National Norm Percentile,Word Reading (WRF) - Semester Growth,Word Reading (WRF) - Year Growth,Reading Accuracy (ORF-Accu) - Level,Reading Accuracy (ORF-Accu) - Score,Reading Accuracy (ORF-Accu) - National Norm Percentile,Reading Accuracy (ORF-Accu) - Semester Growth,Reading Accuracy (ORF-Accu) - Year Growth,Reading Fluency (ORF) - Level,Reading Fluency (ORF) - Score,Reading Fluency (ORF) - National Norm Percentile,Reading Fluency (ORF) - Semester Growth,Reading Fluency (ORF) - Year Growth,Error Rate (ORF) - Score,Reading Comprehension (Maze) - Level,Reading Comprehension (Maze) - Score,Reading Comprehension (Maze) - National Norm Percentile,Reading Comprehension (Maze) - Semester Growth,Reading Comprehension (Maze) - Year Growth,Correct Responses (Maze) - Score,Incorrect Responses (Maze) - Score,Client Date,Sync Date,Student Primary ID,Primary ID - Primary Student ID (State ID),Student ID (District ID or School ID),Administration Type,Vocabulary - Level,Vocabulary - Score,Spelling - Level,Spelling - Score,RAN - Level,RAN - Score,Risk Indicator - Level,DIBELS Composite Score - Lexile
    
            // THESE NEED A NEW READER!
    // mClass_23_24_BOY_English.csv     1
    // mClass_23_24_BOY_Spanish.csv     1         
    // mClass_23_24_EOY_English.csv     1
    // mClass_23_24_EOY_Spanish.csv     1
    // mClass_23_24_MOY_English.csv     1
    // mClass_23_24_MOY_Spanish.csv     1

    // mClass_BOY_24-25.csv             2
    // mClass_BOY_Spanish_24-25.csv     2
    // mclass_EOY_24-25.csv             2
    // mclass_EOY_Span_24-25.csv        2
    // mClass_MOY_24-25.csv             2
    // mClass_MOY_Spanish_24-25.csv     2


    // in 2026
    //  mClass_25_26_BOY_English        1
    //  mClass_25_26_BOY_Spanish        1

           




             CsvFiles.LoadGradesNbIsd(districtId, baseFileFolder + "2026/grades_current_2026.csv");



            
            TeaFiles.LoadTelpas(districtId, baseFileFolder + "Past/SF_0323_TELPAS_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadTelpas(districtId, baseFileFolder + "Past/SF_0324_TELPAS_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadTelpas(districtId, baseFileFolder + "Past/SF_0325_TELPAS_046901_NEW BRAUNFELS I_V01.txt");

            TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "Past/SF_0323_TELPASALT_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "Past/SF_0325_TELPASALT_046901_NEW BRAUNFELS I_V01.txt");


            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0423_3-8ALT_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0424_3-8ALT_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0425_3-8ALT_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0523_3-8_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0524_3-8_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0525_3-8_046901_NEW BRAUNFELS I_V03.txt");
            
            
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1323_EOC_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1324_EOC_046901_NEW BRAUNFELS I_V02.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1523_EOCALT_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1523_EOC_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1524_EOCALT_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1524_EOC_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1525_EOCALT_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1525_EOC_046901_NEW BRAUNFELS I_V02.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1623_EOC_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1624_EOC_046901_NEW BRAUNFELS I_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1625_EOC_046901_NEW BRAUNFELS I_V01.txt");
           



            result.success = true;
            System.out.println("DONE NewBraunfelsSisFiles.Import");


            // this is NOT necessary for SIS only.
            i.importRepo.prepSendBosco(districtId, importDefId, isRoster, isSisData);

            // This will mark students that need to have sis data sent.
            i.importRepo.postSendBosco(districtId, importDefId, isRoster, isSisData);

            


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
