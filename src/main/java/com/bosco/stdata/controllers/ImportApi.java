package com.bosco.stdata.controllers;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.bosco.stdata.config.AppConfig;

import com.bosco.stdata.model.ImportDefinition;
import com.bosco.stdata.model.ImportLog;
import com.bosco.stdata.model.SisDiscipline;
import com.bosco.stdata.model.SisDisciplineCounts;
import com.bosco.stdata.model.SisDisciplineHelper;
import com.bosco.stdata.model.SisStudentData;
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

    private final AppConfig appConfig;
    @Autowired
    ImportRepo importRepo;

    @Autowired 
    ImportTask importTask;

    @Autowired
    EmailService emailService;

    @Autowired 
    BoscoApi boscoApi;

    
    @Value("${bosco.api.instance}")
    private String boscoInstance;

    ImportApi(AppConfig appConfig) {
        this.appConfig = appConfig;
    }       // we only use this to get the students from bosco.
    

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


    private void getBoscoStudents (int districtId) {

        System.out.println("in testTbWeb");

        // So we get  all the pages
        int pageNumber = 0;
        Boolean done = false;

        String results = "Students: " + boscoInstance + " \n\n";
        


        while (!done) {
            JsonNode resNode = boscoApi.getStudents(districtId, pageNumber);

            if (resNode.size() > 0) {

                if (resNode.isArray()) {
                    System.out.println("Getting students page: " + pageNumber);
            
                    ArrayNode arrayNode = (ArrayNode) resNode;

                    for (JsonNode studentNode: arrayNode) {


                        // So here we can actually save it to the importRepo
                        // we know the boscoInstance

                        // we want studentId 

                        importRepo.boscoStudentAdd(boscoInstance, districtId, studentNode.get("id").asText(), studentNode.get("studentId").asText());

                        //results += studentNode.get("id").asText() + "  - " + studentNode.get("firstName").asText() + " " + studentNode.get("lastName").asText() + "\n";
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

        System.out.println("DONE");
        //return results;

    }
    
    @Operation(
            summary = "Testing Bosco web get Students *** CHECK boscoInstance *** " ,
            description = "This will be removed soon.",
            tags = {"Testing"}
            )
    @GetMapping("/import/boscoStudents/{id}")
    public String boscoStudents(@PathVariable int id) {


         Thread taskThread = new Thread(() -> {
               
                getBoscoStudents(id);
            });


            ImportHelper.importRunning = true;
            //importRepo.setSystemStatus("Import", 1);
            taskThread.start();

            return "Getting Students";


        


        
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
        summary = "Send Import To Bosco",
        description = "This will jsut do the send to bosco (AGAIN)",
        tags = {"Import Testing"}
        )
    @GetMapping("/import/sendToBosco/{districtId}")
    public String sendToBosco(@PathVariable int districtId) {

        Thread taskThread = new Thread(() -> {
            
            importTask.sendImportToBoscoNow(districtId);
        });


        ImportHelper.importRunning = true;
        //importRepo.setSystemStatus("Import", 1);
        taskThread.start();

        return "Sending Students";


        



    }


    private void doSendAllSisDataForDistrict (int districId) {

        LocalDateTime startDateTime = LocalDateTime.now();
         List<String> studentIds = importRepo.studentIdsForDistrict (districId);

        int count = 0;
        // nwo we can call
        for (String studentId : studentIds) {

            System.out.println(studentId);

            boscoApi.postSisDataToBosco(studentId);
            count++;
        }



            LocalDateTime endDateTime = LocalDateTime.now();
    
            Duration duration = Duration.between(startDateTime, endDateTime);


        System.out.println("DONE : Sent " + count + "  Took " + duration.toMinutes() + " Minutes");

    }


    @Operation(
        summary = "This will send the Sis Data to bosco-web",
        description = "Send the full student sis data to bosco-web",
        tags = {"Import Testing"}
        )
    @GetMapping("/import/sendAllSisDataForDistrict/{id}")
    public String sendAllSisDataForDistrict(@PathVariable int id) {


        
         Thread taskThread = new Thread(() -> {
               
                doSendAllSisDataForDistrict(id);
            });


            ImportHelper.importRunning = true;
            //importRepo.setSystemStatus("Import", 1);
            taskThread.start();

            return "Sending Sis data to bosco";

        

       

        
    }


    @Operation(
        summary = "This will send the Sis Data to bosco-web",
        description = "Send the full student sis data to bosco-web",
        tags = {"Import Testing"}
        )
    @GetMapping("/import/sendSisDataToBosco/{id}")
    public String sendSisDataToBosco(@PathVariable String id) {


        boscoApi.postSisDataToBosco(id);

        // System.out.println("Param: " + id);
        // // id will be 66.838101615
        // String [] params = id.split("\\.");

        // //var x = params[0];

        // System.out.println("District: " + params[0] + "  - Student : " + params[1]);

        // int districId = Integer.parseInt(params[0]);


        // SisStudentData sd = new SisStudentData();

        
        // sd.grades = importRepo.sisGradesGet(districId, id);
        // // Grades is missing csacode;

        // sd.map = importRepo.sisMapsGet(districId, id);
        // // map is missing proficiencyCode and csacode
        // sd.mclass = importRepo.sisMclassGet(districId, id);
        // // mclass is missing proficiencyCode and csacode

        // sd.staar = importRepo.sisStaarsGet(districId, id);
        // // star is missing code, proficiencyCode and csacode

        // sd.telpas = importRepo.sisTelpasGet(districId, id);
        


        // // discipline is missing grade.

        // List<SisDisciplineHelper> sisDisciplineHelpers = new ArrayList<>();
        // sisDisciplineHelpers = importRepo.sisDisciplinesGet(districId, id);
        // //** this we have to build classes from the results.
        // for (SisDisciplineHelper sdh : sisDisciplineHelpers) {
        //     SisDiscipline dis = new SisDiscipline();
            
        //     dis.schoolYear = sdh.schoolYear;
        //     dis.grade = sdh.grade;
        //     dis.counts = new SisDisciplineCounts();
        //     if (!sdh.issDays.trim().equals(""))
        //         dis.counts.setISS(Integer.parseInt(sdh.issDays));
        //     if (!sdh.ossDays.trim().equals(""))
        //         dis.counts.setOSS(Integer.parseInt(sdh.ossDays));
        //     if (!sdh.aepDays.trim().equals(""))
        //         dis.counts.setDAEP(Integer.parseInt(sdh.aepDays));



        //     //sd.academicRecords.discipline.records.add(dis);
        //     sd.discipline.add(dis);
        // }
        
        
        // boscoApi.postSisDataToBosco(id, sd);



        return "Ok";
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

    
    
    //  @Operation(
    //         summary = "Just testing running a thread",
    //         description = "This will be removed soon.",
    //         tags = {"Testing"}
    //         )

    // @GetMapping("/import/runTaskTEST")
    // public String runTask() {
    //      Thread taskThread = new Thread(() -> {
    //         System.out.println("Starting manual task...");
    //         try {
    //             Thread.sleep(14000);
    //             System.out.println("Manual task finished.");
    //             ImportHelper.importRunning = false;
    //             //importRepo.setSystemStatus("Import", 0);
    //         } catch (InterruptedException e) {
    //             Thread.currentThread().interrupt();
    //             System.out.println("Manual task was interrupted.");
    //             ImportHelper.importRunning = false;

    //             //importRepo.setSystemStatus("Import", 0);

    //         }
    //     });

    //     if (ImportHelper.importRunning) {
    //         return "Imports are running -- bail";

    //     }
    //     else {
    //         ImportHelper.importRunning = true;
    //         taskThread.start();
    //         return "Started Task";


    //     }

        // int status = importRepo.getSystemStatus("Import");
        // if (status > 0) {
        //     return "Imports Are running -- Bailing";

        // }
        // else {

        //     importRepo.setSystemStatus("Import", 1);

        //     taskThread.start();
        //     return "Task started";
        // }
    // }


    //   @Operation(
    //         summary = "Just testing sending an email",
    //         description = "This will be removed soon.",
    //         tags = {"Testing"}
    //         )

    //   @GetMapping("/import/testEmail")
    // public String getMethodName() {
    //     emailService.sendSimpleMessage("benlevy3@gmail.com", "Tesing the email in bosco-stdata with props", "This is the email we are sending with props");
    //     return "Sent";
    // }
    

    

}
