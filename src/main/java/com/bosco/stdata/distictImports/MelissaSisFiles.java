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
        ImportResult result = new ImportResult();


        try {
            ImportDefinition importDef = i.importRepo.getImportDefinition(importDefId);

            int baseImportId = importDef.getBaseImportId();

            List<ImportSetting> importSettings = i.importRepo.getImportSettings(importDefId);

            int districtId = importDef.getDistrictId();

            result.importId = 0;
            result.districtId = districtId;
            result.baseImportId = baseImportId;
            
            //String baseFileFolder = "C:/test/uplift/" + subFolder + "/";
            String baseFileFolder = ImportHelper.ValueForSetting(importSettings, "baseFolder");

            String archiveFolder =  ImportHelper.ValueForSetting(importSettings, "archiveFolder");


            int importId = i.importRepo.prepImport(districtId, "Sis File Loading " + baseFileFolder);



            // Will just reload
             TeaFiles.LoadStar(districtId, baseFileFolder + "2022/SF_0523_3-8_043908_MELISSA ISD_V01.txt");
             TeaFiles.LoadStar(districtId, baseFileFolder + "2022/SF_0423_3-8ALT_043908_MELISSA ISD_V01.txt");
             TeaFiles.LoadStar(districtId, baseFileFolder + "2023/SF_0424_3-8ALT_043908_MELISSA ISD_V01.txt");


// RELOAD THESE
            TeaFiles.LoadTelpas(districtId, baseFileFolder + "2022/SP_0323_TELPAS_043908_MELISSA ISD_V01.txt");

            

                // // SF_0323_TELPASALT_043908_MELISSA ISD_V01
                // // Loaded 9 Students
            TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "2022/SF_0323_TELPASALT_043908_MELISSA ISD_V01.txt");

            
             TeaFiles.LoadTelpas(districtId, baseFileFolder + "2023/SF_0324_TELPAS_043908_MELISSA ISD_V01.txt");

                // // SF_0324_TELPASALT_043908_MELISSA ISD_V01
             TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "2023/SF_0324_TELPASALT_043908_MELISSA ISD_V01.txt");
             
             
             // SF_0423_3-8ALT_043908_MELISSA ISD_V01
             
            
            // This mClass files.
            // 2022_2023_2024_2025 mClass
            // CsvFiles.LoadDibels8(districtId, baseFileFolder + "2022_2023_2024_2025 mClass/dibels8_BM_2021-2022_BOY_MOY_EOY_grades-KG-01-02-03-04-05-06_2025-09-08_19-56-59.csv", false);
            // CsvFiles.LoadDibels8(districtId, baseFileFolder + "2022_2023_2024_2025 mClass/dibels8_BM_2022-2023_BOY_MOY_EOY_grades-KG-01-02-03-04-05-06_2025-09-08_19-57-46.csv", false);
            // CsvFiles.LoadDibels8(districtId, baseFileFolder + "2022_2023_2024_2025 mClass/dibels8_BM_2023-2024_BOY_MOY_EOY_grades-KG-01-02-03-04-05-06_2025-09-08_19-58-45.csv", false);
            // CsvFiles.LoadDibels8(districtId, baseFileFolder + "2022_2023_2024_2025 mClass/dibels8_BM_2024-2025_BOY_MOY_EOY_grades-KG-01-02-03-04-05-06_2025-09-08_19-59-22.csv", false);

            

                
                // // In folder 2022





                // // SF_0523_3-8_043908_MELISSA ISD_V01
                // TeaFiles.LoadStar(districtId, baseFileFolder + "2022/SF_0523_3-8_043908_MELISSA ISD_V01.txt");


                
                
                // // SF_1323_EOC_043908_MELISSA ISD_V01
                // // SF_1523_EOC_043908_MELISSA ISD_V01_August5 file
                // // SF_1523_EOCALT_043908_MELISSA ISD_V01
                // // SP_1623_EOC_043908_MELISSA ISD_V01

                // //  ** Loaded 0 students!
                // TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2022/SF_1323_EOC_043908_MELISSA ISD_V01.txt");

                // TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2022/SF_1523_EOC_043908_MELISSA ISD_V01_August5 file.txt");
                // TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2022/SF_1523_EOCALT_043908_MELISSA ISD_V01.txt");

                // //  ** Loaded 0 students!
                // TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2022/SP_1623_EOC_043908_MELISSA ISD_V01.txt");



                // // SP_0323_TELPAS_043908_MELISSA ISD_V01
                // TeaFiles.LoadTelpas(districtId, baseFileFolder + "2022/SP_0323_TELPAS_043908_MELISSA ISD_V01.txt");

                // // SF_0323_TELPASALT_043908_MELISSA ISD_V01
                // // Loaded 9 Students
                // TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "2022/SF_0323_TELPASALT_043908_MELISSA ISD_V01.txt");


                // // TODO: 2023 Folder

                // // SF_0524_3-8_043908_MELISSA ISD_V01
                // TeaFiles.LoadStar(districtId, baseFileFolder + "2023/SF_0524_3-8_043908_MELISSA ISD_V01.txt");


                // // SF_1324_EOC_043908_MELISSA ISD_V02

                // // SF_1524_EOC_043908_MELISSA ISD_V01
                // // SF_1524_EOCALT_043908_MELISSA ISD_V01
                // // SF_1624_EOC_043908_MELISSA ISD_V01

                // TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2023/SF_1324_EOC_043908_MELISSA ISD_V02.txt");
                // TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2023/SF_1524_EOC_043908_MELISSA ISD_V01.txt");
                // TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2023/SF_1524_EOCALT_043908_MELISSA ISD_V01.txt");
                // TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2023/SF_1624_EOC_043908_MELISSA ISD_V01.txt");


                // // SF_0324_TELPAS_043908_MELISSA ISD_V01

                // TeaFiles.LoadTelpas(districtId, baseFileFolder + "2023/SF_0324_TELPAS_043908_MELISSA ISD_V01.txt");

                // // SF_0324_TELPASALT_043908_MELISSA ISD_V01
                // TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "2023/SF_0324_TELPASALT_043908_MELISSA ISD_V01.txt");
                

                // // TODO: 2024 Folder

                // // SF_0525_3-8_043908_MELISSA ISD_V03
                // TeaFiles.LoadStar(districtId, baseFileFolder + "2024/SF_0525_3-8_043908_MELISSA ISD_V03.txt");


                // // SF_1525_EOC_043908_MELISSA ISD_V02
                // TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2024/SF_1525_EOC_043908_MELISSA ISD_V02.txt");

                // // SP_1625_EOC_043908_MELISSA ISD_V01
                // TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2024/SP_1625_EOC_043908_MELISSA ISD_V01.txt");


                // // Melissa
                // // these appare to have correct student id's
                // CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "24-25 MAP/Fall 2024 district scores.csv", false);
                // CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "24-25 MAP/Spring 25 district scores.csv", false);
                // CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "24-25 MAP/Winter 25 district scores.csv", false);

                // CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "23-24 MAP/2024_Winter_Map_ComboStudentAssessment (1).csv", false);
                // CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "23-24 MAP/Fall23_24MAPComboStudentAssessment.csv", false);
                // CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "23-24 MAP/Spring2024_MAP_ComboStudentAssessment.csv", false);


                // //this one has an empty level, but we skip.
                // CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "22-23 MAP/MAP_Fall_22_23ComboStudentAssessment.csv", false);
                // CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "22-23 MAP/MOY_MAP_22_23_ComboStudentAssessment.csv", false);
                // CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "22-23 MAP/Spring_22_23_ComboStudentAssessment.csv", false);

                // CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "21-22 MAP/Spring_2022_MAP_ComboStudentAssessment.csv", false);
                // CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "21-22 MAP/Winter_21_22_ComboStudentAssessment (1).csv", false);
                // CsvFiles.LoadComboStudentAssessment(districtId, baseFileFolder + "21-22 MAP/Winter_21_22_ComboStudentAssessment.csv", false);



            // TODO  TODO Folder

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
