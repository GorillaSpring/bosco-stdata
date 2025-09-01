package com.bosco.stdata.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
import java.util.List;

@Service
public class BoscoApi {

    private final ImportRepo importRepo;
    private final EmailService emailService;
    
    private final BoscoClient boscoClient;
    

    private final int PAGE_SIZE = 100;

    @Value("${bosco.api.authUrl}")
    private String authUrl;

    @Value("${bosco.api.baseUrl}")
    private String baseUrl;

    @Value("${bosco.api.clientId}")
    private String clientId;

    @Value("${bosco.api.clientSecret}")
    private String clientSecret;
        

    BoscoApi(ImportRepo importRepo, EmailService emailService,  BoscoClient boscoClient) {
        this.importRepo = importRepo;
        this.emailService = emailService;
        
        this.boscoClient = boscoClient;
        
        
        
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

           String res = boscoClient.postStudent(postUrl, token, bs);

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

        String res = boscoClient.putStudent(postUrl, token, bs);

        return res;
    }

    public String deleteStudentToBosco (String id) {
        String token = authBosco();
        
        String postUrl = baseUrl + "students/{id}";

        String res = boscoClient.deleteStudent(postUrl, token, id);

      
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

    public JsonNode getStudents (int pageNumber) {
       

        String token = authBosco();
        // Now we test sending this:

        
        String getUrl = baseUrl + "students?page=" + pageNumber + "&size=" + PAGE_SIZE + "&active=true";

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


    public Boolean sendImportToBosco (int importId, int baseImportId) throws IOException {

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


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFileName)))
            {
                writer.write("Bosco Sync " + importId + "\n\n");

                if (baseImportId == 0) {
                    writer.write(" *******  NO BASE IMPORT - THIS IS FIRST LOAD *******\n\n");
                }


                   // this will send all the imported stuff to bosco
                writer.write("------------ NEW SCHOOLS ----------------\n");

                List<School> schools = importRepo.schoolsBoscoForExport(importId, 2);
                for (School bs : schools) {
                    areNewSchools = true;
                    newSchools += "<li> NEW: " + bs.getSchoolCode() + " - " + bs.getName() + "</li>\n";
                    writer.write(ow.writeValueAsString(bs));
                    writer.write("\n");

                    importRepo.logInfo("There is a new shool: " + bs.getSchoolCode() + " - " + bs.getName());
                }

                writer.write("------------ CHANGED SCHOOLS ----------------\n");
                schools = importRepo.schoolsBoscoForExport(importId, 1);
                for (School bs : schools) {
                    areNewSchools = true;
                    newSchools += "<li> CHANGED: " + bs.getSchoolCode() + " - " + bs.getName() + "</li>\n";

                    writer.write(ow.writeValueAsString(bs));
                    writer.write("\n");

                    importRepo.logInfo("There is a changed shool: " + bs.getSchoolCode() + " - " + bs.getName());
                }


                writer.write("------------ NEW TEACHERS ----------------\n");

                List<Teacher> teachers = importRepo.teacherBoscoGetForExport(importId, 2);
                for (Teacher bu : teachers) {
                    writer.write(ow.writeValueAsString(bu));
                    writer.write("\n");

                    newTeachers++;
                    boscoClient.postTeacher(baseUrl + "users", token, bu);
                }

                writer.write("------------ CHANGED TEACHERS ----------------\n");
                teachers = importRepo.teacherBoscoGetForExport(importId, 1);
                for (Teacher bu : teachers) {
                    writer.write(ow.writeValueAsString(bu));
                    writer.write("\n");

                    changedTeachers++;
                    boscoClient.putTeacher(baseUrl + "users/{id}", token, bu);
                }
                

                writer.write("------------ NEW STUDENTS ----------------\n");
                List<Student> bss = importRepo.studentsBoscoForExport(importId, 2);

                for (Student bs : bss) {

                    
                    bs.setGuardians(importRepo.guardiansBoscoForStudent(importId, bs.getStudentId()));
                    bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(importId, bs.getStudentId()));

                    // make this pretty json and log.

                    writer.write(ow.writeValueAsString(bs));
                    writer.write("\n");
                    newStudents++;
                    boscoClient.postStudent(baseUrl + "students", token, bs);


                }


                writer.write("------------ CHANGED STUDENTS ----------------\n");
                bss = importRepo.studentsBoscoForExport(importId, 1);

                for (Student bs : bss) {

                    
                    bs.setGuardians(importRepo.guardiansBoscoForStudent(importId, bs.getStudentId()));
                    bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(importId, bs.getStudentId()));

                    // make this pretty json and log.

                    writer.write(ow.writeValueAsString(bs));
                    writer.write("\n");

                    changedStudents++;
                    boscoClient.putStudent(baseUrl + "students/{id}", token, bs);


                }

                

                // if baseImportId = 0 then we have no deleted.

                if (baseImportId != 0) {


                    List<String> dss;

                    writer.write("------------ DELETED SCHOOLS ----------------\n");
                    writer.write("    *** NOT YET IMPLEMENTS \n");
                    
                    //  ** WE NEED TO SORT THIS OUT!! ***
                    writer.write("------------ DELETED TEACHERS ----------------\n");

                    dss = importRepo.teacherIdsDeletedFromImport(importId, baseImportId);
                    for (String ds : dss) {
                        writer.write(ds + "\n");
                        boscoClient.deleteStudent(baseUrl + "users/{id}", token, ds);
                        deletedTeachers++;
                    }

                    writer.write("------------ DELETED STUDENTS ----------------\n");

                    dss = importRepo.studentIdsDeletedFromImport(importId, baseImportId);
                    for (String ds : dss) {
                        writer.write(ds + "\n");
                        //THIS IS FAILING
                        //boscoClient.deleteStudent(baseUrl + "students/{id}", token, ds);
                        deletedStudents++;
                    }

                }

                writer.write("---------------------------------\n");
            }
        

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
