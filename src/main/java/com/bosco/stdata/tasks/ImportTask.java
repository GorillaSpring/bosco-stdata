package com.bosco.stdata.tasks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bosco.stdata.distictImports.AllenFiles;

import com.bosco.stdata.distictImports.BurlesonFiles;
import com.bosco.stdata.distictImports.BurlesonSisFiles;
//import com.bosco.stdata.distictImports.CelinaFiles;
import com.bosco.stdata.distictImports.CelinaSisFiles;
import com.bosco.stdata.distictImports.ClassLinkOneRosterApi;
import com.bosco.stdata.distictImports.MelissaFiles;
import com.bosco.stdata.distictImports.MelissaSisFiles;
import com.bosco.stdata.distictImports.NewBraunfelsSisFiles;
import com.bosco.stdata.distictImports.PowerSchoolOneRosterApi;
import com.bosco.stdata.distictImports.SkywardOneRosterApi;
import com.bosco.stdata.distictImports.SpringtownSisFiles;
import com.bosco.stdata.distictImports.TestFiles;
import com.bosco.stdata.distictImports.Testing;
import com.bosco.stdata.distictImports.UpliftFiles;
import com.bosco.stdata.model.ImportDefinition;
import com.bosco.stdata.model.ImportLog;
import com.bosco.stdata.model.ImportResult;
import com.bosco.stdata.model.MapCourseCsaCode;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.EmailService;
import com.bosco.stdata.utils.ImportHelper;



@Component
public class ImportTask {

    @Autowired
    ImportRepo importRepo;

    @Autowired
    EmailService emailService;

    @Autowired 
    BoscoApi boscoApi;


    public ImportTask() {}


    public String sendImportToBoscoNow(int districtId) {
        try {
            boscoApi.sendImportToBosco(districtId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "Sending";
    }


   

    public String doImports (Boolean sendEmail) {


        // this will do all the imports.
        // we should check if it is running before we do this, but just incase.

        

        //int importStatus = importRepo.getSystemStatus("Import");

        if (ImportHelper.importRunning) {
            System.out.println("Imports are running, so we will bail");
            return "Imports are running, so we will bail";
        }
        else {
            Thread taskThread = new Thread(() -> {
               
                processImports(sendEmail);
            });


            ImportHelper.importRunning = true;
            //importRepo.setSystemStatus("Import", 1);
            taskThread.start();

            return "Imports Running";

            //return "Task started";

        }


    }

    
    public String runImportDefn (Boolean sendEmail, String importDefId) {

         if (ImportHelper.importRunning) {
            System.out.println("Imports are running, so we will bail");
            return "Imports are running, so we will bail";
        }
        else {
            try {
                ImportDefinition importDef = importRepo.getImportDefinition(importDefId);
                Thread taskThread = new Thread(() -> {
                
                    processImportDefn(sendEmail, importDef);
                });


                ImportHelper.importRunning = true;
                //importRepo.setSystemStatus("Import", 1);
                taskThread.start();

                return "Imports Running";

            }
            catch (Exception ex) {
                ImportHelper.importRunning = false;
                return "Error: " + ex.getMessage();
                // faild 
            }
            

            //return "Task started";

        }


    }

    public void processImportDefn (Boolean sendEmail, ImportDefinition importDef) {
        try {
            List<ImportResult> importResults = new ArrayList<>();
            ImportResult res = runImport(importDef);
            importResults.add(res);

         
            if (sendEmail) {

                emailImportResults(importResults);
            }
            System.out.println("DONE");
            ImportHelper.importRunning = false;

        }
        catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
            ImportHelper.importRunning = false;
            //importRepo.setSystemStatus("Import", 0);

        }
    }

    private void processImports(Boolean sendEmail) {

        // so this will send email when done!
        
        try {

            List<ImportResult> importResults = new ArrayList<>();

            
            List<ImportDefinition> importDefs = importRepo.getActiveImportDefinitions();
            for (ImportDefinition importDef : importDefs)
            {
                ImportResult res = runImport(importDef);
                importResults.add(res);
            }

            if (sendEmail) {

                emailImportResults(importResults);
            }
            

            System.out.println("DONE");
            ImportHelper.importRunning = false;
            //importRepo.setSystemStatus("Import", 0);

        }
        catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
            ImportHelper.importRunning = false;
            //importRepo.setSystemStatus("Import", 0);

        }
        // catch (InterruptedException e) {
        //                 Thread.currentThread().interrupt();
        //                 System.out.println(" ---  import interupted in doImport.");
        //                 importRepo.setSystemStatus("Import", 0);

        //             }


    }


    private void emailImportResults(List<ImportResult> importResults) {
        String emailBody = "<html><h5>Imports</h5>";
        emailBody += "<hr>";


        for (ImportResult ir : importResults) {
            if (ir.success) {
                emailBody += "<h6> Success: " + ir.districtId + " ImportId : " + ir.importId + "  </h6>";
                emailBody += importLogs(ir.importId);
                System.out.println("Import: " + ir.districtId + " ID: " + ir.importId);
            }
            else {
                emailBody += "<h6> Failed: " + ir.districtId + " ImportId : " + ir.importId  + "  </h6>";
                emailBody += "<h6> Error: " + ir.errorMessage + "</h6>";
                emailBody += importLogs(ir.importId);

                System.out.println("FAILD Import:" + ir.districtId + " ID: " + ir.importId);
                System.out.println(ir.errorMessage);

            }
        }


        // importResults.forEach(ir -> {
        //     if (ir.success) {
                
        //         System.out.println("Import: " + ir.districtId + " ID: " + ir.importId);
        //     }
        //     else {
        //         System.out.println("FAILD Import:" + ir.districtId + " ID: " + ir.importId);
        //         System.out.println(ir.errorMessage);
        //     }

        // }); 

        // lets add th codes to the import.

        List<MapCourseCsaCode> codes = importRepo.undefinedMapCourseCsaCode();
        if (codes.size() > 0) {
            emailBody += "<hr>UNDEFINED CODES<ul>";

            
            for (MapCourseCsaCode mapCourseCsaCode : codes) {
                emailBody += "<li>" + mapCourseCsaCode.districtId + " : " + mapCourseCsaCode.courseName;

            }
            emailBody += "</ul>";

        }

      

        emailBody += "<hr></html>";


        

        emailService.sendSimpleMessage("BenLevy3@gmail.com",  "Import Results", emailBody);
        
    }


    private ImportResult runImport(ImportDefinition importDef) throws Exception {
        ImportResult importResult;
        
        String importDefId = importDef.getId();

        String importType = importDef.getImportType();
        
        //importRepo.logInfo("Importing: " + importDefId);

        System.out.println("Importing: " + importDefId);


        switch (importType) {
            case "ClassLinkOneRosterApi":
                importResult = ClassLinkOneRosterApi.Import(importDefId);
                //importResults.add(importResult);
                
                break;
            case "SkywardOneRosterApi":

                //Testing.Test(importDefId);

                importResult = SkywardOneRosterApi.Import(importDefId);
                //importResults.add(importResult);

                break;
            case "PowerSchoolOneRosterApi":
                importResult = PowerSchoolOneRosterApi.Import(importDefId);
                //importResults.add(importResult);
                break;
        
            default:

                    switch (importDefId) {
                        case "AllenFiles":
                            importResult = AllenFiles.Import(importDefId);
                            //importResults.add(importResult);
                            break;
                        // case "AllenSis":
                        //     importResult = AllenSis.Import(importDefId);
                        //     importResults.add(importResult);
                        //     break;
                        case "SpringtownSisFiles":
                            importResult = SpringtownSisFiles.Import(importDefId);
                            //importResults.add(importResult);
                            break;
                        case "NewBraunfelsSisFiles":

                            importResult = NewBraunfelsSisFiles.Import(importDefId);
                            //importResults.add(importResult);
                            break;


                        case "CelinaSisFiles":
                            importResult = CelinaSisFiles.Import(importDefId);
                            //importResults.add(importResult);
                            break;
                        case "MelissaSisFiles":
                            importResult = MelissaSisFiles.Import(importDefId);
                            //importResults.add(importResult);
                            break;

                        case "MelissaFiles":
                            importResult = MelissaFiles.Import(importDefId);
                            //importResults.add(importResult);

                            break;

                        case "BurlesonFiles":
                            importResult = BurlesonFiles.Import(importDefId);
                            //importResults.add(importResult);
                            break;
                        case "BurlesonSisFiles":
                            importResult = BurlesonSisFiles.Import(importDefId);
                            //importResults.add(importResult);
                            break;
                        case "Testing":
                            // This is used for code testing, not import testing
                            //TestImportTests.Test(importRepo, "Testing");
                            Testing.Test(importDefId);
                            importResult = new ImportResult();
                            importResult.success = false;
                            break;
                        case "TestFiles":
                            //TestFiles.Import(importRepo, boscoApi, importDefId);

                            importResult = TestFiles.Import(importDefId);
                            //importResults.add(importResult);
                            break;
                        case "UpliftFiles":
                            importResult = UpliftFiles.Import( importDefId);
                            //importResults.add(importResult);
                            
                            break;
                        // case "CelinaFiles":
                        //     // THis is API now.
                        //     importResult = CelinaFiles.Import(importDefId);
                        //     importResults.add(importResult);
                        //     break;
                        default:
                            importRepo.logError("Unknown Import Definition : " + importDefId);
                            importResult = new ImportResult();
                            importResult.success = false;
                            break;
                    }

                break;
        }
        return importResult;
    }

   

    private String importLogs (int importId) {
        List<ImportLog> logs = importRepo.getInfoLogs(importId);

        String html = "<hr>";
        // we will return html

        html += "<h6>Info Logs</h6>";
        html += "<table><tr><th>ImportId</th><th>Info</th><th>Date</th></tr>";
        for(ImportLog log: logs) {
            html += """
                <tr>
                    <td>%d</td>

                    <td>%s</td>
                    <td>%s</td>
                </tr>
            """.formatted(log.getImportId(), log.getInfo(), log.getCreatedDateTime());

            

        }
        html += "</table>";


        logs = importRepo.getErrorLogs(importId);

        html += "<h6>Errors</h6>";

        html += "<table><tr><th>ImportId</th><th>Error</th><th>Date</th></tr>";
        for(ImportLog log: logs) {
            html += """
                <tr>
                    <td>%d</td>
                    
                    <td>%s</td>
                    <td>%s</td>
                </tr>
            """.formatted(log.getImportId(), log.getError(), log.getCreatedDateTime());

            

        }
        html += "</table>";


        return html;
        //html += "</html>";

    }

}
