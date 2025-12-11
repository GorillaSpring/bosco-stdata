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

    private final CsvFiles csvFiles;

    private final AppConfig appConfig;
   @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;

    private static NewBraunfelsSisFiles i;

    NewBraunfelsSisFiles(AppConfig appConfig, CsvFiles csvFiles) {
        this.appConfig = appConfig;
        this.csvFiles = csvFiles;
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


            // working on the discipline

            CsvFiles.LoadDisciplineLedger (districtId, baseFileFolder + "ToDo/discicpline_2025.csv");


            CsvFiles.LoadDisciplineLedger (districtId, baseFileFolder + "ToDo/discicpline_2026.csv");


            //CsvFiles.LoadDisciplineLedger (districtId, baseFileFolder + "ToDo/discicpline_2026.csv");


            // ALL BELOW is done.  Some files may be reloads.





            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "2026/mClass_25_26_BOY_English.csv", 5, -1, 10, 6, 7, 11 );
            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "2026/mClass_25_26_BOY_Spanish.csv", 5, -1, 10, 6,  7,11 );


            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_BOY_24-25.csv", 45, 0, -1, 46, 47 , 98);
            
            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_BOY_Spanish_24-25.csv", 45, 0, -1, 46, 47 , 98);
            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mclass_EOY_24-25.csv", 45, 0, -1, 46, 47 , 98);
            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mclass_EOY_Span_24-25.csv", 45, 0, -1, 46, 47 , 98);
            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_MOY_24-25.csv", 45, 0, -1, 46, 47 , 98);
            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_MOY_Spanish_24-25.csv", 45, 0, -1, 46, 47 , 98);
            
            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_23_24_BOY_English.csv", 6, -1, 11, 7,  8,12);
            
            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_23_24_BOY_Spanish.csv", 6, -1, 11, 7, 8,12);
            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_23_24_EOY_English.csv", 6, -1, 11, 7, 8,12);
            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_23_24_EOY_Spanish.csv", 6, -1, 11, 7, 8,12);
            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_23_24_MOY_English.csv", 6, -1, 11, 7, 8,12);
            // isVald = CsvFiles.GenericMClass_Load(districtId, baseFileFolder + "Past2/mClass_23_24_MOY_Spanish.csv", 6, -1, 11, 7, 8,12);



            // CsvFiles.LoadGradesNbIsd(districtId, baseFileFolder + "Past2/grades_S1_2023-2024.csv");
            // CsvFiles.LoadGradesNbIsd(districtId, baseFileFolder + "Past2/grades_S1_2024-2025.csv");
            // CsvFiles.LoadGradesNbIsd(districtId, baseFileFolder + "Past2/grades_S2_2023-2024.csv");
            // CsvFiles.LoadGradesNbIsd(districtId, baseFileFolder + "Past2/grades_S2_2024-2025.csv");


            //  CsvFiles.LoadGradesNbIsd(districtId, baseFileFolder + "2026/grades_current_2026.csv");



            
            // TeaFiles.LoadTelpas(districtId, baseFileFolder + "Past/SF_0323_TELPAS_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadTelpas(districtId, baseFileFolder + "Past/SF_0324_TELPAS_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadTelpas(districtId, baseFileFolder + "Past/SF_0325_TELPAS_046901_NEW BRAUNFELS I_V01.txt");

            // TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "Past/SF_0323_TELPASALT_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadTelpasAlt(districtId, baseFileFolder + "Past/SF_0325_TELPASALT_046901_NEW BRAUNFELS I_V01.txt");


            // TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0423_3-8ALT_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0424_3-8ALT_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0425_3-8ALT_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0523_3-8_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0524_3-8_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadStaarAndStaarAlt(districtId, baseFileFolder + "Past/SF_0525_3-8_046901_NEW BRAUNFELS I_V03.txt");
            
            
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1323_EOC_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1324_EOC_046901_NEW BRAUNFELS I_V02.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1523_EOCALT_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1523_EOC_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1524_EOCALT_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1524_EOC_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1525_EOCALT_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1525_EOC_046901_NEW BRAUNFELS I_V02.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1623_EOC_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1624_EOC_046901_NEW BRAUNFELS I_V01.txt");
            // TeaFiles.LoadStarEOCAndEOCAlt(districtId, baseFileFolder + "Past/SF_1625_EOC_046901_NEW BRAUNFELS I_V01.txt");
           



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
