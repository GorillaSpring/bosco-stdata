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
public class SpringtownSisFiles {
    private final AppConfig appConfig;
    @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;

    private static SpringtownSisFiles i;

    SpringtownSisFiles(AppConfig appConfig) {
        this.appConfig = appConfig;
    }  // instance

    @PostConstruct
    public void init() {
        System.out.println("SpringtownSisFiles - init()");
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

            int importId = i.importRepo.prepImport(districtId, importDefId, isRoster,isSisData,  "Springtown Sis files " + baseFileFolder);

             LocalDateTime startDateTime = LocalDateTime.now();

            result.importId = importId;
            result.districtId = districtId;

// LOADED IN DEV, TEST, PROD
            //CsvFiles.LoadMapCourseNameCsaCode(baseFileFolder + "springtown_course_csaCode-UpdatedML.csv");


            // BOSCOK12 - Grades

            CsvFiles.LoadGradesNbIsd(districtId, baseFileFolder + "BOSCOK12 - Grades.csv");

          



            
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
