package com.bosco.stdata.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.tasks.ImportTask;

@RestController
public class ImportApi {
    @Autowired
    ImportRepo importRepo;

    @Autowired 
    ImportTask importTask;

    
    @GetMapping("/import/runImport")
    public String runImportNow() {
        //ImportTask importTask = new ImportTask();

        String result = importTask.doImports();

        return result;

    }


    
    

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
    

}
