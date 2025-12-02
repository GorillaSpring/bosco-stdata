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
import com.bosco.stdata.utils.MappingHelper;
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


        Boolean isRoster = true;
        Boolean isSisData = false;
 

        ImportResult result = new ImportResult();

        try 
        {
            ImportDefinition importDef = i.importRepo.getImportDefinition(importDefId);

            Boolean setNoEmails = importDef.getSetNoEmails();



            List<ImportSetting> importSettings = i.importRepo.getImportSettings(importDefId);

            districtId = importDef.getDistrictId();
            


                
        //         //String baseFileFolder = "C:/test/uplift/" + subFolder + "/";
             clientId = ImportHelper.ValueForSetting(importSettings, "clientId");

             clientSecret =  ImportHelper.ValueForSetting(importSettings, "clientSecret");
            
        //     // remove this.
        //     tokenUrl =  ImportHelper.ValueForSetting(importSettings, "tokenUrl");
             apiBase =  ImportHelper.ValueForSetting(importSettings, "apiBase");


             int importId = i.importRepo.prepImport(districtId, importDefId, isRoster, isSisData,  "ClassLinkOneRosterApi");


        //    clientId = "2b6652400a043b8a3eae0c08";
         //   clientSecret = "ab6fae43e09784fc3549df25";

            ClassLinkOneRoster oneRoster = new ClassLinkOneRoster(clientId, clientSecret);


            LocalDateTime startDateTime = LocalDateTime.now();

            
            
            result.importId = importId;
            result.districtId = districtId;

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

            String requestUrl = "";

            //filter = "status='active'/orgs?type='school'";


            ClassLinkOneRosterResponse oneRosterRes;


            Boolean skipSchools = false;

            // there will only be one page of schools.

            if (!skipSchools) {
                requestUrl = apiBase + "ims/oneroster/v1p1/schools?filter=status%3D'active'&limit=" + PAGE_SIZE + "&offset=" + PAGE_SIZE * pageNumber + "&orderBy=asc";

                oneRosterRes = oneRoster.makeRosterRequest(requestUrl);

                statusCode = oneRosterRes.getStatusCode();

                System.out.println("Status is: " + statusCode);
                

                response = oneRosterRes.getResponse();

                rootNode = objectMapper.readTree(response);
                data = rootNode.get("orgs");
                

                if (data.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) data;
                    for (JsonNode orgsNode: arrayNode) {

                        String sourceId = orgsNode.get("sourcedId").asText();
                        String name = orgsNode.get("name").asText();


                        String identifier = orgsNode.get("identifier").asText();

                        if (identifier.isEmpty())
                            identifier = sourceId;

                        //System.out.println("School : " + sourceId + " - " + name);

                        // for NBISD, the idenitfier is balnk, so we will just use the sourceID

                        i.importRepo.saveSchool(sourceId, name, identifier);
                        schoolCount++;

                    
                    }
                        
                    
                }
                else {
                    System.out.println("Not Array");
                }

                System.out.println ("Schools Imported: " + schoolCount);
                i.importRepo.logInfo("Schools Imported: " + schoolCount);

            }

            
            System.out.println("Getting USERS");



            pageNumber = 0;
            studentCount = 0;
            guardianCount = 0;
            teacherCount = 0;
            //String filter = "status='active'/users?role='guardian'";
            //filter = "status='active'";


            //String url = "https://springtownisd-tx-v2.rosterserver.com/ims/oneroster/v1p1/users?filter=status%3D'active'&limit=100&offset=0&orderBy=asc";


            // SO, we are going to get students first to make sure we have the student Id loaded.


            
            requestUrl = apiBase + "ims/oneroster/v1p1/students?filter=status%3D'active'&limit=" + PAGE_SIZE + "&offset=" + PAGE_SIZE * pageNumber + "&orderBy=asc";

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
                        
                            
                            default:
                                System.out.println("INVALID ROLE Found " + userNode.get("role").asText());
                                break;
                        }

                    }
                
                }
                else {
                    System.out.println("Not Array");
                }

                // next page
                pageNumber++;

                requestUrl = apiBase + "ims/oneroster/v1p1/students?filter=status%3D'active'&limit=" + PAGE_SIZE + "&offset=" + PAGE_SIZE * pageNumber + "&orderBy=asc";

                oneRosterRes = oneRoster.makeRosterRequest(requestUrl);

                statusCode = oneRosterRes.getStatusCode();

                System.out.println("Status is: " + statusCode);

                response = oneRosterRes.getResponse();

                rootNode = objectMapper.readTree(response);
                data = rootNode.get("users");

            }
            i.importRepo.logInfo ("Saved Students: " + studentCount);

            System.out.println("Saved Students: " + studentCount);


            pageNumber = 0;
            studentCount = 0;


            requestUrl = apiBase + "ims/oneroster/v1p1/users?filter=status%3D'active'&limit=" + PAGE_SIZE + "&offset=" + PAGE_SIZE * pageNumber + "&orderBy=asc";

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
                                // we ignore this because we got above!
                                break;

                        
                            case "parent":
                            case "guardian":
                                // we only pull in if the email is not null or empty

                                // if (userNode.get("role").asText().equals("guardian")) {
                                //     System.out.println("Got Guardian");
                                //     System.out.println(userNode.toPrettyString());
                                // }

                                String email = userNode.get("email").asText();
                                String guardianType = "U";
                                if (email != null && !email.isEmpty()) {

                                    JsonNode agentNodes = userNode.get("agents");

                                    if (agentNodes.isArray()) {
                                        for (JsonNode studentNode: agentNodes) {
                                            


                                                
                                            if (setNoEmails && email.length() >= 4) {
                                                String trimedEmail = email.substring(0, email.length() - 4);
                                                email = trimedEmail + "_no.no";
                                            }


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
                            case "administrator":    // lets try to get the admin.

                                String teacherEmail = userNode.get("email").asText();

                                if (!teacherEmail.isBlank()) {

                                
                                    // sourceid, teacherId, firstname, lastname,  email
                                    //Teacher t = new Teacher(userNode.get("sourcedId").asText(),userNode.get("identifier").asText(), userNode.get("givenName").asText(),  userNode.get("familyName").asText(), teacherEmail);


                                    if (setNoEmails && teacherEmail.length() >= 4) {
                                        String trimedEmail = teacherEmail.substring(0, teacherEmail.length() - 4);
                                        teacherEmail = trimedEmail + "_no.no";
                                    }


                                    if (userNode.get("role").asText().equals("administrator")) {
                                        System.out.println ("Found Admin");
                                    }

                                    String tschoolSourceId = "X";
                                    JsonNode tschoolNode = userNode.get("orgs");
                                    if (tschoolNode != null) {
                                        if (tschoolNode.isArray()) {
                                                if (tschoolNode.size() > 0) {
                                                    JsonNode tschoolElement = tschoolNode.get(0);
                                                    tschoolSourceId = tschoolElement.get("sourcedId").asText();
                                                }

                                        }
                                    }

                                    // String sourceid, String teacherId, String firstname, String lastname, String email
                                    // String sourceId, String teacherId, String firstName, String lastName, String email
                                    i.importRepo.saveTeacher(
                                        userNode.get("sourcedId").asText(),userNode.get("identifier").asText(), userNode.get("givenName").asText(),  userNode.get("familyName").asText(), 
                                        teacherEmail,
                                        tschoolSourceId
                                    );
                                    teacherCount++;
                                }
                                
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
            
            i.importRepo.logInfo ("Saved Guardians: " + guardianCount);
            i.importRepo.logInfo ("Saved Teachers: " + teacherCount);
            System.out.println("Saved Guardians: " + guardianCount);
            System.out.println("Saved Teachers: " + teacherCount);

        
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


                        //System.err.println(demographicsNode.toPrettyString());

           


                        // This is JUST for NewBraunfelsClasslinkOneRosterApi.  Put in district Id if

                        if (districtId == 4832370) {


                            JsonNode metadataNode = demographicsNode.get("metadata");

                            String Guardian1FullName = metadataNode.get("Guardian1FullName").asText();
                            String Guardian1Email = metadataNode.get("Guardian1Email").asText();
                            String Guardian2FullName = metadataNode.get("Guardian2FullName").asText();
                            String Guardian2Email = metadataNode.get("Guardian2Email").asText();

                            String[] guardianNames;

                            if (!Guardian1FullName.isEmpty()) {
                                guardianNames = Guardian1FullName.split(" ");

                                i.importRepo.saveGuardian(sourceId + "_g1", sourceId + "_g1", sourceId, guardianNames[0], guardianNames[1], Guardian1Email, "U");
                            }

                            if (!Guardian2FullName.isEmpty()) {
                                guardianNames = Guardian2FullName.split(" ");

                                i.importRepo.saveGuardian(sourceId + "_g2", sourceId + "_g2", sourceId, guardianNames[0], guardianNames[1], Guardian2Email, "U");

                            }

                        }

                        // String sourceId, String guardianId, String studentSourceId, String firstName, String lastName, String email, String type
                        //i.importRepo.saveGuardian(sourceId, Guardian2FullName, sourceId, importDefId, response, Guardian2Email, requestUrl);

                                    

                        //GET HERE

                            // For burleson, the guarian info is in meta data, we should pass in a flag to get it.
                            // check springtown.

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

                            if (!birthDate.isEmpty()) {
                                birthDate =  ImportHelper.DateToStdFormat(birthDate);
                            }


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


                            String gender = ImportHelper.GenderFromSex(demographicsNode.get("sex").asText());


                            i.importRepo.saveStudentDemographics(
                                studentNumber,
                                birthDate,
                                gender,
                                Boolean.parseBoolean(demographicsNode.get("americanIndianOrAlaskaNative").asText()),
                                Boolean.parseBoolean(demographicsNode.get("asian").asText()),
                                Boolean.parseBoolean(demographicsNode.get("blackOrAfricanAmerican").asText()),
                                Boolean.parseBoolean(demographicsNode.get("nativeHawaiianOrOtherPacificIslander").asText()),
                                Boolean.parseBoolean(demographicsNode.get("white").asText()),
                                Boolean.parseBoolean(demographicsNode.get("hispanicOrLatinoEthnicity").asText())
                                
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
            System.out.println("Demographics updated: " + studentCount);


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

                        String schoolSourceId = "";
                        JsonNode schoolNode = enrollmentNode.get("school");
                        schoolSourceId = schoolNode.get("sourcedId").asText();


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

         

            // validation on the data.
            // check number of diffs vs the cutoff.


            // this will mark the importId as the base.
            //i.importRepo.setImportBase(importDefId);


            
            i.importRepo.prepSendBosco(districtId, importDefId, isRoster, isSisData);



            if (!importDef.getForceLoad() && isRoster) {
                String checkDeltas = i.importRepo.checkImportDeltas(districtId, importDefId);
                if (!checkDeltas.equals("OK")) {
                    throw new Exception("Check Import Delta failed: " + checkDeltas);
                }

            }


      

            i.boscoApi.sendImportToBosco(districtId);

            i.importRepo.postSendBosco(districtId, importDefId, isRoster, isSisData);

            LocalDateTime endDateTime = LocalDateTime.now();
    
            Duration duration = Duration.between(startDateTime, endDateTime);
            
            System.out.println ("Import Complete in : " + duration.toSeconds() + " Seconds" );

            i.importRepo.logInfo("Import " + importDefId + "  Complete in : " + duration.toSeconds() + " Seconds" );


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
