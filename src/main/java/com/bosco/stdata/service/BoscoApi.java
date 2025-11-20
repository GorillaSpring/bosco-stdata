package com.bosco.stdata.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.model.*;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.utils.ImportHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import jakarta.el.ImportHandler;

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


        // first see if the student even exists in our local DB
        if (!importRepo.studentExists(id)) {
            System.out.println("DID NOT FIND STUDENT in import DB : " + id);
            return "DID NOT FIND STUDENT : " + id;
        }
        

        int districId = Integer.parseInt(params[0]);


        SisStudentData sd = new SisStudentData();

        
        sd.grades = importRepo.sisGradesGet(id);
        // Grades is missing csacode;

        sd.map = importRepo.sisMapsGet(id);
        // map is missing proficiencyCode and csacode
        sd.mclass = importRepo.sisMclassGet(id);
        // mclass is missing proficiencyCode and csacode

        sd.staar = importRepo.sisStaarsGet(id);
        // star is missing code, proficiencyCode and csacode

        sd.telpas = importRepo.sisTelpasGet(id);
        


        // discipline is missing grade.
        // Temp fix for Celina broken

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



        // do we bother sending if no data.

        if (!sd.grades.isEmpty()  || !sd.map.isEmpty() || !sd.mclass.isEmpty() || !sd.staar.isEmpty() || !sd.telpas.isEmpty() || !sd.discipline.isEmpty() )
        {

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
        
        else {
            System.out.println("NO SIS DATA FOR " + id);
            return "NO DATA";
        }
        
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






    // public String postStudentToBosco (String id) {
    //        String token = authBosco();

    //        String postUrl = baseUrl + "students";

    //         System.out.println("Param: " + id);
    //     // id will be 66.838101615
    //     String [] params = id.split("\\.");
    //     int districId = Integer.parseInt(params[0]);




    //     Student bs = importRepo.studentBoscoForExport(id);

    //     bs.setGuardians(importRepo.guardiansBoscoForStudent(bs.getId()));
    //     bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(bs.getId()));

    //     String res;
    //     try {
    //         res = boscoClient.postStudent(postUrl, token, bs);
    //     } catch (Exception e) {
    //         // TODO Auto-generated catch block
    //         res = e.getMessage();
    //     }

    //        return res;
    // }


    public ApiResult getReferralsForDistrict (int districtId) {
        String token = authBosco();

        String getUrl = baseUrl + "sis-data/active-referrals/" + districtId;

        ApiResult res = new ApiResult();

        

        try {
            List<String> refs = boscoClient.getActiveReferralsForDistrict(getUrl, token);

            res.success = true;
            res.message =  "Got " + refs.size();

            for (String ref : refs) {
                System.out.println ("Active Ref: " + ref);
                importRepo.sisReferralAdd(ref);
                
            }
            

        }
        catch (Exception ex) {
            res.success = false;
            res.errorMessage = ex.getMessage();

        }

        return res;

        
    }

    public String putStudentToBosco (String id) {



//   boscoClient.putStudent(baseUrl + "students/upsert/{id}", token, bs);

        //return "TO FIX";

        String token = authBosco();
        
        //String postUrl = baseUrl + "students/{id}";

        String postUrl = baseUrl + "students/upsert/{id}";

            System.out.println("Param: " + id);
        // id will be 66.838101615
        String [] params = id.split("\\.");
        int districId = Integer.parseInt(params[0]);

        //int importId = importRepo.getBaseImportForDistrict(districId);




        Student bs = importRepo.studentBoscoForExport(id);


    

        bs.setGuardians(importRepo.guardiansBoscoForStudent(id));
        bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(id));

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
        
        ApiResult res = boscoClient.deleteStudent2(baseUrl + "students/{id}", token, id);


        if (res.success) {
            return "DELETED";
        }
        else {
            return "ERROR: " + res.errorMessage;
        }
      
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


    

    public JsonNode getUsers (int districtId, int pageNumber) {
       

        String token = authBosco();
        // Now we test sending this:

        
        String getUrl = baseUrl + "users?page=" + pageNumber + "&size=" + PAGE_SIZE + "&organizationId=" + districtId;

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


    



     public Boolean sendImportToBosco (int districId) throws Exception {


        // for logging if run from api

        //importRepo.setImportId(importId);
        
        

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




        Boolean useOld = true;
        if (boscoInstance.equals("local"))
            useOld = false;

        // when test rolls in do this
        // if (boscoInstance != "prod")
        //     useOld = false;


                   // this will send all the imported stuff to bosco

            // List<School> schools = importRepo.schoolsBoscoForExport(importId, 2);
            // for (School bs : schools) {
            //     areNewSchools = true;
            //     newSchools += "<li> NEW: " + bs.getSchoolCode() + " - " + bs.getName() + "</li>\n";

            //     importRepo.logInfo("There is a new shool: " + bs.getSchoolCode() + " - " + bs.getName());
            // }

            // schools = importRepo.schoolsBoscoForExport(importId, 1);
            // for (School bs : schools) {
            //     areNewSchools = true;
            //     newSchools += "<li> CHANGED: " + bs.getSchoolCode() + " - " + bs.getName() + "</li>\n";


            //     importRepo.logInfo("There is a changed shool: " + bs.getSchoolCode() + " - " + bs.getName());
            // }


            

            System.out.println("------------ NEW TEACHERS ----------------\n");

            List<Teacher> teachers = importRepo.teacherBoscoGetForExport(districId, "NEW");

            //ImportHelper.DebugSpin(true);

            ImportHelper.DebugCountdownSet(teachers.size());


            for (Teacher bu : teachers) {

                newTeachers++;
                //ImportHelper.DebugSpin(false);
                ImportHelper.DebugCountdown();

                // THIS IS OK it will return empty list if school not found!

                bu.setAssignedSchools(importRepo.schoolsForTeacher(bu.getId()));


                // System.out.println(bu.getId());
                // System.out.println(bu.getPrefix());
                // System.out.println(bu.getFirstName());
                // System.out.println(bu.getLastName());
                // System.out.println(bu.getMiddleName());
                // System.out.println(bu.getEmail());
                // System.out.println(bu.getTitle());
                // System.out.println(bu.getOrganizationId());
                // System.out.println(bu.getRole());

                // System.out.println(bu.getAssignedSchools());



                if (useOld) {
                    boscoClient.putTeacher(baseUrl + "users/upsertUser/{id}", token, bu);
                }

                else {
                    ApiResult res = boscoClient.putTeacher2(baseUrl + "users/upsertUser/{id}", token, bu);
                    if (!res.success) {
                        importRepo.logError(res.errorMessage);
                        System.out.println("ERRROR: " + res.errorMessage);

                    }
                }

            }

            System.out.println("    - " + newTeachers);


            System.out.println("------------ CHANGED TEACHERS ----------------\n");
            teachers = importRepo.teacherBoscoGetForExport(districId, "CHANGED");

            //ImportHelper.DebugSpin(true);
            ImportHelper.DebugCountdownSet(teachers.size());

            for (Teacher bu : teachers) {

                changedTeachers++;
                //ImportHelper.DebugSpin(false);

                ImportHelper.DebugCountdown();

                bu.setAssignedSchools(importRepo.schoolsForTeacher(bu.getId()));

                if (useOld) {
                    boscoClient.putTeacher(baseUrl + "users/upsertUser/{id}", token, bu);
                }

                else {
                    ApiResult res = boscoClient.putTeacher2(baseUrl + "users/upsertUser/{id}", token, bu);
                    if (!res.success) {
                        importRepo.logError(res.errorMessage);
                        System.out.println("ERRROR: " + res.errorMessage);

                    }
                }

            }

            System.out.println("    - " + changedTeachers);

            
            

            System.out.println("------------ NEW STUDENTS ----------------\n");
            List<Student> bss = importRepo.studentsBoscoForExport(districId, "NEW");

            ImportHelper.DebugCountdownSet(bss.size());
            //ImportHelper.DebugSpin(true);

            for (Student bs : bss) {

                
                bs.setGuardians(importRepo.guardiansBoscoForStudent(bs.getId()));
                bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(bs.getId()));

                // make this pretty json and log.

                newStudents++;

                ImportHelper.DebugCountdown();
                //ImportHelper.DebugSpin(false);
                //boscoClient.postStudent(baseUrl + "students", token, bs);
                

                if (useOld) {
                    boscoClient.putStudent(baseUrl + "students/upsert/{id}", token, bs);
                }

                else {
                    ApiResult res = boscoClient.putStudent2(baseUrl + "students/upsert/{id}", token, bs);
                    if (!res.success) {
                        importRepo.logError(res.errorMessage);
                        System.out.println("ERRROR: " + res.errorMessage);

                    }
                }


            }

            System.out.println("    - " + newStudents);

            System.out.println("------------ CHANGED STUDENTS ----------------\n");
            bss = importRepo.studentsBoscoForExport(districId, "CHANGED");

            //ImportHelper.DebugSpin(true);
            ImportHelper.DebugCountdownSet(bss.size());

            for (Student bs : bss) {

                
               bs.setGuardians(importRepo.guardiansBoscoForStudent(bs.getId()));
                bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(bs.getId()));


                // make this pretty json and log.

                changedStudents++;

                //ImportHelper.DebugSpin(false);

                ImportHelper.DebugCountdown();

                

                if (useOld) {
                    boscoClient.putStudent(baseUrl + "students/upsert/{id}", token, bs);
                }

                else {
                    ApiResult res = boscoClient.putStudent2(baseUrl + "students/upsert/{id}", token, bs);
                    if (!res.success) {
                        importRepo.logError(res.errorMessage);
                        System.out.println("ERRROR: " + res.errorMessage);

                    }
                }


            }
            System.out.println("    - " + changedStudents);

            



// DELETES


            System.out.println("------------ DELETED STUDENTS ----------------\n");
            bss = importRepo.studentsBoscoForExport(districId, "DELETE");

            ImportHelper.DebugCountdownSet(bss.size());
             for (Student bs : bss) {

                deletedStudents++;
                

                 if (useOld) {
                     boscoClient.deleteStudent(baseUrl + "students/{id}", token, bs.getId());
                }

                else {
                    ApiResult res = boscoClient.deleteStudent2(baseUrl + "students/{id}", token, bs.getId());
                    if (!res.success) {
                        importRepo.logError(res.errorMessage);
                        System.out.println("ERRROR: " + res.errorMessage);

                    }
                }

                ImportHelper.DebugCountdown();

                
            }

            System.out.println("    - " + deletedStudents);

            System.out.println("------------ DELETED TEACHERS ----------------\n");
            teachers = importRepo.teacherBoscoGetForExport(districId, "DELETE");

            ImportHelper.DebugCountdownSet(teachers.size());
            for (Teacher bu : teachers) {

                deletedTeachers++;

                if (useOld) {
                     boscoClient.deleteTeacher(baseUrl + "users/{id}", token, bu.getId());
                }

                else {
                    ApiResult res = boscoClient.deleteTeacher2(baseUrl + "users/{id}", token, bu.getId());
                    if (!res.success) {
                        importRepo.logError(res.errorMessage);
                        System.out.println("ERRROR: " + res.errorMessage);

                    }
                }
               

                ImportHelper.DebugCountdown();

                
            }

            System.out.println("    - " + deletedTeachers);


            
        return true;

    }


}
