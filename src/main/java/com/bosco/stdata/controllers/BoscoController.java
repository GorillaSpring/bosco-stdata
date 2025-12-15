package com.bosco.stdata.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.tasks.ImportTask;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bosco.stdata.model.*; 

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/import/api/bosco")
@Tag(name = "Import API for Bosco (BoscoController)", description = "Api for Bosco to interact with the Import System")
@SecurityRequirement(name = "oauth2")
public class BoscoController {
@Autowired
    ImportRepo importRepo;

    @Autowired 
    ImportTask importTask;

    @Autowired 
    BoscoApi boscoApi;

    

    @Operation(
        summary = "Register a student for SIS data",
        description = "SIS data for this student will be sent SOON.  If any data changes during imports, it will be sent again"
        
        )


    @GetMapping("/studentDataRegister/{id}")
    public String studentDataRegister(@PathVariable String id) {


        System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");

        //var x = params[0];

        System.out.println("District: " + params[0] + "  - Student : " + params[1]);

        int districId = Integer.parseInt(params[0]);

        importRepo.sisReferralAdd(id);

        // NOW we want to send the sis data if we have any.
        boscoApi.postSisDataToBosco(id);


        return "Registered";
    }
    
    @Operation(
        summary = "UnRegister a student for SIS data",
        description = "When data is no longer need for this student"
        )

    @GetMapping("/studentDataUnRegister/{id}")
    public String studentDataUnRegister(@PathVariable String id) {


        System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");

        //var x = params[0];

        System.out.println("District: " + params[0] + "  - Student : " + params[1]);

        int districId = Integer.parseInt(params[0]);

        importRepo.sisReferralDelete(id);


        return "Unregistered";
    }
    

    

}
