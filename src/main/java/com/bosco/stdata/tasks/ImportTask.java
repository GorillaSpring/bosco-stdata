package com.bosco.stdata.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
                System.out.println("Starting import in doImport...");
                try {
                    Thread.sleep(14000);
                    System.out.println("----  import finished in doImport.");
                    importRepo.setSystemStatus("Import", 0);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println(" ---  import interupted in doImport.");
                    importRepo.setSystemStatus("Import", 0);

                }
            });


            importRepo.setSystemStatus("Import", 1);
            taskThread.start();

            return "Imports Running";

            //return "Task started";

        }


    }

    private void processImports() {
        System.out.println("Processing Imports");
    }

}
