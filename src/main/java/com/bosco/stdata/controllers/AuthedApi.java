package com.bosco.stdata.controllers;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.model.ApiResult;
import com.bosco.stdata.model.ImportDefinition;
import com.bosco.stdata.model.ImportLog;
// import com.bosco.stdata.model.SisDiscipline;
// import com.bosco.stdata.model.SisDisciplineCounts;
// import com.bosco.stdata.model.SisDisciplineHelper;
// import com.bosco.stdata.model.SisStudentData;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.EmailService;
import com.bosco.stdata.tasks.ImportTask;
import com.bosco.stdata.utils.ImportHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
// @RequestMapping("/bosco/api/students")
@CrossOrigin(origins = "*")
@Tag(name = "Auth Testing (AuthedApi)", description = "APIs for managing student data")
@SecurityRequirement(name = "oauth2")
public class AuthedApi {

    private final AppConfig appConfig;
    @Autowired
    ImportRepo importRepo;

    @Autowired 
    ImportTask importTask;

    @Autowired
    EmailService emailService;

    @Autowired 
    BoscoApi boscoApi;

    private static Logger logger = Logger.getLogger(AuthedApi.class.getName());

    
    @Value("${bosco.api.instance}")
    private String boscoInstance;

    AuthedApi(AppConfig appConfig) {
        this.appConfig = appConfig;
    }       // we only use this to get the students from bosco.
    

    // @Operation(
    //         summary = "Testing Bosco POST StData Student to Bosco ",
    //         description = "This will be removed soon.",
    //         tags = {"Testing"}
    //         )

    // @GetMapping("/import/boscoPostStudent/{id}")
    // public String bocoPostStudent(@PathVariable String id) {

    //     String res = boscoApi.postStudentToBosco(id);

    //     return res;
    // }

     @GetMapping("/authed")
    public String protectedResource() {
        return "This is a protected resource!";
    }
    
    @Operation(
        summary = "Testing Auth ",
        description = "This will be removed soon."
        
        )

    @GetMapping("/authed/test1/{id}")
    public String authedTest1(@PathVariable String id) {


        logger.log(Level.INFO, "This is a test log");

        //String res = boscoApi.putStudentToBosco(id);

        String res = "ALL GOOD";

        return res;
    }

       

}
