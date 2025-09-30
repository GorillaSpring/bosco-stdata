package com.bosco.stdata.distictImports;

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
public class CelinaSisFiles {

    private final AppConfig appConfig;
   @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;

    private static CelinaSisFiles i;

    CelinaSisFiles(AppConfig appConfig) {
        this.appConfig = appConfig;
    }  // instance

    @PostConstruct
    public void init() {
        System.out.println("CelinaSisFiles - init()");
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
            
            //String baseFileFolder = "C:/test/uplift/" + subFolder + "/";


            // Seot 27th loads.

            // 2022-2023 Assessment Files\2022-23 MAP
            /*
             
-a----        2025-09-27  10:31 AM        6910318 MAP Data file Fall 2223.csv
-a----        2025-09-27  10:31 AM        5386318 MAP Data file Spring 2223.csv
-a----        2025-09-27  10:31 AM        6937416 MAP Data file Winter 2223.csv


             */

             CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "2022-2023 Assessment Files/2022-23 MAP/MAP Data file Fall 2223.csv", true);
             CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "2022-2023 Assessment Files/2022-23 MAP/MAP Data file Spring 2223.csv", true);
             CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "2022-2023 Assessment Files/2022-23 MAP/MAP Data file Winter 2223.csv", true);



             // 2022-2023 Assessment Files/2022-23 STAAR

             /*
              * 

-a----        2025-09-27  10:31 AM        7823910 SF_0523_3_8_043903_CELINA_ISD_V01.txt
-a----        2025-09-27  10:31 AM          18018 SF_1523_EOCALT_043903_CELINA_ISD_V01.txt
-a----        2025-09-27  10:31 AM        2788786 SF_1523_EOC_043903_CELINA_ISD_V01.txt


              */

              TeaFiles.LoadStar(districtId, baseFileFolder + "2022-2023 Assessment Files/2022-23 STAAR/SF_0523_3_8_043903_CELINA_ISD_V01.txt");
              TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2022-2023 Assessment Files/2022-23 STAAR/SF_1523_EOCALT_043903_CELINA_ISD_V01.txt");
              TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2022-2023 Assessment Files/2022-23 STAAR/SF_1523_EOC_043903_CELINA_ISD_V01.txt");



              // 2023-2024 Assessment Files/2023-24 MAP

              /*
               
-a----        2025-09-27  10:32 AM        8951696 MAP Data file Spring 2324.csv
-a----        2025-09-27  10:32 AM        6073742 MAP Data file Winter 2324.csv
                
                
               */

            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "2023-2024 Assessment Files/2023-24 MAP/MAP Data file Spring 2324.csv", true);
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "2023-2024 Assessment Files/2023-24 MAP/MAP Data file Winter 2324.csv", true);


               // 2023-2024 Assessment Files/2023-24 STAAR

               /*

-a----        2025-09-27  10:32 AM         104052 SF_0425_3_8ALT_043903_CELINA_ISD_V01.txt
-a----        2025-09-27  10:32 AM        9164580 SF_0524_3_8_043903_CELINA_ISD_V01 (1).txt
-a----        2025-09-27  10:32 AM          18018 SF_1524_EOCALT_043903_CELINA_ISD_V01.txt
-a----        2025-09-27  10:32 AM        2988986 SF_1524_EOC_043903_CELINA_ISD_V01.txt



               */
            
              TeaFiles.LoadStar(districtId, baseFileFolder + "2023-2024 Assessment Files/2023-24 STAAR/SF_0425_3_8ALT_043903_CELINA_ISD_V01.txt");
              TeaFiles.LoadStar(districtId, baseFileFolder + "2023-2024 Assessment Files/2023-24 STAAR/SF_0524_3_8_043903_CELINA_ISD_V01 (1).txt");
              TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2023-2024 Assessment Files/2023-24 STAAR/SF_1524_EOCALT_043903_CELINA_ISD_V01.txt");
              TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2023-2024 Assessment Files/2023-24 STAAR/SF_1524_EOC_043903_CELINA_ISD_V01.txt");



               // 2024-2025 Assessment Files/2024-25 MAP

               /*
             
-a----        2025-09-27  10:32 AM       12367077 ComboStudentAssessment (1).csv
-a----        2025-09-27  10:32 AM       12145818 ComboStudentAssessment_10.csv
-a----        2025-09-27  10:32 AM       11274833 ComboStudentAssessment_7.csv

                */

            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "2024-2025 Assessment Files/2024-25 MAP/ComboStudentAssessment (1).csv", true);
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "2024-2025 Assessment Files/2024-25 MAP/ComboStudentAssessment_10.csv", true);
            CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "2024-2025 Assessment Files/2024-25 MAP/ComboStudentAssessment_7.csv", true);
            


            // 2024-2025 Assessment Files/2024-25 STAAR

            /*
 
-a----        2025-09-27  10:32 AM         104052 SF_0425_3_8ALT_043903_CELINA_ISD_V01.txt
-a----        2025-09-27  10:32 AM       10997496 SF_0525_3_8_043903_CELINA_ISD_V03.txt
-a----        2025-09-27  10:32 AM          28028 SF_1525_EOCALT_043903_CELINA_ISD_V01.txt
-a----        2025-09-27  10:32 AM        3793790 SF_1525_EOC_043903_CELINA_ISD_V02.txt

              
             */

            TeaFiles.LoadStar(districtId, baseFileFolder + "2024-2025 Assessment Files/2024-25 STAAR/SF_0425_3_8ALT_043903_CELINA_ISD_V01.txt");
            TeaFiles.LoadStar(districtId, baseFileFolder + "2024-2025 Assessment Files/2024-25 STAAR/SF_0525_3_8_043903_CELINA_ISD_V03.txt");
            TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2024-2025 Assessment Files/2024-25 STAAR/SF_1525_EOCALT_043903_CELINA_ISD_V01.txt");
            TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2024-2025 Assessment Files/2024-25 STAAR/SF_1525_EOC_043903_CELINA_ISD_V02.txt");
            



            // 2024-2025 Assessment Files/2024-25 TELPAS

            /*
-a----        2025-09-27  10:32 AM         954388 SF_0325_TELPAS_043903_CELINA_ISD_V01.txt              
             
             */

            TeaFiles.LoadTelpas(districtId, baseFileFolder + "2024-2025 Assessment Files/2024-25 TELPAS/SF_0325_TELPAS_043903_CELINA_ISD_V01.txt");


            //TeaFiles.LoadStar(districtId, baseFileFolder + "2024/SF_0425_3_8ALT_043903_CELINA_ISD_V01.txt");


            // In folder 2024

            //  TeaFiles.LoadStar(districtId, baseFileFolder + "2024/SF_0525_3_8_043903_CELINA_ISD_V03.txt");
            //  TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2024/SF_1525_EOC_043903_CELINA_ISD_V02.txt");

            //  TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2024/SF_1525_EOCALT_043903_CELINA_ISD_V01.txt");




            // // TODO  2024/ComboStudentAssessment_10.csv FILE

            //CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "2024/ComboStudentAssessment_10.csv", true);


            result.success = true;
            System.out.println("DONE CelinaSisFiles.Import");


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
