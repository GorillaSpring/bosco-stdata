package com.bosco.stdata.distictImports;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.model.ImportDefinition;
import com.bosco.stdata.model.ImportResult;
import com.bosco.stdata.model.ImportSetting;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.sisDataFiles.CsvFiles;
import com.bosco.stdata.sisDataFiles.TeaFiles;
import com.bosco.stdata.utils.ImportHelper;

import jakarta.annotation.PostConstruct;

@Component
public class MelissaSisFiles {

    private final AppConfig appConfig;
    @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;

    private static MelissaSisFiles i;

    MelissaSisFiles(AppConfig appConfig) {
        this.appConfig = appConfig;
    }  // instance

    @PostConstruct
    public void init() {
        System.out.println("MelissaSisFiles - init()");
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


            
            //String baseFileFolder = "C:/test/uplift/" + subFolder + "/";
            String baseFileFolder = ImportHelper.ValueForSetting(importSettings, "baseFolder");

            String archiveFolder =  ImportHelper.ValueForSetting(importSettings, "archiveFolder");

            int importId = i.importRepo.prepImport(districtId, importDefId, isRoster,isSisData,  "Melissa Sis files " + baseFileFolder);

             LocalDateTime startDateTime = LocalDateTime.now();

            result.importId = importId;
            result.districtId = districtId;


          
            // LETS load all this into dev / test / prod

            // dev : NOW

            // THEN we can work on the gardes

            // Do this to laod the map files when done.

            //System.out.println("Loading map file");

             //CsvFiles.LoadMapCourseNameCsaCode(baseFileFolder + "Melissa_map_courseName_csaCode.csv");

            


            // THESE SHOULD BE GOOD TO GO AFTER WE LOAD THE MAP FILE.

            // System.out.println("grades_current_year");
            // CsvFiles.LoadGradesMelissa(districtId, baseFileFolder + "grades_current_year.csv");

            // System.out.println("grades_historical");


            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "NEW/BOY fall 2025 data.csv", false);


            CsvFiles.LoadDibels8(districtId, baseFileFolder + "NEW/dibels8_BM_2025-2026_BOY_grades-KG-01-02-03-04-05-06_2025-10-10_08-18-57.csv", false);


            CsvFiles.LoadGradesMelissa(districtId, baseFileFolder + "grades_historical.csv");

            


            // 2022

            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "2022/SF_0523_3-8_043908_MELISSA ISD_V01.txt");
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "2022/SF_0423_3-8ALT_043908_MELISSA ISD_V01.txt");
            TeaFiles.LoadTelpas(districtId, baseFileFolder + "2022/SP_0323_TELPAS_043908_MELISSA ISD_V01.txt");
            TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "2022/SF_0323_TELPASALT_043908_MELISSA ISD_V01.txt");

            
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "2022/SF_1323_EOC_043908_MELISSA ISD_V01.txt");

            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "2022/SF_1523_EOC_043908_MELISSA ISD_V01_August5 file.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "2022/SF_1523_EOCALT_043908_MELISSA ISD_V01.txt");

            // //  ** Loaded 0 students!
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "2022/SP_1623_EOC_043908_MELISSA ISD_V01.txt");



            




            // 2023
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "2023/SF_0424_3-8ALT_043908_MELISSA ISD_V01.txt");
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "2023/SF_0524_3-8_043908_MELISSA ISD_V01.txt");



            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "2023/SF_1324_EOC_043908_MELISSA ISD_V02.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "2023/SF_1524_EOC_043908_MELISSA ISD_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "2023/SF_1524_EOCALT_043908_MELISSA ISD_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "2023/SF_1624_EOC_043908_MELISSA ISD_V01.txt");

            TeaFiles.LoadTelpas(districtId, baseFileFolder + "2023/SF_0324_TELPAS_043908_MELISSA ISD_V01.txt");

            TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "2023/SF_0324_TELPASALT_043908_MELISSA ISD_V01.txt");


            // 2024
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "2024/SF_0525_3-8_043908_MELISSA ISD_V03.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "2024/SF_1525_EOC_043908_MELISSA ISD_V02.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "2024/SP_1625_EOC_043908_MELISSA ISD_V01.txt");


            //This mClass files.
            //2022_2023_2024_2025 mClass
            CsvFiles.LoadDibels8(districtId, baseFileFolder + "2022_2023_2024_2025 mClass/dibels8_BM_2021-2022_BOY_MOY_EOY_grades-KG-01-02-03-04-05-06_2025-09-08_19-56-59.csv", false);
            CsvFiles.LoadDibels8(districtId, baseFileFolder + "2022_2023_2024_2025 mClass/dibels8_BM_2022-2023_BOY_MOY_EOY_grades-KG-01-02-03-04-05-06_2025-09-08_19-57-46.csv", false);
            CsvFiles.LoadDibels8(districtId, baseFileFolder + "2022_2023_2024_2025 mClass/dibels8_BM_2023-2024_BOY_MOY_EOY_grades-KG-01-02-03-04-05-06_2025-09-08_19-58-45.csv", false);
            CsvFiles.LoadDibels8(districtId, baseFileFolder + "2022_2023_2024_2025 mClass/dibels8_BM_2024-2025_BOY_MOY_EOY_grades-KG-01-02-03-04-05-06_2025-09-08_19-59-22.csv", false);

            

            // 21-22 MAP
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "21-22 MAP/Spring_2022_MAP_ComboStudentAssessment.csv", false);
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "21-22 MAP/Winter_21_22_ComboStudentAssessment (1).csv", false);
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "21-22 MAP/Winter_21_22_ComboStudentAssessment.csv", false);



            // 22-23 MAP
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "22-23 MAP/MAP_Fall_22_23ComboStudentAssessment.csv", false);
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "22-23 MAP/MOY_MAP_22_23_ComboStudentAssessment.csv", false);
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "22-23 MAP/Spring_22_23_ComboStudentAssessment.csv", false);


            // 23-24 MAP
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "23-24 MAP/2024_Winter_Map_ComboStudentAssessment (1).csv", false);
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "23-24 MAP/Fall23_24MAPComboStudentAssessment.csv", false);
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "23-24 MAP/Spring2024_MAP_ComboStudentAssessment.csv", false);

        // 24-25 Map
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "24-25 MAP/Fall 2024 district scores.csv", false);
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "24-25 MAP/Spring 25 district scores.csv", false);
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "24-25 MAP/Winter 25 district scores.csv", false);







            
            // so we don't send anyting to bosco for sis
            // however, we do need to cal it.


              // this is NOT necessary for SIS only.
            i.importRepo.prepSendBosco(districtId, importDefId, isRoster, isSisData);

            // This will mark students that need to have sis data sent.
            i.importRepo.postSendBosco(districtId, importDefId, isRoster, isSisData);


              LocalDateTime endDateTime = LocalDateTime.now();
    
            Duration duration = Duration.between(startDateTime, endDateTime);
            
            System.out.println ("Import Complete in : " + duration.toSeconds() + " Seconds" );

            i.importRepo.logInfo("Import " + importDefId + "  Complete in : " + duration.toSeconds() + " Seconds" );




            result.success = true;
            System.out.println("DONE MelissaSisFiles.Import");



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
