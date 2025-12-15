package com.bosco.stdata.controllers;
import org.springframework.web.bind.annotation.RestController;

import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.EmailService;
import com.bosco.stdata.tasks.ImportTask;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.model.*; 

@RestController
@RequestMapping("/import/api/admin")
@Tag(name = "Import API Admin (AdminController)", description = "Api for Administering the Import System")

public class AdminController {

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
            summary = "Get Import System Info",
            description = "Get the status etc. of import system"
            
            )

    @GetMapping("/importSystemInfo")
    public String importSystemInfo() {
        

        List<ImportDefinition> defs = importRepo.getAllImportDefinitions();

        String res = "Inport System\n\n";

        res += "  - Instance : " + boscoInstance + "\n\nActive Imports: \n";


        for (ImportDefinition id : defs) {
            if (id.getActive()) {
                res += " - " + id.getId() + " : " + id.getDistrictId() + "  ForceLoad: " + Boolean.toString(id.getForceLoad())  + "\n";
            }
        }

        res += "--------------------------------\n";


        return res;
        // return defs;

        //return "TODO Get defs";
    }
    


     @Operation(
            summary = "Get All Import Definitions",
            description = "Get the status etc. of import definitions"
            
            )

    @GetMapping("/importDefs")
    public List<ImportDefinition> importDefs() {
        

        List<ImportDefinition> defs = importRepo.getAllImportDefinitions();

        return defs;

        //return "TODO Get defs";
    }
    


    
    @Operation(
            summary = "Turn on or off Import",
            description = "BoscoClasslinkOneRosterApi, BoscoSkywardApi, CelinaFiles, ClintSkywardApi, SpringtownOneRosterApi, TestFiles, UpliftFiles"
            
            )
    @GetMapping("/setImportDef/{id}/{active}")
    public String setImportDef(@PathVariable String id, @PathVariable Boolean active) {

        importRepo.setImportDefActive(id, active);

        if (active) {
            return "Set Import Def for " + id + " to Active";
        }
        else {
            return "Set Import Def for " + id + " to Disabled";
        }



    }


    

    @Operation(
            summary = "Get Logs for an import",
            description = "For now, this just sends html back."
            
            )
    
    @GetMapping("/getLogsHTML/{importId}")
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



}
