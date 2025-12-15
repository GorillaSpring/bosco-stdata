package com.bosco.stdata.controllers;
import org.springframework.web.bind.annotation.RestController;

import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.EmailService;
import com.bosco.stdata.tasks.ImportTask;
import com.bosco.stdata.utils.ImportHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bosco.stdata.model.*; 

@RestController
@RequestMapping("/import/api/system")
@Tag(name = "Import API System (SystemController)", description = "Api for the Import System")
public class SystemController {
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


    

    @Operation(
        summary = "Run Imports NOW",
        description = "Email will be sent if sendEmail is true. This will kick off the imports.  Please check Definitions to see what is active"
        
        )
    @GetMapping("/runImport/{sendEmail}")
    public String runImportNow(@PathVariable Boolean sendEmail) {
        //ImportTask importTask = new ImportTask();

        String result = importTask.doImports(sendEmail);

        return result;

    }


    

    private void doSendAllSisDataForDistrictOld (int districId) {

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
        description = "Send the full student sis data to bosco-web"
        
        )
    @GetMapping("/sendAllSisDataForDistrictOLD/{id}")
    public String sendAllSisDataForDistrictOLD(@PathVariable int id) {


        
         Thread taskThread = new Thread(() -> {
               
                doSendAllSisDataForDistrictOld(id);
            });


            
            //importRepo.setSystemStatus("Import", 1);
            taskThread.start();

            return "Sending Sis data to bosco";

        

       

        
    }

    // With student nuber starting
     private void doSendAllSisDataForDistrict (int districId, int studentNumber) {

        LocalDateTime startDateTime = LocalDateTime.now();
         List<String> studentIds = importRepo.studentIdsForDistrictGreaterThan(districId, studentNumber);

        int count = 0;
        // nwo we can call
        int countDown = 0;
        countDown = studentIds.size();
        for (String studentId : studentIds) {

            System.out.println(studentId  + " : " + countDown);
            countDown--;

            boscoApi.postSisDataToBosco(studentId);
            count++;
        }



            LocalDateTime endDateTime = LocalDateTime.now();
    
            Duration duration = Duration.between(startDateTime, endDateTime);


        System.out.println("DONE : Sent " + count + "  Took " + duration.toMinutes() + " Minutes");

    }


    @Operation(
        summary = "This will send the Sis Data to bosco-web",
        description = "Send the full student sis data to bosco-web"
        
        )
    @GetMapping("/sendAllSisDataForDistrict")
    public String sendAllSisDataForDistrict(@RequestParam int id, @RequestParam(required = false) String startStudentNumber) 
    
    {

        if (startStudentNumber == null || startStudentNumber.isEmpty()) {
            startStudentNumber = "0";
        }
        final String copy = startStudentNumber;

        
         Thread taskThread = new Thread(() -> {

            int studentNumber = Integer.parseInt(copy);
               
            doSendAllSisDataForDistrict(id, studentNumber);
        });


            
            //importRepo.setSystemStatus("Import", 1);
            taskThread.start();

            return "Sending Sis data to bosco";

        

       

        
    }

    
    @Operation(
            summary = "This will send SisData for all sis_students that are marked dirty in the district *** CHECK boscoInstance *** " ,
            description = "This will send SisData for all sis_students that are marked dirty in the district *** CHECK boscoInstance ***."
            
            )
    @GetMapping("/sendDirtySisDataForDistrict/{districtId}")
    public String sendDirtySisDataForDistrict(@PathVariable int districtId) {


         Thread taskThread = new Thread(() -> {

            // We get a list of them.
            List<String> refIds = importRepo.dirtyReferralsForDistrict(districtId);

            
            
            for (String refId : refIds) {
                boscoApi.postSisDataToBosco(refId);
                importRepo.markSisStudentClean(refId);
                
            }
            
        });


        ImportHelper.importRunning = true;
        //importRepo.setSystemStatus("Import", 1);
        taskThread.start();

        return "Getting Active Referrals " + boscoInstance;

        
    }



    
    @Operation(
        summary = "Send Import To Bosco",
        description = "This will jsut do the send to bosco (AGAIN)"
        
        )
    @GetMapping("/sendToBosco/{districtId}")
    public String sendToBosco(@PathVariable int districtId) {

        Thread taskThread = new Thread(() -> {
            
            importTask.sendImportToBoscoNow(districtId);
        });


        ImportHelper.importRunning = true;
        //importRepo.setSystemStatus("Import", 1);
        taskThread.start();

        return "Sending Students";


        



    }

    

    @Operation(
        summary = "This will send the Sis Data to bosco-web",
        description = "Send the full student sis data to bosco-web"
        
        )
    @GetMapping("/sendSisDataToBosco/{id}")
    public String sendSisDataToBosco(@PathVariable String id) {


        boscoApi.postSisDataToBosco(id);

       



        return "Ok";
    }
    


    

    
    @Operation(
            summary = "Get all Active Referrals from the District AND Send Dirty Sis Data. *** CHECK boscoInstance *** " ,
            description = "This will allow us to send SIS data for all active Referrals."
            )
    @GetMapping("/getAndUpdateActiveReferrals/{districtId}")
    public String getAndUpdateActiveReferrals(@PathVariable int districtId) {


         Thread taskThread = new Thread(() -> {
            
            var res = boscoApi.getReferralsForDistrict(districtId);

            if (res.success) {
            

                List<String> refIds = importRepo.dirtyReferralsForDistrict(districtId);

                System.out.println("Dirty Referrals in " + districtId + " : " + refIds.size());

                for (String refId : refIds) {
                    boscoApi.postSisDataToBosco(refId);
                    importRepo.markSisStudentClean(refId);
                    
                }
            }
            else {
                System.out.println("Get Active Referrals failed : " + res.errorMessage);
            }
            
        });


        ImportHelper.importRunning = true;
        //importRepo.setSystemStatus("Import", 1);
        taskThread.start();

        return "Getting Active Referrals And Sending dirty data " + boscoInstance;

        
    }

   @Operation(
            summary = "Get all Active Referrals from the District *** CHECK boscoInstance *** " ,
            description = "This will allow us to send SIS data for all active Referrals."
            )
    @GetMapping("/active-referrals/{id}")
    public String activeReferrals(@PathVariable int id) {


         Thread taskThread = new Thread(() -> {
            
            var res = boscoApi.getReferralsForDistrict(id);
            if (res.success) {
                System.out.println("Got Active Referrals");
            }
            else {
               System.out.println("Get Active Referrals failed : " + res.errorMessage);
            }
            
        });


        ImportHelper.importRunning = true;
        //importRepo.setSystemStatus("Import", 1);
        taskThread.start();

        return "Getting Active Referrals " + boscoInstance;

        
    }


   

    
    
}
