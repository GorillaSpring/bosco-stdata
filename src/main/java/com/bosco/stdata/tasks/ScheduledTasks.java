package com.bosco.stdata.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.utils.ImportHelper;

import jakarta.el.ImportHandler;

@Component
public class ScheduledTasks {

    @Autowired
    ImportRepo importRepo;

    @Scheduled(fixedRate = 15000) // Runs every 5 seconds
    public void reportCurrentTime() {


        if (ImportHelper.importRunning) {
            System.out.println("Import Running - bail");
        }
        else {


            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Example format
            String formattedDate = sdf.format(date);

            System.out.println("Tick: Current time: " + formattedDate);
        }


        // int status = importRepo.getSystemStatus("Import");
        // if (status > 0) {
        //     System.out.println("The imports are running so do nonthing in tick");

        // }
        // else {


        //     Date date = new Date(System.currentTimeMillis());
        //     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Example format
        //     String formattedDate = sdf.format(date);

        //     System.out.println("Tick: Current time: " + formattedDate);
        // }
    }

    @Scheduled(cron = "0 0 10 * * MON-FRI") // Runs at 10 AM on weekdays
    public void dailyReport() {
        System.out.println("Generating daily report...");
    }
}
