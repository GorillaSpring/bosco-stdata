package com.bosco.stdata.controllers;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.bosco.stdata.model.ImportChanges;
import com.bosco.stdata.model.ImportDefinition;
import com.bosco.stdata.model.ImportLog;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.EmailService;
import com.bosco.stdata.tasks.ImportTask;
import com.bosco.stdata.utils.ImportHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class ImportApi {
    @Autowired
    ImportRepo importRepo;

    @Autowired 
    ImportTask importTask;

    @Autowired
    EmailService emailService;

    @Autowired 
    BoscoApi boscoApi;
    

    @Operation(
            summary = "Testing Bosco POST StData Student to Bosco ",
            description = "This will be removed soon.",
            tags = {"Testing"}
            )

    @GetMapping("/import/boscoPostStudent/{id}")
    public String bocoPostStudent(@PathVariable String id) {

        String res = boscoApi.postStudentToBosco(id);

        return res;
    }

    
    @Operation(
        summary = "Testing Bosco PUT StData Student to Bosco ",
        description = "This will be removed soon.",
        tags = {"Testing"}
        )

    @GetMapping("/import/boscoPutStudent/{id}")
    public String boscoPutStudent(@PathVariable String id) {

        String res = boscoApi.putStudentToBosco(id);

        return res;
    }

    @Operation(
            summary = "Testing Bosco Delete student from Bosco ",
            description = "This will be removed soon.",
            tags = {"Testing"}
            )

    @GetMapping("/import/boscoDeleteStudent/{id}")
    public String bocoDeleteStudent(@PathVariable String id) {

        String res = boscoApi.deleteStudentToBosco(id);

        return res;
    }

    
    @Operation(
            summary = "Testing Bosco get student 1000000.422576716 ",
            description = "This will be removed soon.",
            tags = {"Testing"}
            )

    @GetMapping("/import/boscoStudent")
    public String boscoStudent() {

        System.out.println("in testTbWeb");

        JsonNode resNode = boscoApi.getStudent("1000000.422576716");

        // we expect it is NOT an array

        // if (resNode.isArray()) {
        //     return "Got Array";
        // }
        // else {
        //     return "Not Array";
        // }

        String studentName = resNode.get("firstName").asText() + " " + resNode.get("lastName").asText();

        //return resNode.toPrettyString();
        
        return studentName;
    }
    
    @Operation(
            summary = "Testing Bosco web get Strudents",
            description = "This will be removed soon.",
            tags = {"Testing"}
            )
    @GetMapping("/import/boscoStudents")
    public String boscoStudents() {

        System.out.println("in testTbWeb");

        // So we get  all the pages
        int pageNumber = 0;
        Boolean done = false;

        String results = "Students: \n\n";


        while (!done) {
            JsonNode resNode = boscoApi.getStudents(pageNumber);
            if (resNode.size() > 0) {

                if (resNode.isArray()) {
                    System.out.println("Getting students page: " + pageNumber);
            
                    ArrayNode arrayNode = (ArrayNode) resNode;

                    for (JsonNode studentNode: arrayNode) {
                        results += studentNode.get("firstName").asText() + " " + studentNode.get("lastName").asText() + "\n";
                    }

                    pageNumber++;
                
                }
                else {
                    done = true;
                    results = "NOT ARRAY";

                }

            }
            else {
                done = true;
            }

            
        }
        return results;


        
    }
    


    @Operation(
        summary = "Run Imports NOW",
        description = "Email will be sent if sendEmail is true. This will kick off the imports.  Please check Definitions to see what is active",
        tags = {"Import Testing"}
        )
    @GetMapping("/import/runImport/{sendEmail}")
    public String runImportNow(@PathVariable Boolean sendEmail) {
        //ImportTask importTask = new ImportTask();

        String result = importTask.doImports(sendEmail);

        return result;

    }

     @Operation(
        summary = "Run Skyward SpecialEducationEnrollmentTX  test ",
        description = "Testing",
        tags = {"Import Testing"}
        )
    @GetMapping("/import/runImportSpEd/{sendEmail}")
    public String runImportSpEdNow(@PathVariable Boolean sendEmail) {
        //ImportTask importTask = new ImportTask();


        

        String result = importTask.doSkywardSpedTest();

        return result;

    }


  

    
    @Operation(
        summary = "Check Import difs testing",
        description = "This will be the check for % changes",
        tags = {"Import Testing"}
        )
    @GetMapping("/import/testChanges/{importId}/{baseImportId}")
    public String runImportNow(@PathVariable int importId, @PathVariable int baseImportId) {
        
        ImportChanges ic = importRepo.importChangesFromBase(importId, baseImportId);

        

        return "Base Count: " + ic.baseStudentCount + " St Count: " + ic.importStudentCount + " Changed: " + ic.importStudentChanged;

    }


    @Operation(
            summary = "Get Logs for an import",
            description = "For now, this just sends html back.",
            tags = {"Import Defs"}
            )
    
    @GetMapping("/import/getLogsHTML/{importId}")
    public String getLogsHTML(@PathVariable int importId) {
        List<ImportLog> logs = importRepo.getInfoLogs(importId);

        String html = "<html>";
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


        html += "</html>";

        return html;

        
    }


     @Operation(
            summary = "Get All Import Definitions",
            description = "Get the status etc. of import definitions",
            tags = {"Import Defs"}
            )

    @GetMapping("/import/importDefs")
    public List<ImportDefinition> importDefs() {
        

        List<ImportDefinition> defs = importRepo.getAllImportDefinitions();

        return defs;

        //return "TODO Get defs";
    }
    


    
    @Operation(
            summary = "Turn on or off Import",
            description = "BoscoClasslinkOneRosterApi, BoscoSkywardApi, CelinaFiles, ClintSkywardApi, SpringtownOneRosterApi, TestFiles, UpliftFiles",
            tags = {"Import Defs"}
            )
    @GetMapping("/import/setImportDef/{id}/{active}")
    public String setImportDef(@PathVariable String id, @PathVariable Boolean active) {

        importRepo.setImportDefActive(id, active);

        if (active) {
            return "Set Import Def for " + id + " to Active";
        }
        else {
            return "Set Import Def for " + id + " to Disabled";
        }



    }

    
    
     @Operation(
            summary = "Just testing running a thread",
            description = "This will be removed soon.",
            tags = {"Testing"}
            )

    @GetMapping("/import/runTaskTEST")
    public String runTask() {
         Thread taskThread = new Thread(() -> {
            System.out.println("Starting manual task...");
            try {
                Thread.sleep(14000);
                System.out.println("Manual task finished.");
                importRepo.setSystemStatus("Import", 0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Manual task was interrupted.");
                importRepo.setSystemStatus("Import", 0);

            }
        });

        int status = importRepo.getSystemStatus("Import");
        if (status > 0) {
            return "Imports Are running -- Bailing";

        }
        else {

            importRepo.setSystemStatus("Import", 1);
            taskThread.start();
            return "Task started";
        }
    }


      @Operation(
            summary = "Just testing sending an email",
            description = "This will be removed soon.",
            tags = {"Testing"}
            )

      @GetMapping("/import/testEmail")
    public String getMethodName() {
        emailService.sendSimpleMessage("benlevy3@gmail.com", "Tesing the email in bosco-stdata with props", "This is the email we are sending with props");
        return "Sent";
    }
    

    

}
