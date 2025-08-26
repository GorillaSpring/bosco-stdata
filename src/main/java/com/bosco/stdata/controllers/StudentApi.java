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


    

    @GetMapping("/student/studentDataRegister")
    public String studentDataRegister(@RequestParam("id") String id) {


        System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");

        //var x = params[0];

        System.out.println("District: " + params[0] + "  - Student : " + params[1]);

        int districId = Integer.parseInt(params[0]);


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

    @GetMapping("/student/getStudent")
    public Student getStudent(@RequestParam("id") String id) {


        System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");
        int districId = Integer.parseInt(params[0]);

        int importId = importRepo.getBaseImportForDistrict(districId);



        Student bs = importRepo.studentBoscoForExport(importId, params[1]);

        bs.setGuardians(importRepo.guardiansBoscoForStudent(importId, bs.getStudentId()));
        bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(importId, bs.getStudentId()));





        return bs;
    }


    

}
