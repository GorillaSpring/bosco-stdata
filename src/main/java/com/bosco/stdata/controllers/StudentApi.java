package com.bosco.stdata.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.tasks.ImportTask;

import io.swagger.v3.oas.annotations.Operation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.bosco.stdata.model.*; 


@RestController
public class StudentApi {
    @Autowired
    ImportRepo importRepo;

    @Autowired 
    ImportTask importTask;


    

    @Operation(
        summary = "Register a student for SIS data",
        description = "SIS data for this student will be sent SOON.  If any data changes during imports, it will be sent again",
        tags = {"Bosco Endpoints"}
        )


    @GetMapping("/student/studentDataRegister/{id}")
    public String studentDataRegister(@PathVariable String id) {


        System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");

        //var x = params[0];

        System.out.println("District: " + params[0] + "  - Student : " + params[1]);

        int districId = Integer.parseInt(params[0]);


        return "Registered";
    }
    
    @Operation(
        summary = "UnRegister a student for SIS data",
        description = "When data is no longer need for this student",
        tags = {"Bosco Endpoints"}
        )

    @GetMapping("/student/studentDataUnRegister/{id}")
    public String studentDataUnRegister(@PathVariable String id) {


        System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");

        //var x = params[0];

        System.out.println("District: " + params[0] + "  - Student : " + params[1]);

        int districId = Integer.parseInt(params[0]);


        return "Unregistered";
    }
    

    @Operation(
        summary = "SIS Data that will be sent to Bosco",
        description = "This is the data that will be sent to Bosco on Register AND when new SIS data is imported",
        tags = {"Bosco API Examples"}
            )

    @GetMapping("/student/getSisData/{id}")
    public SisStudentData getSisData(@PathVariable String id) {


        System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");

        //var x = params[0];

        System.out.println("District: " + params[0] + "  - Student : " + params[1]);

        int districId = Integer.parseInt(params[0]);


        SisStudentData sd = new SisStudentData();

        
        sd.grades = importRepo.sisGradesGet(districId, id);
        // Grades is missing csacode;

        sd.map = importRepo.sisMapsGet(districId, id);
        // map is missing proficiencyCode and csacode
        sd.mclass = importRepo.sisMclassGet(districId, id);
        // mclass is missing proficiencyCode and csacode

        sd.staar = importRepo.sisStaarsGet(districId, id);
        // star is missing code, proficiencyCode and csacode

        sd.telpas = importRepo.sisTelpasGet(districId, id);
        


        // discipline is missing grade.

        List<SisDisciplineHelper> sisDisciplineHelpers = new ArrayList<>();
        sisDisciplineHelpers = importRepo.sisDisciplinesGet(districId, id);
        //** this we have to build classes from the results.
        for (SisDisciplineHelper sdh : sisDisciplineHelpers) {
            SisDiscipline dis = new SisDiscipline();
            
            dis.schoolYear = sdh.schoolYear;
            dis.grade = sdh.grade;
            dis.counts = new SisDisciplineCounts();
            if (!sdh.issDays.trim().equals(""))
                dis.counts.setISS(Integer.parseInt(sdh.issDays));
            if (!sdh.ossDays.trim().equals(""))
                dis.counts.setOSS(Integer.parseInt(sdh.ossDays));
            if (!sdh.aepDays.trim().equals(""))
                dis.counts.setDAEP(Integer.parseInt(sdh.aepDays));



            //sd.academicRecords.discipline.records.add(dis);
            sd.discipline.add(dis);
        }
        
        

        // List<SisMap> maps = new ArrayList<>();

        // maps = importRepo.sisMapsGet(districId, id);

        //sd.academicRecords.add(maps);

        // sd.academicGrades = importRepo.sisAcademicGradesGet(districId, id);
        // sd.maps = importRepo.sisMapsGet(districId, id);
        // sd.mclasses = importRepo.sisMclassGet(districId, id);
        // sd.staars = importRepo.sisStaarsGet(districId, id);
        // sd.disciplines = importRepo.sisDisciplinesGet(districId, id);


        return sd;
    }

   

      @Operation(
        summary = "Student Data that will be sent to Bosco",
        description = "This is the student data that will be sent to Bosco when they are NEW or CHANGED in the import",
        tags = {"Bosco API Examples"}
            )

     @GetMapping("/student/getStudent/{id}")
    public Student getStudentById(@PathVariable String id) {


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
