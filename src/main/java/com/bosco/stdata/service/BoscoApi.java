package com.bosco.stdata.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.model.*;
import com.bosco.stdata.repo.ImportRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BoscoApi {

    private final AppConfig appConfig;

    private final ImportRepo importRepo;
    private final EmailService emailService;
    
    private final BoscoClient boscoClient;
    

    private final int PAGE_SIZE = 500;

    @Value("${bosco.api.instance}")
    private String boscoInstance;       // we only use this to get the students from bosco.

    @Value("${bosco.api.authUrl}")
    private String authUrl;

    @Value("${bosco.api.baseUrl}")
    private String baseUrl;

    @Value("${bosco.api.baseUrlSisData}")
    private String baseUrlSisData;

    @Value("${bosco.api.clientId}")
    private String clientId;

    @Value("${bosco.api.clientSecret}")
    private String clientSecret;
        

    BoscoApi(ImportRepo importRepo, EmailService emailService,  BoscoClient boscoClient, AppConfig appConfig) {
        this.importRepo = importRepo;
        this.emailService = emailService;
        
        this.boscoClient = boscoClient;
        
        this.appConfig = appConfig;
        
        
        
    }




    public String postSisDataToBosco (String id) {

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


        String token = authBosco();

        String postUrl = baseUrlSisData + "sis-data/{id}/import-sis-data";

           
        String res;
        try {

            res = boscoClient.postSisData(postUrl, token, id, sd);

        } catch (Exception e) {
            // TODO Auto-generated catch block

            System.out.println(e.getMessage());

            res = e.getMessage();
        }

           return res;
        
        
    }


    public String xpostSisDataToBosco (String id, SisStudentData sd) {
        String token = authBosco();

        String postUrl = baseUrlSisData + "sis-data/{id}/import-sis-data";

           
        String res;
        try {

            res = boscoClient.postSisData(postUrl, token, id, sd);

        } catch (Exception e) {
            // TODO Auto-generated catch block

            System.out.println(e.getMessage());

            res = e.getMessage();
        }

           return res;
    }






    public String postStudentToBosco (String id) {
           String token = authBosco();

           String postUrl = baseUrl + "students";

            System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");
        int districId = Integer.parseInt(params[0]);

        int importId = importRepo.getBaseImportForDistrict(districId);



        Student bs = importRepo.studentBoscoForExport(importId, params[1]);

        bs.setGuardians(importRepo.guardiansBoscoForStudent(importId, bs.getStudentId()));
        bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(importId, bs.getStudentId()));

        String res;
        try {
            res = boscoClient.postStudent(postUrl, token, bs);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            res = e.getMessage();
        }

           return res;
    }



    public String putStudentToBosco (String id) {
        String token = authBosco();
        
        String postUrl = baseUrl + "students/{id}";

            System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");
        int districId = Integer.parseInt(params[0]);

        int importId = importRepo.getBaseImportForDistrict(districId);



        Student bs = importRepo.studentBoscoForExport(importId, params[1]);

        bs.setGuardians(importRepo.guardiansBoscoForStudent(importId, bs.getStudentId()));
        bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(importId, bs.getStudentId()));

        String res;
        try {
            res = boscoClient.putStudent(postUrl, token, bs);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            res = e.getMessage();
        }

        return res;
    }

    public String deleteStudentToBosco (String id) {
        String token = authBosco();
        
        String postUrl = baseUrl + "students/{id}";

        String res;
        try {
            res = boscoClient.deleteStudent(postUrl, token, id);
        } catch (Exception e) {
            // TODO Auto-generated catch block
             res = e.getMessage();
        }

      
        return res;
    }

    public JsonNode getStudent (String id) {
       

        String token = authBosco();
        // Now we test sending this:

        String getUrl = baseUrl + "students/" + id;

        JsonNode resNode = null;
        try {
            resNode = boscoClient.get(getUrl, token );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return resNode;
    }

    public JsonNode getStudents (int districtId, int pageNumber) {
       

        String token = authBosco();
        // Now we test sending this:

        
        String getUrl = baseUrl + "students?page=" + pageNumber + "&size=" + PAGE_SIZE + "&districtId=" + districtId;

        JsonNode resNode = null;
        try {
            resNode = boscoClient.get(getUrl, token );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return resNode;
    }

    private String authBosco() {
        String token = boscoClient.getAccessToken(clientId, clientSecret, authUrl);
        return token;
    }


    public Boolean sendImportToBosco (int importId, int baseImportId) throws Exception {


        // for logging if run from api

        importRepo.setImportId(importId);
        
        

        String token = authBosco();

        // for now we just write to a file
        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

        String outFileName = "c:/test/importLog/bosco_sync" + dateFolder + ".txt";

        System.out.println("Generating log file : " + outFileName);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();


        // if there are any new or changed schools, we will simply send an email.

        String newSchools = "<ul>";
        Boolean areNewSchools = false;

        int newStudents = 0;
        int changedStudents = 0;
        int deletedStudents = 0;
        int newTeachers = 0;
        int changedTeachers = 0;
        int deletedTeachers = 0;





                   // this will send all the imported stuff to bosco

            List<School> schools = importRepo.schoolsBoscoForExport(importId, 2);
            for (School bs : schools) {
                areNewSchools = true;
                newSchools += "<li> NEW: " + bs.getSchoolCode() + " - " + bs.getName() + "</li>\n";

                importRepo.logInfo("There is a new shool: " + bs.getSchoolCode() + " - " + bs.getName());
            }

            schools = importRepo.schoolsBoscoForExport(importId, 1);
            for (School bs : schools) {
                areNewSchools = true;
                newSchools += "<li> CHANGED: " + bs.getSchoolCode() + " - " + bs.getName() + "</li>\n";


                importRepo.logInfo("There is a changed shool: " + bs.getSchoolCode() + " - " + bs.getName());
            }


            System.out.println("------------ NEW TEACHERS ----------------\n");

            List<Teacher> teachers = importRepo.teacherBoscoGetForExport(importId, 2);
            for (Teacher bu : teachers) {

                newTeachers++;
                boscoClient.postTeacher(baseUrl + "users", token, bu);
            }

            System.out.println("------------ CHANGED TEACHERS ----------------\n");
            teachers = importRepo.teacherBoscoGetForExport(importId, 1);
            for (Teacher bu : teachers) {

                changedTeachers++;
                //boscoClient.putTeacher(baseUrl + "users/{id}", token, bu);
                boscoClient.putTeacher(baseUrl + "users/updateInfo/{id}", token, bu);
            }
            

            System.out.println("------------ NEW STUDENTS ----------------\n");
            List<Student> bss = importRepo.studentsBoscoForExport(importId, 2);

            for (Student bs : bss) {

                
                bs.setGuardians(importRepo.guardiansBoscoForStudent(importId, bs.getStudentId()));
                bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(importId, bs.getStudentId()));

                // make this pretty json and log.

                newStudents++;
                boscoClient.postStudent(baseUrl + "students", token, bs);


            }


            System.out.println("------------ CHANGED STUDENTS ----------------\n");
            bss = importRepo.studentsBoscoForExport(importId, 1);

            for (Student bs : bss) {

                
                bs.setGuardians(importRepo.guardiansBoscoForStudent(importId, bs.getStudentId()));
                bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(importId, bs.getStudentId()));

                // make this pretty json and log.

                changedStudents++;
                boscoClient.putStudent(baseUrl + "students/{id}", token, bs);


            }

            

            // if baseImportId = 0 then we have no deleted.

            if (baseImportId != 0) {


                List<String> dss;

                System.out.println("------------ DELETED SCHOOLS ----------------\n");
                System.out.println("    *** NOT YET IMPLEMENTS \n");
                
                //  ** WE NEED TO SORT THIS OUT!! ***
                System.out.println("------------ DELETED TEACHERS ----------------\n");

                dss = importRepo.teacherIdsDeletedFromImport(importId, baseImportId);
                for (String ds : dss) {
                    // TO CHECK
                    boscoClient.deleteTeacher(baseUrl + "users/{id}", token, ds);
                    deletedTeachers++;
                }

                System.out.println("------------ DELETED STUDENTS ----------------\n");

                dss = importRepo.studentIdsDeletedFromImport(importId, baseImportId);
                for (String ds : dss) {
                    //THIS IS FAILING
                    boscoClient.deleteStudent(baseUrl + "students/{id}", token, ds);
                    deletedStudents++;
                }

            }

            System.out.println("---------------------------------\n");
            
        

            newSchools += "</ul>";

            // log coungs.
            importRepo.logInfo("New Students: " + newStudents);
            importRepo.logInfo("Changed Students: " + changedStudents);
            importRepo.logInfo("Deleted Students: " + deletedStudents);
            importRepo.logInfo("New Teachers: " + newTeachers);
            importRepo.logInfo("Changed Teachers: " + changedTeachers);
            importRepo.logInfo("Deleted Teachers: " + deletedTeachers);


            if (areNewSchools) {
                System.out.println("Sending new or changed schools email");
                emailService.sendSimpleMessage("BenLevy3@gmail.com",  "New or Changed Schools", newSchools);
            }


        return true;

    }

}
