package com.bosco.stdata.distictImports;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.controllers.AdminController;
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

    private final AdminController adminController;

    private final AppConfig appConfig;
   @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;

    private static CelinaSisFiles i;

    CelinaSisFiles(AppConfig appConfig, AdminController adminController) {
        this.appConfig = appConfig;
        this.adminController = adminController;
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

            
            // As of Jan 9th.

            // we are all loaded below

            // we have a new academic grades file now.`
            // we have a fixed discipline now.
            // we are waiting on a new attendance (and hopefully tardy) file now.


            CsvFiles.LoadGradesCelina(districtId, baseFileFolder + "/academicgrades.csv");
            CsvFiles.LoadCelinaDiscipline(districtId, baseFileFolder + "/discipline.csv");

            
            // ALL Loaded as of Nov 26.



            // CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "/2025-2026/MAP Data file Fall 2526.csv", false);


            //  //CsvFiles.LoadMapCourseNameCsaCode(baseFileFolder + "Celina_map_courseName_csaCode.csv");

            //  // This is broken because back to comma seporated.
            // // CsvFiles.LoadGradesCelina(districtId, baseFileFolder + "/academicgrades.csv");

            // CsvFiles.LoadCelinaDiscipline(districtId, baseFileFolder + "/discipline.csv");

             








            //  CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "/2022-2023 Assessment Files/2022-23 MAP/MAP Data file Fall 2223.csv", true);
            //  CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "/2022-2023 Assessment Files/2022-23 MAP/MAP Data file Spring 2223.csv", true);
            //  CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "/2022-2023 Assessment Files/2022-23 MAP/MAP Data file Winter 2223.csv", true);




            // TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "/2022-2023 Assessment Files/2022-23 STAAR/SF_0523_3_8_043903_CELINA_ISD_V01.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "/2022-2023 Assessment Files/2022-23 STAAR/SF_1523_EOCALT_043903_CELINA_ISD_V01.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "/2022-2023 Assessment Files/2022-23 STAAR/SF_1523_EOC_043903_CELINA_ISD_V01.txt");


            // TeaFiles.LoadTelpas(districtId, baseFileFolder + "/2022-2023 Assessment Files//2022-23 TELPAS/SF_0323_TELPAS_043903_CELINA ISD_V02.txt");
            // TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "/2022-2023 Assessment Files//2022-23 TELPAS/SF_0323_TELPASALT_043903_CELINA ISD_V01.txt");



            // CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "/2023-2024 Assessment Files/2023-24 MAP/MAP Data file Spring 2324.csv", true);
            // CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "/2023-2024 Assessment Files/2023-24 MAP/MAP Data file Winter 2324.csv", true);
            // CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "/2023-2024 Assessment Files/2023-24 MAP/MAP Data file Fall 2324.csv", true);

   
            // TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "/2023-2024 Assessment Files/2023-24 STAAR/SF_0425_3_8ALT_043903_CELINA_ISD_V01.txt");
            // TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "/2023-2024 Assessment Files/2023-24 STAAR/SF_0524_3_8_043903_CELINA_ISD_V01 (1).txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "/2023-2024 Assessment Files/2023-24 STAAR/SF_1524_EOCALT_043903_CELINA_ISD_V01.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "/2023-2024 Assessment Files/2023-24 STAAR/SF_1524_EOC_043903_CELINA_ISD_V01.txt");


            
            // TeaFiles.LoadTelpas(districtId, baseFileFolder + "/2023-2024 Assessment Files/2023-24 TELPAS/SF_0324_TELPAS_043903_CELINA ISD_V01.txt");
            // TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "/2023-2024 Assessment Files/2023-24 TELPAS/SF_0324_TELPASALT_043903_CELINA ISD_V01.txt");




            // CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "/2024-2025 Assessment Files/2024-25 MAP/ComboStudentAssessment (1).csv", true);
            // CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "/2024-2025 Assessment Files/2024-25 MAP/ComboStudentAssessment_10.csv", true);
            // CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "/2024-2025 Assessment Files/2024-25 MAP/ComboStudentAssessment_7.csv", true);
            
    

            // TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "/2024-2025 Assessment Files/2024-25 STAAR/SF_0425_3_8ALT_043903_CELINA_ISD_V01.txt");
            // TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "/2024-2025 Assessment Files/2024-25 STAAR/SF_0525_3_8_043903_CELINA_ISD_V03.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "/2024-2025 Assessment Files/2024-25 STAAR/SF_1525_EOCALT_043903_CELINA_ISD_V01.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "/2024-2025 Assessment Files/2024-25 STAAR/SF_1525_EOC_043903_CELINA_ISD_V02.txt");
            



            // TeaFiles.LoadTelpas(districtId, baseFileFolder + "/2024-2025 Assessment Files/2024-25 TELPAS/SF_0325_TELPAS_043903_CELINA_ISD_V01.txt");


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
