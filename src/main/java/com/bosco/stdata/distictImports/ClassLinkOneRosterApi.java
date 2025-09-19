package com.bosco.stdata.distictImports;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bosco.stdata.model.ImportDefinition;
import com.bosco.stdata.model.ImportResult;
import com.bosco.stdata.model.ImportSetting;


import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.utils.ClassLinkOneRoster;
import com.bosco.stdata.utils.ClassLinkOneRosterResponse;
import com.bosco.stdata.utils.ImportHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import jakarta.annotation.PostConstruct;

@Component
public class ClassLinkOneRosterApi {
     @Autowired
    ImportRepo importRepo;

    
    @Autowired 
    BoscoApi boscoApi;

    private static ClassLinkOneRosterApi i;  // instance


    static int districtId = 0;
	static String clientId = "";
    static String clientSecret = "";
	static String tokenUrl = "";

    static String apiBase = "";

    private final static int PAGE_SIZE = 5000;

    
    @PostConstruct
    public void init() {
        System.out.println("ClassLinkOneRosterApi - init()");
        i = this;
    }

    public static ImportResult Import(String importDefId) {
        ImportResult result = new ImportResult();

        try 
        {
            ImportDefinition importDef = i.importRepo.getImportDefinition(importDefId);

            //int baseImportId = importDef.getBaseImportId();


            List<ImportSetting> importSettings = i.importRepo.getImportSettings(importDefId);

             districtId = importDef.getDistrictId();
            
                
        //         //String baseFileFolder = "C:/test/uplift/" + subFolder + "/";
             clientId = ImportHelper.ValueForSetting(importSettings, "clientId");

             clientSecret =  ImportHelper.ValueForSetting(importSettings, "clientSecret");
            
        //     // remove this.
        //     tokenUrl =  ImportHelper.ValueForSetting(importSettings, "tokenUrl");
             apiBase =  ImportHelper.ValueForSetting(importSettings, "apiBase");


        //    clientId = "2b6652400a043b8a3eae0c08";
         //   clientSecret = "ab6fae43e09784fc3549df25";

            ClassLinkOneRoster oneRoster = new ClassLinkOneRoster(clientId, clientSecret);


            LocalDateTime startDateTime = LocalDateTime.now();

            i.importRepo.prepImport(districtId, "Import for " + importDefId);
            
            result.importId = 0;
            result.districtId = districtId;
            result.baseImportId = 0;

            i.importRepo.logInfo("OneRoster API import : " + importDefId);


            System.out.println("Import  For District " + districtId);
            
            JsonNode data;
            JsonNode rootNode;
            
            int pageNumber = 0;

            
            // users

            pageNumber = 0;

            int studentCount = 0;
            int guardianCount = 0;
            int teacherCount = 0;

            ObjectMapper objectMapper = new ObjectMapper();
            String response;
            int statusCode;

            // First the orgs
            System.out.println("Getting Schools");

            pageNumber = 0;
            studentCount = 0;

            //String filter = "";

            int schoolCount = 0;

            //filter = "status='active'/orgs?type='school'";


// https://springtownisd-tx-v2.rosterserver.com/ims/oneroster/v1p1/schools?filter=status%3D'active'&limit=1000&offset=0&orderBy=asc


            ClassLinkOneRosterResponse oneRosterRes;

            //Srring urlNotEncoded = "https://springtownisd-tx-v2.rosterserver.com/ims/oneroster/v1p1/schools?filter=status='active'&limit=1000&offset=0&orderBy=asc"

            //String testUrl = "https://springtownisd-tx-v2.rosterserver.com/ims/oneroster/v1p1/schools?filter=status%3D'active'&limit=1000&offset=0&orderBy=asc";


            // there will only be one page of schools.

            // String requestUrl = apiBase + "ims/oneroster/v1p1/schools?filter=status%3D'active'&limit=" + PAGE_SIZE + "&offset=" + PAGE_SIZE * pageNumber + "&orderBy=asc";

            // oneRosterRes = oneRoster.makeRosterRequest(requestUrl);

            // statusCode = oneRosterRes.getStatusCode();

            // System.out.println("Status is: " + statusCode);
            

            // response = oneRosterRes.getResponse();

            // rootNode = objectMapper.readTree(response);
            // data = rootNode.get("orgs");
            

            // if (data.isArray()) {
            //     ArrayNode arrayNode = (ArrayNode) data;
            //     for (JsonNode orgsNode: arrayNode) {

            //         String sourceId = orgsNode.get("sourcedId").asText();
            //         String name = orgsNode.get("name").asText();
            //         String identifier = orgsNode.get("identifier").asText();

            //         //System.out.println("School : " + sourceId + " - " + name);

            //         i.importRepo.saveSchool(sourceId, name, identifier);
            //         schoolCount++;

                
            //     }
                    
                
            // }
            // else {
            //     System.out.println("Not Array");
            // }

            // System.out.println ("Schools Imported: " + schoolCount);
            // i.importRepo.logInfo("Schools Imported: " + schoolCount);


            
            System.out.println("Getting USERS");



            pageNumber = 0;
            studentCount = 0;
            guardianCount = 0;
            teacherCount = 0;
            //String filter = "status='active'/users?role='guardian'";
            //filter = "status='active'";


            //String url = "https://springtownisd-tx-v2.rosterserver.com/ims/oneroster/v1p1/users?filter=status%3D'active'&limit=100&offset=0&orderBy=asc";


            String requestUrl = apiBase + "ims/oneroster/v1p1/users?filter=status%3D'active'&limit=" + PAGE_SIZE + "&offset=" + PAGE_SIZE * pageNumber + "&orderBy=asc";

            oneRosterRes = oneRoster.makeRosterRequest(requestUrl);

            statusCode = oneRosterRes.getStatusCode();

            System.out.println("Status is: " + statusCode);

            response = oneRosterRes.getResponse();

            rootNode = objectMapper.readTree(response);
            data = rootNode.get("users");


            //data = oneRosterService.fetchResourcePage( apiBase + "users", token, pageNumber);

            while ( data.size() > 0) {

                if (data.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) data;
                    for (JsonNode userNode: arrayNode) {

                        switch (userNode.get("role").asText()) {
                            case "role":
                                // just the header.
                                break;
                            case "student":

                                // ** So, there is orgs array.  Has "Entity_2"  this is the school.

                                String schoolSourceId = "X";
                                JsonNode schoolNode = userNode.get("orgs");
                                if (schoolNode != null) {
                                    if (schoolNode.isArray()) {
                                            if (schoolNode.size() > 0) {
                                                JsonNode schoolElement = schoolNode.get(0);
                                                schoolSourceId = schoolElement.get("sourcedId").asText();
                                            }

                                    }
                                }


                                String grade = "";
                                JsonNode gradeNode = userNode.get("grades");
                                if (gradeNode != null) {
                                    if (gradeNode.isArray()) {
                                            if (gradeNode.size() > 0) {
                                                JsonNode gradeElement = gradeNode.get(0);
                                                grade = gradeElement.asText();
                                            }

                                    }
                                }



                                // Student s = new Student(userNode.get("sourcedId").asText(), userNode.get("identifier").asText(),  userNode.get("givenName").asText(),  
                                //         userNode.get("familyName").asText(),
                                //         grade, schoolCode);
                                
                                // so identifier should be our id.
                                // grades[]  is an array of grades.  This is a bit weired.
                                // Student s = new Student(userNode.get("sourcedId").asText(), userNode.get("givenName").asText(),  userNode.get("familyName").asText());
                                // 	//Student s = new Student(row[0], row[8], row[9]);

                                //String sourceId, String studentId, String firstName, String lastName, String grade, String schoolSourceId

                                // String sourceId, String studentNumber, String firstName, String lastName, String grade, String schoolSourceId

                                i.importRepo.saveStudent(userNode.get("sourcedId").asText(), userNode.get("identifier").asText(),  userNode.get("givenName").asText(),  
                                        userNode.get("familyName").asText(),
                                        grade, schoolSourceId);
                                studentCount++;
                                break;
                        
                            case "parent":
                            case "guardian":
                                // we only pull in if the email is not null or empty
                                String email = userNode.get("email").asText();
                                String guardianType = "U";
                                if (email != null && !email.isEmpty()) {

                                    JsonNode agentNodes = userNode.get("agents");

                                    if (agentNodes.isArray()) {
                                        for (JsonNode studentNode: agentNodes) {
                                            

                                            String studentSourceId = studentNode.get("sourcedId").asText();
                                            //Guardian g = new Guardian(userNode.get("sourcedId").asText(), studentId, userNode.get("identifier").asText(), userNode.get("givenName").asText(), userNode.get("familyName").asText(), email, guardianType);

                                            // String sourceId, String guardianId, String studentId, String firstName, String lastName, String email, String type
                                            // String sourceId, String guardianId, String studentId, String firstName, String lastName, String email, String type

                                            // String sourceId, String guardianId, String studentSourceId, String firstName, String lastName, String email, String type
                                            i.importRepo.saveGuardian(userNode.get("sourcedId").asText(), userNode.get("identifier").asText(), studentSourceId, userNode.get("givenName").asText(), userNode.get("familyName").asText(), email, guardianType);
                                            guardianCount++;



                                        }
                                    }
                                }
                                break;
                            case "teacher":

                                String teacherEmail = userNode.get("email").asText();
                                // sourceid, teacherId, firstname, lastname,  email
                                //Teacher t = new Teacher(userNode.get("sourcedId").asText(),userNode.get("identifier").asText(), userNode.get("givenName").asText(),  userNode.get("familyName").asText(), teacherEmail);


                                // String sourceid, String teacherId, String firstname, String lastname, String email
                                // String sourceId, String teacherId, String firstName, String lastName, String email
                                i.importRepo.saveTeacher(
                                    userNode.get("sourcedId").asText(),userNode.get("identifier").asText(), userNode.get("givenName").asText(),  userNode.get("familyName").asText(), teacherEmail
                                );
                                teacherCount++;
                                
                                break;
                            default:
                                System.out.println("Found " + userNode.get("role").asText());
                                break;
                        }

                    }
                
                }
                else {
                    System.out.println("Not Array");
                }

                // next page
                pageNumber++;

                requestUrl = apiBase + "ims/oneroster/v1p1/users?filter=status%3D'active'&limit=" + PAGE_SIZE + "&offset=" + PAGE_SIZE * pageNumber + "&orderBy=asc";

                oneRosterRes = oneRoster.makeRosterRequest(requestUrl);

                statusCode = oneRosterRes.getStatusCode();

                System.out.println("Status is: " + statusCode);

                response = oneRosterRes.getResponse();

                rootNode = objectMapper.readTree(response);
                data = rootNode.get("users");

            }
            i.importRepo.logInfo ("Saved Students: " + studentCount);
            i.importRepo.logInfo ("Saved Guardians: " + guardianCount);
            i.importRepo.logInfo ("Saved Teachers: " + teacherCount);

        
            // now the demographics


            System.out.println("Getting Student Demographics");

            pageNumber = 0;
            studentCount = 0;


            // https://springtownisd-tx-v2.rosterserver.com/ims/oneroster/v1p1/demographics?filter=status%3D'active'&limit=100&offset=0&orderBy=asc
            requestUrl = apiBase + "ims/oneroster/v1p1/demographics?filter=status%3D'active'&limit=" + PAGE_SIZE + "&offset=" + PAGE_SIZE * pageNumber + "&orderBy=asc";

            oneRosterRes = oneRoster.makeRosterRequest(requestUrl);

            statusCode = oneRosterRes.getStatusCode();

            System.out.println("Status is: " + statusCode);

            response = oneRosterRes.getResponse();

            rootNode = objectMapper.readTree(response);
            data = rootNode.get("demographics");            
               //data = oneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/demographics", filter, token, pageNumber);

            while ( data.size() > 0) {
                    
                

                if (data.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) data;
                    for (JsonNode demographicsNode: arrayNode) {

                        String sourceId = demographicsNode.get("sourcedId").asText();

                        //System.out.println(sourceId);

                        // SO this is not true for Clint.
                        //if (sourceId.toLowerCase().contains("student"))
                        //if (sourceId.startsWith("Student_")) 
                        //{
                            //System.out.println("Update Student : " + sourceId);

// SO for our Sandbox, the sourceId is just the id of the student.
// we could eigher skip this and let it update 0 rows, or we could have config.

                        //if (sourceId.toLowerCase().contains("student")) {

                            String birthDate = demographicsNode.get("birthDate") == null ? "" : demographicsNode.get("birthDate").asText();


                            // Demographics d = new Demographics(sourceId,
                            //     birthDate,
                            //     demographicsNode.get("sex").asText(),
                            //     Boolean.parseBoolean(demographicsNode.get("americanIndianOrAlaskaNative").asText()),
                            //     Boolean.parseBoolean(demographicsNode.get("asian").asText()),
                            //     Boolean.parseBoolean(demographicsNode.get("blackOrAfricanAmerican").asText()),
                            //     Boolean.parseBoolean(demographicsNode.get("nativeHawaiianOrOtherPacificIslander").asText()),
                            //     Boolean.parseBoolean(demographicsNode.get("white").asText()),
                            //     Boolean.parseBoolean(demographicsNode.get("hispanicOrLatinoEthnicity").asText())
                                
                            //     );

                            String studentNumber = i.importRepo.studentNumberFromSourceId(sourceId);

                            i.importRepo.saveStudentDemographics(
                                studentNumber,
                                birthDate,
                                demographicsNode.get("sex").asText(),
                                Boolean.parseBoolean(demographicsNode.get("americanIndianOrAlaskaNative").asText()),
                                Boolean.parseBoolean(demographicsNode.get("asian").asText()),
                                Boolean.parseBoolean(demographicsNode.get("blackOrAfricanAmerican").asText()),
                                Boolean.parseBoolean(demographicsNode.get("nativeHawaiianOrOtherPacificIslander").asText()),
                                Boolean.parseBoolean(demographicsNode.get("white").asText()),
                                Boolean.parseBoolean(demographicsNode.get("hispanicOrLatinoEthnicity").asText()),
                                false, false, false
                            );

                            studentCount++;
                        
                        
                        //}
                    

                    }
                
                    
                }
                else {
                    System.out.println("Not Array");
                }

                // next page
                pageNumber++;

                System.out.println("Getting Demographics page : " + pageNumber);
                requestUrl = apiBase + "ims/oneroster/v1p1/demographics?filter=status%3D'active'&limit=" + PAGE_SIZE + "&offset=" + PAGE_SIZE * pageNumber + "&orderBy=asc";

                oneRosterRes = oneRoster.makeRosterRequest(requestUrl);

                statusCode = oneRosterRes.getStatusCode();

                System.out.println("Status is: " + statusCode);

                response = oneRosterRes.getResponse();

                rootNode = objectMapper.readTree(response);
                data = rootNode.get("demographics");            


            }
            i.importRepo.logInfo ("Demographics updated: " + studentCount);


              // now the classes
            System.out.println("Getting Enrollments");

            pageNumber = 0;
            studentCount = 0;
            teacherCount = 0;


            // https://springtownisd-tx-v2.rosterserver.com/ims/oneroster/v1p1/enrollments?filter=status%3D'active'&limit=100&offset=0&orderBy=asc

            requestUrl = apiBase + "ims/oneroster/v1p1/enrollments?filter=status%3D'active'&limit=" + PAGE_SIZE + "&offset=" + PAGE_SIZE * pageNumber + "&orderBy=asc";

            oneRosterRes = oneRoster.makeRosterRequest(requestUrl);

            statusCode = oneRosterRes.getStatusCode();

            System.out.println("Status is: " + statusCode);

            response = oneRosterRes.getResponse();

            rootNode = objectMapper.readTree(response);
            data = rootNode.get("enrollments");           



            while ( data.size() > 0) {
                    
                

                if (data.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) data;
                    for (JsonNode enrollmentNode: arrayNode) {

                        String role = enrollmentNode.get("role").asText();

                        String classSourceId = "";
                        String userSourceId = "";


                        JsonNode classNode = enrollmentNode.get("class");
                        classSourceId = classNode.get("sourcedId").asText();

                        JsonNode userNode = enrollmentNode.get("user");
                        userSourceId = userNode.get("sourcedId").asText();
                        
                        if (userSourceId != "" && classSourceId != "") {

                            switch (role) {
                                case "teacher":
                                    i.importRepo.saveTeacherClass(userSourceId, classSourceId);
                                    teacherCount++;
                                    break;
                                case "student":
                                    i.importRepo.saveStudentClass(userSourceId, classSourceId);
                                    studentCount++;
                                    break;
                            
                                default:
                                    break;
                            }

                        }

                    }
                    

                    
                        
                }
                else {
                    System.out.println("Not Array");
                }

                // next page
                pageNumber++;
                System.out.println("Getting Enrollments page : " + pageNumber);

                requestUrl = apiBase + "ims/oneroster/v1p1/enrollments?filter=status%3D'active'&limit=" + PAGE_SIZE + "&offset=" + PAGE_SIZE * pageNumber + "&orderBy=asc";

                oneRosterRes = oneRoster.makeRosterRequest(requestUrl);

                statusCode = oneRosterRes.getStatusCode();

                System.out.println("Status is: " + statusCode);

                response = oneRosterRes.getResponse();

                rootNode = objectMapper.readTree(response);
                data = rootNode.get("enrollments");           

            }

            i.importRepo.logInfo ("Saved StudentClasses: " + studentCount);
            i.importRepo.logInfo ("Saved TeacherClasses: " + teacherCount);

            // build the student teacher table

            i.importRepo.buildStudentTeacher();

            
            //i.importRepo.diffImports(baseImportId);

            // validation on the data.
            // check number of diffs vs the cutoff.


            // this will mark the importId as the base.
            //i.importRepo.setImportBase(importDefId);


            
            i.importRepo.postImport();


            LocalDateTime endDateTime = LocalDateTime.now();
    
            Duration duration = Duration.between(startDateTime, endDateTime);
            
            System.out.println ("Import Complete in : " + duration.toSeconds() + " Seconds" );

            i.importRepo.logInfo("Import " + importDefId + "  Complete in : " + duration.toSeconds() + " Seconds" );


            //System.out.println ("Import ID is: " + importId);

            i.boscoApi.sendImportToBosco(districtId);

            result.success = true;


        }
       catch (Exception ex) {
            i.importRepo.logError(ex.toString());
            result.errorMessage = ex.toString();
            result.success = false;
            System.out.println(ex.toString());
        }
        return result;

    }

}
