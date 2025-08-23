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

}
