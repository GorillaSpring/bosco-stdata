package com.bosco.stdata.distictImports;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
   @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;

    private static CelinaSisFiles i;  // instance

    @PostConstruct
    public void init() {
        System.out.println("CelinaSisFiles - init()");
        i = this;
    }


    public static ImportResult Import(String importDefId) {
        ImportResult result = new ImportResult();


        try {
            ImportDefinition importDef = i.importRepo.getImportDefinition(importDefId);

            int baseImportId = importDef.getBaseImportId();

            List<ImportSetting> importSettings = i.importRepo.getImportSettings(importDefId);

            int districtId = importDef.getDistrictId();
            
            // WE don't prep an import!
            //int importId = i.importRepo.prepImport(districtId, "Import for " + importDefId);

            result.importId = 0;
            result.districtId = districtId;
            result.baseImportId = baseImportId;
            
            //String baseFileFolder = "C:/test/uplift/" + subFolder + "/";
            String baseFileFolder = ImportHelper.ValueForSetting(importSettings, "baseFolder");

            String archiveFolder =  ImportHelper.ValueForSetting(importSettings, "archiveFolder");


            int importId = i.importRepo.prepImport(districtId, "Sis File Loading " + baseFileFolder);


            // In folder 2024

            TeaFiles.LoadStar(districtId, baseFileFolder + "2024/SF_0525_3_8_043903_CELINA_ISD_V03.txt");
            TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2024/SF_1525_EOC_043903_CELINA_ISD_V02.txt");

            TeaFiles.LoadStarEOC(districtId, baseFileFolder + "2024/SF_1525_EOCALT_043903_CELINA_ISD_V01.txt");




            // TODO  2024/ComboStudentAssessment_10.csv FILE

            CsvFiles.LoadComboStudentAssessment(districtId, "C:/test/importBase/tea/ComboStudentAssessment_10.csv", true);


            result.success = true;
            System.out.println("DONE CelinaSisFiles.Import");

            


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
