package com.bosco.stdata.distictImports;

import java.text.NumberFormat.Style;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.apache.commons.text.WordUtils;
//import org.springframework.batch.core.repository.persistence.ExecutionContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.FlatFileItemReader<com.bosco.stdata.teaModel.CelinaCombo>;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.model.ImportDefinition;
import com.bosco.stdata.model.ImportResult;
import com.bosco.stdata.model.ImportSetting;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.BoscoClient;
import com.bosco.stdata.sisDataFiles.CsvFiles;
import com.bosco.stdata.teaModel.BoscoProficiency;
import com.bosco.stdata.teaModel.CelinaCombo;


import com.bosco.stdata.teaModel.Star2024;
import com.bosco.stdata.teaModel.Star2024EOC;
import com.bosco.stdata.teaModel.Telpas2024;
import com.bosco.stdata.utils.ImportHelper;
import com.bosco.stdata.utils.TeaStaarFlatFileReader;

import jakarta.annotation.PostConstruct;


@Component
public class Testing {

    private final BoscoClient boscoClient;

    private final AppConfig appConfig;

    // private final teaModel.CelinaCombo> celinaComboItemReader;

    // private final CelinaFiles celinaFiles;

    // private final BoscoClient boscoClient;

    // private final AppConfig appConfig;

    // this is just for testing while we are doing dev.
    // not a real import

    @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;
    

    private static Testing i;


    Testing(AppConfig appConfig, BoscoClient boscoClient) {
        this.appConfig = appConfig;
        this.boscoClient = boscoClient;
    }


    @PostConstruct
    public void init() {
        System.out.println("Testing - init()");
        i = this;
    }

   
    
    


    public static void Test(String importDefId) throws Exception {

        Boolean isRoster = true;
        Boolean isSisData = false;
        
        System.out.println("Test Starting");

        i.boscoApi.sendImportToBosco(4813290);

        // List<String> assignedSchools;

        // String id = "4813290.001515";

        // id = "4813290.000097";

        // assignedSchools = i.importRepo.schoolsForTeacher(id);

        // System.out.println("Assigned Schools");
        // System.out.println(assignedSchools);
        // System.out.println("---");


        // String testStr = "O'Brien";

        // System.out.println(testStr + " - " + ImportHelper.TitleCase(testStr)); 

        // testStr = "O'BRIEN";
        // System.out.println(testStr + " - " + ImportHelper.TitleCase(testStr)); 


        // testStr = "o'brien";
        // System.out.println(testStr + " - " + ImportHelper.TitleCase(testStr)); 

        // testStr = "SMITH-JONES";

        // System.out.println(testStr + " - " + ImportHelper.TitleCase(testStr)); 

        // testStr = "Smith-Jones";

        // System.out.println(testStr + " - " + ImportHelper.TitleCase(testStr)); 


        // testStr = "smith-jones";

        // System.out.println(testStr + " - " + ImportHelper.TitleCase(testStr)); 

        // testStr = "SMITH JONES";

        // System.out.println(testStr + " - " + ImportHelper.TitleCase(testStr)); 


        // testStr = "smith jones";

        // System.out.println(testStr + " - " + ImportHelper.TitleCase(testStr)); 

         //CsvFiles.LoadFindUsers(  "C:\\db\\Docs\\Find_dups\\all_users.csv");


        // try to create a file reader

        //  ImportDefinition importDef = i.importRepo.getImportDefinition(importDefId);


        //   int districtId = importDef.getDistrictId();

        // List<ImportSetting> importSettings = i.importRepo.getImportSettings(importDefId);


        // System.out.println("  CALLLING ");

        // int newImportId = i.importRepo.prepImport(districtId, importDefId, isRoster, isSisData, "Roster  - Not sis");

        // System.out.println("We got id: " + newImportId);

        // if (!importDef.getForceLoad() && isRoster) {
        //     System.out.println("Checking Delta");
        //     String checkDeltas = i.importRepo.checkImportDeltas(9999999, "TestFiles");
        //     if (!checkDeltas.equals("OK")) {
        //         throw new Exception("Check Import Delta failed: " + checkDeltas);
        //     }
        //     System.err.println("Check Delta OK");
        // }
        // else {
        //     System.out.println("Force Import - no check");
        // }



        
        
        System.out.println(("-----------------------"));

       

    

    }


    
}
