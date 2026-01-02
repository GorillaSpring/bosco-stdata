package com.bosco.stdata.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.EmailService;
import com.bosco.stdata.utils.ImportHelper;

import jakarta.el.ImportHandler;

@Component
public class ScheduledTasks {

    @Autowired
    ImportRepo importRepo;

    @Autowired
    EmailService emailService;

    @Autowired 
    ImportTask importTask;


    // @Scheduled(fixedRate = 600000) // 5 Secconds = 5000,  60 = 60000  10 minutes = 600000
    // public void reportCurrentTime() {

    //     // We will test sending an email.

        

    //     String emailBody = "This is the the test email.";

    //         // importResults.forEach(ir -> {
    //         //     if (ir.success) {
                    
    //         //         System.out.println("Import: " + ir.districtId + " ID: " + ir.importId);
    //         //     }
    //         //     else {
    //         //         System.out.println("FAILD Import:" + ir.districtId + " ID: " + ir.importId);
    //         //         System.out.println(ir.errorMessage);
    //         //     }

    //         // }); 

    //     Date date = new Date(System.currentTimeMillis());
    //     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Example format
    //     String formattedDate = sdf.format(date);


    //     System.out.println("Testing Schedule " + formattedDate);


    //     //emailService.sendSimpleMessage("BenLevy3@gmail.com",  "Import Testing - " + formattedDate, emailBody);


    //     // int status = importRepo.getSystemStatus("Import");
    //     // if (status > 0) {
    //     //     System.out.println("The imports are running so do nonthing in tick");

    //     // }
    //     // else {


    //     //     Date date = new Date(System.currentTimeMillis());
    //     //     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Example format
    //     //     String formattedDate = sdf.format(date);

    //     //     System.out.println("Tick: Current time: " + formattedDate);
    //     // }
    // }

    // @Scheduled(cron = "0 1 1 * * ?")   This should be 1:01 AM  EVERY DAY.

    //@Scheduled(cron = "0 0 10 * * MON-FRI") // Runs at 10 AM on weekdays
    
    @Scheduled(cron = "0 1 1 * * ?")
    public void dailyImports() {
        System.out.println("Starting Daily Imports...");

        
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Example format
        String formattedDate = sdf.format(date);


        System.out.println("Ruining Daily Imports at " + formattedDate);

        String emailBody = "Ruining Daily Imports at " + formattedDate;


        emailService.sendSimpleMessage("BenLevy3@gmail.com",  "Daily Imports Running Now - " + formattedDate, emailBody);


        // just to be sure.  Remove on next build;
        ImportHelper.importRunning = false;


        String result = importTask.doImports(true);

        System.out.println ("Running Daily Imports Now: " + result);

        


    }
}
