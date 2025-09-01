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
import com.bosco.stdata.service.EmailService;



@Component
public class ImportTask {

    @Autowired
    ImportRepo importRepo;

    @Autowired
    EmailService emailService;


    public ImportTask() {}



    public String doSkywardSpedTest () {
        //String res = SkywardOneRosterApi.GetSpecialEducationEnrollmentTX("BoscoSkywardApi");
        String res = SkywardOneRosterApi.GetSpecialEducationEnrollmentTX("ClintSkywardApi");
        // ClintSkywardApi
        return res;
    }

    public String doImports (Boolean sendEmail) {


        // this will do all the imports.
        // we should check if it is running before we do this, but just incase.

        

        int importStatus = importRepo.getSystemStatus("Import");

        if (importStatus > 0) {
            System.out.println("Imports are running, so we will bail");
            return "Imports are running, so we will bail";
        }
        else {
            Thread taskThread = new Thread(() -> {
               
                processImports(sendEmail);
            });


            importRepo.setSystemStatus("Import", 1);
            taskThread.start();

            return "Imports Running";

            //return "Task started";

        }


    }

    private void processImports(Boolean sendEmail) {

        // so this will send email when done!
        
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

            
            String emailBody = "<html><h6>Imports</h6>";
            emailBody += "<ul>";


            for (ImportResult ir : importResults) {
                if (ir.success) {
                    emailBody += "<li> Success: " + ir.districtId + " ImportId : " + ir.importId + "  : <a href='http://localhost:8080/import/getLogsHTML/" + ir.importId  + "'>Logs</a>";
                    System.out.println("Import: " + ir.districtId + " ID: " + ir.importId);
                }
                else {
                    emailBody += "<li> Failed: " + ir.districtId + " ImportId : " + ir.importId  + "  : <a href='http://localhost:8080/import/getLogsHTML/" + ir.importId  + "'>Logs</a>";
                    System.out.println("FAILD Import:" + ir.districtId + " ID: " + ir.importId);
                    System.out.println(ir.errorMessage);

                }
            }

            emailBody += "</ul></html>";

            // importResults.forEach(ir -> {
            //     if (ir.success) {
                    
            //         System.out.println("Import: " + ir.districtId + " ID: " + ir.importId);
            //     }
            //     else {
            //         System.out.println("FAILD Import:" + ir.districtId + " ID: " + ir.importId);
            //         System.out.println(ir.errorMessage);
            //     }

            // }); 

            emailService.sendSimpleMessage("BenLevy3@gmail.com",  "Import Results", emailBody);

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
