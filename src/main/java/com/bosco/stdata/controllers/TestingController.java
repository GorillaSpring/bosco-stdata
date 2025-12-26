package com.bosco.stdata.controllers;
import org.springframework.web.bind.annotation.RestController;

import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.EmailService;
import com.bosco.stdata.tasks.ImportTask;
import com.bosco.stdata.utils.ImportHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

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

import com.bosco.stdata.model.*; 

@RestController
@RequestMapping("/import/api/testing")
@Tag(name = "Import API System TESTING (TestingController)", description = "Api for TESTING the Import System")
public class TestingController {


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
            summary = "Testing for DEPLOY? " ,
            description = "This will just be for testing during inital install."
            
            )
    @GetMapping("/test1")
    public String boscoStudents() {


        return "Test 1 successfull " + boscoInstance;

        


        
    }


    
  

    private void getBoscoStudents (int districtId) {

        System.out.println("in testTbWeb");

        // So we get  all the pages
        int pageNumber = 0;
        Boolean done = false;

        String results = "Students: " + boscoInstance + " \n\n";
        


        while (!done) {
            JsonNode resNode = boscoApi.getStudents(districtId, pageNumber);

            if (resNode.size() > 0) {

                if (resNode.isArray()) {
                    System.out.println("Getting students page: " + pageNumber);
            
                    ArrayNode arrayNode = (ArrayNode) resNode;

                    for (JsonNode studentNode: arrayNode) {


                        // So here we can actually save it to the importRepo
                        // we know the boscoInstance

                        // we want studentId 

                        if (Boolean.parseBoolean(studentNode.get("active").asText())) {

                            importRepo.boscoStudentAdd(districtId, studentNode.get("id").asText(), studentNode.get("studentId").asText());
                        }
                        else {
                            importRepo.boscoStudentRemove(districtId, studentNode.get("id").asText(), studentNode.get("studentId").asText());

                        }

                        //results += studentNode.get("id").asText() + "  - " + studentNode.get("firstName").asText() + " " + studentNode.get("lastName").asText() + "\n";
                    }

                    pageNumber++;
                
                }
                else {
                    done = true;
                    results = "NOT ARRAY";

                }

            }
            else {
                done = true;
            }

            
        }

        System.out.println("DONE");
        //return results;

    }
    
    @Operation(
            summary = "Testing Bosco web get Students *** CHECK boscoInstance *** " ,
            description = "This will get all active students to allow us to check for any differences."
            
            )
    @GetMapping("/boscoStudents/{id}")
    public String boscoStudents(@PathVariable int id) {


         Thread taskThread = new Thread(() -> {
               
                getBoscoStudents(id);
            });


            ImportHelper.importRunning = true;
            //importRepo.setSystemStatus("Import", 1);
            taskThread.start();

            return "Getting Students " + boscoInstance;


        


        
    }



     private void getBoscoUsers (int districtId) {

        

        // So we get  all the pages
        int pageNumber = 0;
        Boolean done = false;

        String results = "Users: " + boscoInstance + " \n\n";
        


        while (!done) {

            System.out.println("Getting users page: " + pageNumber);
            JsonNode resNode = boscoApi.getUsers(districtId, pageNumber);

            if (resNode.size() > 0) {

                if (resNode.isArray()) {
                    
            
                    ArrayNode arrayNode = (ArrayNode) resNode;

                    for (JsonNode boscoUserNode: arrayNode) {


                        // So here we can actually save it to the importRepo
                        // we know the boscoInstance

                        // we want studentId 

                        if (Boolean.parseBoolean(boscoUserNode.get("active").asText())) {

                            importRepo.boscoUserAdd(districtId, boscoUserNode.get("id").asText(), boscoUserNode.get("role").asText(), boscoUserNode.get("email").asText());
                        }
                        else {
                            importRepo.boscoUserRemove(districtId, boscoUserNode.get("id").asText(), boscoUserNode.get("role").asText(), boscoUserNode.get("email").asText());

                        }

                        //results += studentNode.get("id").asText() + "  - " + studentNode.get("firstName").asText() + " " + studentNode.get("lastName").asText() + "\n";
                    }

                    pageNumber++;
                
                }
                else {
                    done = true;
                    results = "NOT ARRAY";

                }

            }
            else {
                done = true;
            }

            
        }

        System.out.println("DONE");
        //return results;

    }
    


     @Operation(
            summary = "Testing Bosco web get USERS *** CHECK boscoInstance *** " ,
            description = "This allow us to get all active users to allow us to check for any differences."
            
            )
    @GetMapping("/boscoUsers/{id}")
    public String boscoUsers(@PathVariable int id) {


         Thread taskThread = new Thread(() -> {
               
                getBoscoUsers(id);
            });


            ImportHelper.importRunning = true;
            //importRepo.setSystemStatus("Import", 1);
            taskThread.start();

            return "Getting Users " + boscoInstance;


        


        
    }
    

    @Operation(
        summary = "Testing Bosco PUT StData Student to Bosco ",
        description = "This will be removed soon."
       
        )

    @GetMapping("/boscoPutStudent/{id}")
    public String boscoPutStudent(@PathVariable String id) {

        String res = boscoApi.putStudentToBosco(id);

        return res;
    }

    @Operation(
            summary = "Testing Bosco Delete student from Bosco ",
            description = "This will be removed soon."
            
            )

    @GetMapping("/boscoDeleteStudent/{id}")
    public String bocoDeleteStudent(@PathVariable String id) {

        

        String res = boscoApi.deleteStudentToBosco(id);

        return res;
    }



    
    @Operation(
        summary = "SIS Data that will be sent to Bosco",
        description = "This is the data that will be sent to Bosco on Register AND when new SIS data is imported"
            )

    @GetMapping("/getSisData/{id}")
    public SisStudentData getSisData(@PathVariable String id) {

        // This should be the same as the post, but without sending it!

        System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");

        //var x = params[0];

        System.out.println("District: " + params[0] + "  - Student : " + params[1]);

        int districId = Integer.parseInt(params[0]);


        SisStudentData sd = new SisStudentData();

        
        sd.grades = importRepo.sisGradesGet( id);
        // Grades is missing csacode;

        sd.map = importRepo.sisMapsGet(id);
        // map is missing proficiencyCode and csacode
        sd.mclass = importRepo.sisMclassGet( id);
        // mclass is missing proficiencyCode and csacode

        sd.staar = importRepo.sisStaarsGet( id);
        // star is missing code, proficiencyCode and csacode

        sd.telpas = importRepo.sisTelpasGet( id);
        


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
                dis.counts.setISS(sdh.issDays);
            if (!sdh.ossDays.trim().equals(""))
                dis.counts.setOSS(sdh.ossDays);
            if (!sdh.aepDays.trim().equals(""))
                dis.counts.setDAEP(sdh.aepDays);



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
        description = "This is the student data that will be sent to Bosco when they are NEW or CHANGED in the import"
            )

     @GetMapping("/getStudent/{id}")
    public Student getStudentById(@PathVariable String id) {


        System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");
        int districId = Integer.parseInt(params[0]);

        //int importId = importRepo.getBaseImportForDistrict(districId);



        Student bs = importRepo.studentBoscoForExport(id);

        bs.setGuardians(importRepo.guardiansBoscoForStudent(bs.getId()));
        bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(bs.getId()));





        return bs;
    }


}
