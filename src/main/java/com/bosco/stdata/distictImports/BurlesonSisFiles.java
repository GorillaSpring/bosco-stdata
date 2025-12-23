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
public class BurlesonSisFiles {

    private final AppConfig appConfig;
    @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;

    private static BurlesonSisFiles i;

    BurlesonSisFiles(AppConfig appConfig) {
        this.appConfig = appConfig;
    }  // instance

    @PostConstruct
    public void init() {
        System.out.println("BurlesonSisFiles - init()");
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

            int importId = i.importRepo.prepImport(districtId, importDefId, isRoster,isSisData,  "Burleson Sis files " + baseFileFolder);

             LocalDateTime startDateTime = LocalDateTime.now();

            result.importId = importId;
            result.districtId = districtId;


            // Run in TEST, PROD, and DEV.
            CsvFiles.LoadMapCourseNameCsaCode(baseFileFolder + "Burleson_map_courseName_csaCode.csv");

            // THIS IS Everything as of Nov 26.



            CsvFiles.LoadGradesPriorBurleson(districtId, baseFileFolder + "WORKING/grades_prior_year.csv");

            // // grades_current_year
            CsvFiles.LoadGradesCurrentBurleson(districtId, baseFileFolder + "WORKING/grades_current_year.csv");
            






          
            CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "2026/Final Combo File - Fall 2025.csv", false);

   


            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "PAST/SF_0523_3-8_126902_BURLESON ISD_V01.txt");
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "PAST/SF_0524_3-8_126902_BURLESON ISD_V01.txt");
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "PAST/SF_0525_3-8_126902_BURLESON ISD_V03.txt");
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "PAST/SP_0424_3-8ALT_126902_BURLESON ISD_V01.txt");

            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "PAST/SF_1524_EOC_126902_BURLESON ISD_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "PAST/SF_1525_EOC_126902_BURLESON ISD_V02.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "PAST/SF_1525_EOCALT_126902_BURLESON ISD_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "PAST/SP_1523_EOC_126902_BURLESON ISD_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "PAST/SP_1523_EOCALT_126902_BURLESON ISD_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "PAST/SP_1524_EOCALT_126902_BURLESON ISD_V01.txt");


            TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "PAST/0323_TELPASALT_126902_BURLESON ISD_V01.txt");
            TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "PAST/SF_0324_TELPASALT_126902_BURLESON ISD_V01.txt");
            TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "PAST/SF_0325_TELPASALT_126902_BURLESON ISD_V01.txt");

            TeaFiles.LoadTelpas(districtId, baseFileFolder + "PAST/SF_0324_TELPAS_126902_BURLESON ISD_V01.txt");
            TeaFiles.LoadTelpas(districtId, baseFileFolder + "PAST/SP_0323_TELPAS_126902_BURLESON ISD_V01.txt");
            TeaFiles.LoadTelpas(districtId, baseFileFolder + "PAST/SP_0325_TELPAS_126902_BURLESON ISD_V01.txt");

            
  
  





            
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
            System.out.println("DONE BurlesonSisFiles.Import");



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
