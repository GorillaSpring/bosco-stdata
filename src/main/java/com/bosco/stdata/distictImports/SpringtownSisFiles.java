package com.bosco.stdata.distictImports;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.controllers.AuthedApi;
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
public class SpringtownSisFiles {

    private final AuthedApi authedApi;
    private final AppConfig appConfig;
    @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;

    private static SpringtownSisFiles i;

    SpringtownSisFiles(AppConfig appConfig, AuthedApi authedApi) {
        this.appConfig = appConfig;
        this.authedApi = authedApi;
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


            



            // STAAR

            
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0523_3_8_184902_SPRINGTOWN_ISD_V01.txt");
            TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0524_3_8_184902_SPRINGTOWN_ISD_V01.txt");
            


            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/1322_EOC_184902_SPRINGTOWN_ISD_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1323_EOC_184902_SPRINGTOWN_ISD_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1524_EOC_184902_SPRINGTOWN_ISD_V01.txt");
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1525_EOC_184902_SPRINGTOWN_ISD_V02.txt");
            
            
            
            // // These 2 had 0 Students?
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1624_EOC_184902_SPRINGTOWN_ISD_V01.txt");            
            TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1625_EOC_184902_SPRINGTOWN_ISD_V01.txt");



            // TELPAS

            TeaFiles.LoadTelpas(districtId, baseFileFolder + "Past/SF_0323_TELPAS_184902_SPRINGTOWN_ISD_V01.txt");
            TeaFiles.LoadTelpas(districtId, baseFileFolder + "Past/SF_0324_TELPAS_184902_SPRINGTOWN_ISD_V01.txt");
            TeaFiles.LoadTelpas(districtId, baseFileFolder + "Past/SF_0325_TELPAS_184902_SPRINGTOWN_ISD_V01.txt");



            CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "Past/ComboStudentAssessment (1) (1).csv", false);
            CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "Past/ComboStudentAssessment (1).csv", false);
            CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "Past/ComboStudentAssessment (2).csv", false);
            CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "Past/ComboStudentAssessment (3).csv", false);
            CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "Past/ComboStudentAssessment (4).csv", false);
            CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "Past/ComboStudentAssessment (5).csv", false);
            CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "Past/ComboStudentAssessment (6).csv", false);
            CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "Past/ComboStudentAssessment (7).csv", false);
            CsvFiles.LoadMapComboStudentAssessment(districtId, baseFileFolder + "Past/SISD_NWEA_MOY_2025.csv", false);



            

          

            //   For mClass files, these are different then others.  There seems to be 3 different fomats
            //   For these, we pass in the schoolYear and the period
            //   Then the columns for StudentNumber, Proficiency and Score

            

             Boolean isValid = false;

            isValid = CsvFiles.GenericMClass_Lexie_Load(districtId, baseFileFolder + "Past/mCLASS_BOY_2024_2025.csv", "2024-2025", "BOY", 9, 5, 6 );
            isValid = CsvFiles.GenericMClass_Lexie_Load(districtId, baseFileFolder + "Past/mclass_EOY_2023_2024.csv", "2023-2024", "EOY", 9, 5, 6 );
            isValid = CsvFiles.GenericMClass_Lexie_Load(districtId, baseFileFolder + "Past/mCLASS_Lectura_BOY_24_25.csv", "2024-2025", "BOY", 9, 5, 6 );
            isValid = CsvFiles.GenericMClass_Lexie_Load(districtId, baseFileFolder + "Past/MOY_mclass_Eduphoria_2025.csv", "2024-2025", "MOY", 9, 5, 6 );
            isValid = CsvFiles.GenericMClass_Lexie_Load(districtId, baseFileFolder + "Past/MOY_mclass_lectura_eduphoria_2025.csv", "2024-2025", "MOY", 9, 5, 6 );


            isValid = CsvFiles.GenericMClass_Lexie_Load(districtId, baseFileFolder + "Past/MOY_mCLASS__2023.csv", "2022-2023", "MOY", 10, 6, 7);




            // BOSCOK12 - Grades


            // Loaded in Dev and Prod, NOT IN TEST!

            CsvFiles.LoadGradesNbIsd(districtId, baseFileFolder + "BOSCOK12 - Grades.csv");

          
            CsvFiles.LoadSpringtownDiscipline(districtId, baseFileFolder + "BOSCOK12 - Discipline.csv");



            
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
            System.out.println("DONE SpringtownSisFiles.Import");



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
