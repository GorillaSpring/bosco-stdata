// package com.bosco.stdata.controllers;

// import java.io.FileNotFoundException;
// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import com.bosco.stdata.config.AppConfig;
// import com.bosco.stdata.model.ApiResult;
// import com.bosco.stdata.model.ImportDefinition;
// import com.bosco.stdata.model.ImportLog;
// // import com.bosco.stdata.model.SisDiscipline;
// // import com.bosco.stdata.model.SisDisciplineCounts;
// // import com.bosco.stdata.model.SisDisciplineHelper;
// // import com.bosco.stdata.model.SisStudentData;
// import com.bosco.stdata.repo.ImportRepo;
// import com.bosco.stdata.service.BoscoApi;
// import com.bosco.stdata.service.EmailService;
// import com.bosco.stdata.tasks.ImportTask;
// import com.bosco.stdata.utils.ImportHelper;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.node.ArrayNode;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.websocket.server.PathParam;

// import org.springframework.web.bind.annotation.RequestParam;


// @RestController
// @RequestMapping("/import/api")
// // Could add Bosco to this one for bosco api calls.
// @Tag(name = "Import API", description = "Api for import System")
// public class ImportApi {

//     private final AppConfig appConfig;
//     @Autowired
//     ImportRepo importRepo;

//     @Autowired 
//     ImportTask importTask;

//     @Autowired
//     EmailService emailService;

//     @Autowired 
//     BoscoApi boscoApi;

    
//     @Value("${bosco.api.instance}")
//     private String boscoInstance;

//     ImportApi(AppConfig appConfig) {
//         this.appConfig = appConfig;
//     }       // we only use this to get the students from bosco.
    

//     // @Operation(
//     //         summary = "Testing Bosco POST StData Student to Bosco ",
//     //         description = "This will be removed soon.",
//     //         tags = {"Testing"}
//     //         )

//     // @GetMapping("/import/boscoPostStudent/{id}")
//     // public String bocoPostStudent(@PathVariable String id) {

//     //     String res = boscoApi.postStudentToBosco(id);

//     //     return res;
//     // }

    
 
    

  


    
    
//     //  @Operation(
//     //         summary = "Just testing running a thread",
//     //         description = "This will be removed soon.",
//     //         tags = {"Testing"}
//     //         )

//     // @GetMapping("/import/runTaskTEST")
//     // public String runTask() {
//     //      Thread taskThread = new Thread(() -> {
//     //         System.out.println("Starting manual task...");
//     //         try {
//     //             Thread.sleep(14000);
//     //             System.out.println("Manual task finished.");
//     //             ImportHelper.importRunning = false;
//     //             //importRepo.setSystemStatus("Import", 0);
//     //         } catch (InterruptedException e) {
//     //             Thread.currentThread().interrupt();
//     //             System.out.println("Manual task was interrupted.");
//     //             ImportHelper.importRunning = false;

//     //             //importRepo.setSystemStatus("Import", 0);

//     //         }
//     //     });

//     //     if (ImportHelper.importRunning) {
//     //         return "Imports are running -- bail";

//     //     }
//     //     else {
//     //         ImportHelper.importRunning = true;
//     //         taskThread.start();
//     //         return "Started Task";


//     //     }

//         // int status = importRepo.getSystemStatus("Import");
//         // if (status > 0) {
//         //     return "Imports Are running -- Bailing";

//         // }
//         // else {

//         //     importRepo.setSystemStatus("Import", 1);

//         //     taskThread.start();
//         //     return "Task started";
//         // }
//     // }


//     //   @Operation(
//     //         summary = "Just testing sending an email",
//     //         description = "This will be removed soon.",
//     //         tags = {"Testing"}
//     //         )

//     //   @GetMapping("/import/testEmail")
//     // public String getMethodName() {
//     //     emailService.sendSimpleMessage("benlevy3@gmail.com", "Tesing the email in bosco-stdata with props", "This is the email we are sending with props");
//     //     return "Sent";
//     // }
    

    

// }
