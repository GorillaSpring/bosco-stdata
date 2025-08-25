package com.bosco.stdata.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.tasks.ImportTask;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bosco.stdata.model.*; 


@RestController
public class StudentApi {
    @Autowired
    ImportRepo importRepo;

    @Autowired 
    ImportTask importTask;

    @GetMapping("getStudentsTest")
    public List<BoscoStudent> getStudentsTest() {
        System.out.println(("here"));


        List<BoscoStudent> sts = importRepo.boscoStudentsGet(4);

        return sts;
    }

    @GetMapping(value = "getStudentMap")
    public List<TestMap> getStudentMap(@RequestParam("id") String id) {


        System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");

        //var x = params[0];

        System.out.println("District: " + params[0] + "  - Student : " + params[1]);

        int districId = Integer.parseInt(params[0]);

        List<TestMap> mapsForStudent = importRepo.studentMapsGetForStudent(districId, params[1]);

        return mapsForStudent;
    }
    

    @GetMapping("/student/studentDataRegister")
    public String studentDataRegister(@RequestParam("id") String id) {


        System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");

        //var x = params[0];

        System.out.println("District: " + params[0] + "  - Student : " + params[1]);

        int districId = Integer.parseInt(params[0]);

        List<TestMap> mapsForStudent = importRepo.studentMapsGetForStudent(districId, params[1]);

        return "Registered";
    }
    
    @GetMapping("/student/studentDataUnRegister")
    public String studentDataUnRegister(@RequestParam("id") String id) {


        System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");

        //var x = params[0];

        System.out.println("District: " + params[0] + "  - Student : " + params[1]);

        int districId = Integer.parseInt(params[0]);

        List<TestMap> mapsForStudent = importRepo.studentMapsGetForStudent(districId, params[1]);

        return "Unregistered";
    }
    
    @GetMapping("/student/getSisData")
    public SisStudentData getSisData(@RequestParam("id") String id) {


        System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");

        //var x = params[0];

        System.out.println("District: " + params[0] + "  - Student : " + params[1]);

        int districId = Integer.parseInt(params[0]);


        SisStudentData sd = new SisStudentData();
        sd.id = id;

        sd.academicGrades = importRepo.sisAcademicGradesGet(districId, id);
        sd.maps = importRepo.sisMapsGet(districId, id);
        sd.mclasses = importRepo.sisMclassGet(districId, id);
        sd.staars = importRepo.sisStaarsGet(districId, id);
        sd.disciplines = importRepo.sisDisciplinesGet(districId, id);


        return sd;
    }

    
    

    @GetMapping("/runTask")
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
