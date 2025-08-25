package com.bosco.stdata.tasks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bosco.stdata.distictImports.CelinaFiles;
import com.bosco.stdata.distictImports.ClassLinkOneRosterApi;
import com.bosco.stdata.distictImports.SkywardOneRosterApi;
import com.bosco.stdata.distictImports.TestFiles;
import com.bosco.stdata.distictImports.UpliftFiles;
import com.bosco.stdata.model.ImportDefinition;
import com.bosco.stdata.model.ImportResult;
import com.bosco.stdata.repo.ImportRepo;



@Component
public class ImportTask {

    @Autowired
    ImportRepo importRepo;

    public ImportTask() {}

    public String doImports () {


        // this will do all the imports.
        // we should check if it is running before we do this, but just incase.

        int importStatus = importRepo.getSystemStatus("Import");

        if (importStatus > 0) {
            System.out.println("Imports are running, so we will bail");
            return "Imports are running, so we will bail";
        }
        else {
            Thread taskThread = new Thread(() -> {
               
                processImports();
            });


            importRepo.setSystemStatus("Import", 1);
            taskThread.start();

            return "Imports Running";

            //return "Task started";

        }


    }

    private void processImports() {
        try {

            List<ImportResult> importResults = new ArrayList<>();

            List<ImportDefinition> importDefs = importRepo.getActiveImportDefinitions();
            for (ImportDefinition importDef : importDefs)
            {
                ImportResult importResult;
                
                String importDefId = importDef.getId();

                String importType = importDef.getImportType();
                
                importRepo.logInfo("Importing: " + importDefId);

                System.out.println("Importing: " + importDefId);


                switch (importType) {
                    case "ClassLinkOneRosterApi":
                        importResult = ClassLinkOneRosterApi.Import(importDefId);
                        importResults.add(importResult);
                        
                        break;
                    case "SkywardOneRosterApi":
                        importResult = SkywardOneRosterApi.Import(importDefId);
                        importResults.add(importResult);

                        break;
                
                    default:

                            switch (importDefId) {
                                case "Testing":
                                    // This is used for code testing, not import testing
                                    //TestImportTests.Test(importRepo, "Testing");
                                    break;
                                case "TestFiles":
                                    //TestFiles.Import(importRepo, boscoApi, importDefId);

                                    importResult = TestFiles.Import(importDefId);
                                    importResults.add(importResult);
                                    break;
                                case "UpliftFiles":
                                    importResult = UpliftFiles.Import( importDefId);
                                    importResults.add(importResult);
                                    
                                    break;
                                case "CelinaFiles":
                                    importResult = CelinaFiles.Import(importDefId);
                                    importResults.add(importResult);
                                    break;
                                default:
                                    importRepo.logError("Unknown Import Definition : " + importDefId);
                                    break;
                            }

                        break;
                }
            }

            importResults.forEach(ir -> {
                if (ir.success) {
                    System.out.println("Import: " + ir.districtId + " ID: " + ir.importId);
                }
                else {
                    System.out.println("FAILD Import:" + ir.districtId + " ID: " + ir.importId);
                    System.out.println(ir.errorMessage);
                }

            }); 

            System.out.println("DONE");
            importRepo.setSystemStatus("Import", 0);

        }
        catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
            importRepo.setSystemStatus("Import", 0);

        }
        // catch (InterruptedException e) {
        //                 Thread.currentThread().interrupt();
        //                 System.out.println(" ---  import interupted in doImport.");
        //                 importRepo.setSystemStatus("Import", 0);

        //             }


    }

    private void xprocessImports() {




        System.out.println("Processing Imports");
         System.out.println("Starting import in doImport...");
                try {

                    TestFiles.Import("SomeDef");
                    SkywardOneRosterApi.Import("NotherDef");
                    Thread.sleep(14000);
                    System.out.println("----  import finished in doImport.");
                    importRepo.setSystemStatus("Import", 0);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println(" ---  import interupted in doImport.");
                    importRepo.setSystemStatus("Import", 0);

                }
    }

}
