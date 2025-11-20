package com.bosco.stdata.distictImports;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.model.ImportDefinition;
import com.bosco.stdata.model.ImportResult;
import com.bosco.stdata.model.ImportSetting;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.SkywardOneRosterService;
import com.bosco.stdata.service.SkywardTokenService;
import com.bosco.stdata.utils.ImportHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.swagger.v3.core.util.Json;
import jakarta.annotation.PostConstruct;

@Component
public class PowerSchoolOneRosterApi {

    private final AppConfig appConfig;

 @Autowired
    ImportRepo importRepo;

    
    @Autowired 
    BoscoApi boscoApi;

    @Autowired
    SkywardTokenService tokenService;

    @Autowired
    SkywardOneRosterService skywardOneRosterService;

     private static PowerSchoolOneRosterApi i;  // instance

    

    static int districtId = 0;
	static String clientId = "";
    static String clientSecret = "";
	static String tokenUrl = "";

    static String apiBase = "";

    static Boolean useSkywardSpEd = false;

    PowerSchoolOneRosterApi(AppConfig appConfig) {
        this.appConfig = appConfig;
    }  

     @PostConstruct
    public void init() {
        System.out.println("PowerSchoolOneRosterApi - init()");
        i = this;
    }


    public static ImportResult Import(String importDefId) {

    Boolean isRoster = true;
        Boolean isSisData = false;


        ImportResult result = new ImportResult();

        try {
            ImportDefinition importDef = i.importRepo.getImportDefinition(importDefId);


            Boolean setNoEmails = importDef.getSetNoEmails();

            List<ImportSetting> importSettings = i.importRepo.getImportSettings(importDefId);

            districtId = importDef.getDistrictId();

            clientId = ImportHelper.ValueForSetting(importSettings, "clientId");

            clientSecret =  ImportHelper.ValueForSetting(importSettings, "clientSecret");
            
            tokenUrl =  ImportHelper.ValueForSetting(importSettings, "tokenUrl");
            apiBase =  ImportHelper.ValueForSetting(importSettings, "apiBase");

            //useSkywardSpEd = Boolean.parseBoolean(ImportHelper.ValueForSetting(importSettings, "useSkywardSpEd"));

            LocalDateTime startDateTime = LocalDateTime.now();

            
            int importId = i.importRepo.prepImport(districtId, importDefId, isRoster, isSisData,  "PowerSchoolOneRosterApi ");
            
            result.importId = importId;
            result.districtId = districtId;

            i.importRepo.logInfo("OneRoster API import : " + importDefId);


            System.out.println("Import For District " + districtId);

            



            System.out.println("Import One Roster Start");

            String token = i.tokenService.getAccessTokenPowerSchool(clientId, clientSecret, tokenUrl);

// GetDemographisViaSpedApi(token);
            

            JsonNode data;

            
            int pageNumber = 0;

            
            // users

            pageNumber = 0;

            int studentCount = 0;
            int guardianCount = 0;
            int teacherCount = 0;

                String filter = "";

            int schoolCount = 0;


            // Lets try to get grads.

/*
              
            System.out.println("Trying  Grades");

            pageNumber = 0;
            studentCount = 0;


// Lets try to get all the categories.appConfig

            filter = "";
            data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/categories", filter, token, pageNumber);

            while ( data.size() > 0) {
                
                System.out.println("Got Data");
                System.out.println(data.toPrettyString());
                

                // if (data.isArray()) {
                //     ArrayNode arrayNode = (ArrayNode) data;
                //     for (JsonNode orgsNode: arrayNode) {

                //         String sourceId = orgsNode.get("sourcedId").asText();
                //         String name = orgsNode.get("name").asText();

                //         String identifier = orgsNode.get("identifier").asText();
                //         // 

                //         i.importRepo.saveSchool(sourceId, name, identifier);
                //         schoolCount++;

                    
                //     }
                        
                    
                // }
                // else {
                //     System.out.println("Not Array");
                // }

                // next page
                pageNumber++;

                System.out.println("Getting results page : " + pageNumber);
                data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/categories", filter, token, pageNumber);


            }
            System.out.println ("results Imported: " + schoolCount);
            i.importRepo.logInfo("results Imported: " + schoolCount);



            

            String studentSurceId = "84b742b5-739c-31d4-80f2-5613a61509cb-S-403";

            String categoryId = "84b742b5-739c-31d4-80f2-5613a61509cb-684";
            String classId = "84b742b5-739c-31d4-80f2-5613a61509cb-2044";

            // lets get line items and see what is returned.



            //filter = "status='active'/orgs?type='school'";
            filter = "";
            data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/categories/" + categoryId, filter, token, pageNumber);

            System.out.println(data.toPrettyString());


            // Get the resutls for a class.appConfig

            filter = "";
            data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/classes/" + classId + "/results", filter, token, pageNumber);

            System.out.println(data.toPrettyString());





            //filter = "status='active'/orgs?type='school'";
            filter = "";
            data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/lineItems", filter, token, pageNumber);

            System.out.println(data.toPrettyString());


        

            filter = "status='active'/orgs?type='school'";
            filter = "status='active'";
            data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/results", filter, token, pageNumber);

            while ( data.size() > 0) {
                
                System.out.println("Got Data");
                System.out.println(data.toPrettyString());
                

                // if (data.isArray()) {
                //     ArrayNode arrayNode = (ArrayNode) data;
                //     for (JsonNode orgsNode: arrayNode) {

                //         String sourceId = orgsNode.get("sourcedId").asText();
                //         String name = orgsNode.get("name").asText();

                //         String identifier = orgsNode.get("identifier").asText();
                //         // 

                //         i.importRepo.saveSchool(sourceId, name, identifier);
                //         schoolCount++;

                    
                //     }
                        
                    
                // }
                // else {
                //     System.out.println("Not Array");
                // }

                // next page
                pageNumber++;

                System.out.println("Getting results page : " + pageNumber);
                data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/results", filter, token, pageNumber);


            }
            System.out.println ("results Imported: " + schoolCount);
            i.importRepo.logInfo("results Imported: " + schoolCount);


*/

            // First the orgs
            System.out.println("Getting Schools");

            pageNumber = 0;
            studentCount = 0;

        

            filter = "status='active'/orgs?type='school'";
            filter = "status='active'";
            data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/schools", filter, token, pageNumber);

            while ( data.size() > 0) {
                    
                

                if (data.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) data;
                    for (JsonNode orgsNode: arrayNode) {

                        String sourceId = orgsNode.get("sourcedId").asText();
                        String name = orgsNode.get("name").asText();

                        String identifier = orgsNode.get("identifier").asText();
                        // 

                        i.importRepo.saveSchool(sourceId, name, identifier);
                        schoolCount++;

                    
                    }
                        
                    
                }
                else {
                    System.out.println("Not Array");
                }

                // next page
                pageNumber++;

                System.out.println("Getting Orgs page : " + pageNumber);
                data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/schools", filter, token, pageNumber);


            }
            System.out.println ("Schools Imported: " + schoolCount);
            i.importRepo.logInfo("Schools Imported: " + schoolCount);

 System.out.println("Getting USERS");

            pageNumber = 0;
            studentCount = 0;
            guardianCount = 0;
            teacherCount = 0;

            int tempCount = 0;
            //String filter = "status='active'/users?role='guardian'";
            filter = "status='active'";

            data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/users", filter, token, pageNumber);

            //data = i.skywardOneRosterService.fetchResourcePage( apiBase + "users", token, pageNumber);


            // *** The student will work if we just load student via:
            // vhttps://sandbox.skyward.com/BoscoK12SandboxAPI/ims/oneroster/v1p1/students


            while ( data.size() > 0) {

                System.out.println("Users : "  + pageNumber + " Size : " + data.size());

                if (data.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) data;
                    for (JsonNode userNode: arrayNode) {

                        tempCount++;
                        switch (userNode.get("role").asText()) {
                            case "role":
                                // just the header.
                                break;
                            case "student":

                            //System.out.print ("Student: ");


                            //String id = userNode.get("identifier").asText();
                            //System.out.println(id);
                                // ** So, there is orgs array.  Has "Entity_2"  this is the school.


                            //System.out.println(userNode.toPrettyString());

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

                                // for Ciint we are getting USRstudent231311 or Student_XXXXX


                                // String sourceId, String studentNumber, String firstName, String lastName, String grade, String schoolSourceId
                                i.importRepo.saveStudent(
                                    userNode.get("sourcedId").asText(), userNode.get("identifier").asText(),  userNode.get("givenName").asText(),  
                                        userNode.get("familyName").asText(),
                                        grade, schoolSourceId
                                );
                                studentCount++;
                                break;
                        
                            case "guardian":


                            System.out.print ("Guardian: ");
                                // If we are using useSkywardSpEd then we will get the guardians THERE.
                                if (!useSkywardSpEd) {

                                    // we only pull in if the email is not null or empty
                                    String email = userNode.get("email").asText();
                                System.out.println(email);


                                    String guardianType = "U";
                                    if (email != null && !email.isEmpty()) {

                                        if (setNoEmails && email.length() >= 4) {
                                            String trimedEmail = email.substring(0, email.length() - 4);
                                            email = trimedEmail + "_no.no";
                                        }


                                        JsonNode agentNodes = userNode.get("agents");

                                        if (agentNodes.isArray()) {
                                            for (JsonNode studentNode: agentNodes) {
                                                

                                                String studentId = studentNode.get("sourcedId").asText();
                                                
                                                //Guardian g = new Guardian(userNode.get("sourcedId").asText(), userNode.get("identifier").asText(), studentId, userNode.get("givenName").asText(), userNode.get("familyName").asText(), email, guardianType);

                                                // String sourceId, String guardianId, String studentId, String firstName, String lastName, String email, String type
                                                // String sourceId, String guardianId, String studentSourceId, String firstName, String lastName, String email, String type
                                                i.importRepo.saveGuardian(
                                                    userNode.get("sourcedId").asText(), userNode.get("identifier").asText(), studentId, userNode.get("givenName").asText(), userNode.get("familyName").asText(), email, guardianType
                                                );
                                                guardianCount++;



                                            }
                                        }
                                    }
                                }
                                break;
                            case "teacher":

                                //System.out.print ("Teacher: ");

                                // some do not have identifier
                                JsonNode teacherIdentifier = userNode.get("identifier");

                                if (teacherIdentifier != null) {

                                    JsonNode teacherEmailNode = userNode.get("email");
                                    String teacherEmail = "";
                                    if (teacherEmailNode != null) {
                                        teacherEmail = userNode.get("email").asText();


                                        //System.out.println(teacherEmail);
                                    }
                                    // else {
                                    //     //System.out.println("NO EMAIL");
                                    //     System.out.println(userNode.toPrettyString());
                                    // }

                                    if (!teacherEmail.isBlank()) {

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

                                        // We now have the teacher school in tschoolSourceId

                                        // For Bosco Test district, all users have the same email.

                                        // this should fix it.

                                        if (districtId == 9999998) {
                                            // this is the generic email they use, so we will crate our own.
                                            if (teacherEmail.equals("staff@pssis.com")) {
                                                teacherEmail = userNode.get("givenName").asText() + "." + userNode.get("familyName").asText() + "_" + userNode.get("identifier").asText()  + "@PST_no.no";
                                            }
                                        }
                                        else {


                                            if (setNoEmails && teacherEmail.length() >= 4) {
                                                String trimedEmail = teacherEmail.substring(0, teacherEmail.length() - 4);
                                                teacherEmail = trimedEmail + "_no.no";
                                            }
                                        }

                                        // sourceid, teacherId, firstname, lastname,  email
                                        //Teacher t = new Teacher(userNode.get("sourcedId").asText(), userNode.get("identifier").asText(), userNode.get("givenName").asText(),  userNode.get("familyName").asText(), teacherEmail);

                                        // String sourceid, String teacherId, String firstname, String lastname, String email
                                        // String sourceId, String teacherId, String firstName, String lastName, String email
                                        i.importRepo.saveTeacher(
                                            userNode.get("sourcedId").asText(), userNode.get("identifier").asText(), userNode.get("givenName").asText(),  userNode.get("familyName").asText(), 
                                            teacherEmail, tschoolSourceId
                                        );
                                        teacherCount++;
                                    }

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


                // WE ARE getting 5000.
                System.out.println ("We got " + tempCount + " for page : " + pageNumber);

                tempCount = 0;
                // next page
                pageNumber++;

                System.out.println("Getting Page : " + pageNumber);
                data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/users", filter, token, pageNumber);

            }
            i.importRepo.logInfo ("Saved Students: " + studentCount);
            i.importRepo.logInfo ("Saved Guardians: " + guardianCount);
            i.importRepo.logInfo ("Saved Teachers: " + teacherCount);
            

            

            
            // now the classes
            System.out.println("Getting Enrollments");

            pageNumber = 0;
            studentCount = 0;
            teacherCount = 0;

            filter = "status='active'";
            data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/enrollments", filter, token, pageNumber);



            while ( data.size() > 0) {
                    
                

                if (data.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) data;
                    for (JsonNode enrollmentNode: arrayNode) {

                        String role = enrollmentNode.get("role").asText();

                        String classSourceId = "";
                        String userSourceId = "";


                        JsonNode classNode = enrollmentNode.get("class");
                        classSourceId = classNode.get("sourcedId").asText();

                        // we can get school here.

                        String schoolSourceId = "";
                        JsonNode schoolNode = enrollmentNode.get("school");
                        schoolSourceId = schoolNode.get("sourcedId").asText();


                        JsonNode userNode = enrollmentNode.get("user");
                        userSourceId = userNode.get("sourcedId").asText();
                        
                        if (userSourceId != "" && classSourceId != "") {

                            // TODO: so here we need to get the school code for teacher and student.
                            // look at the api and see if we have alternatives.
                            switch (role) {
                                case "teacher":
                                    i.importRepo.saveTeacherClass(userSourceId, schoolSourceId + classSourceId);
                                    teacherCount++;
                                    break;
                                case "student":
                                    i.importRepo.saveStudentClass(userSourceId, schoolSourceId + classSourceId);
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

                data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/enrollments", filter, token, pageNumber);


            }

            i.importRepo.logInfo ("Saved StudentClasses: " + studentCount);
            i.importRepo.logInfo ("Saved TeacherClasses: " + teacherCount);

      

            // THIS DOES NOT WORK
            
              if (!useSkywardSpEd) {

                System.out.println("Getting Student Demographics");

                pageNumber = 0;
                studentCount = 0;


                filter = "status='active'";
                data = i.skywardOneRosterService.fetchResourcePageWithFilter1000( apiBase + "ims/oneroster/v1p1/demographics", filter, token, pageNumber);

                while ( data.size() > 0) {
                        
                    

                    if (data.isArray()) {
                        ArrayNode arrayNode = (ArrayNode) data;
                        for (JsonNode demographicsNode: arrayNode) {

                            String sourceId = demographicsNode.get("sourcedId").asText();

                            //System.out.println(sourceId);

                            // SO this is not true for Clint.
                            if (sourceId.toLowerCase().contains("student"))
                            //if (sourceId.startsWith("Student_")) 
                            {
                                //System.out.println("Update Student : " + sourceId);

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

                                i.importRepo.saveStudentDemographics(
                                    studentNumber,
                                    birthDate,
                                    demographicsNode.get("sex").asText(),
                                    Boolean.parseBoolean(demographicsNode.get("americanIndianOrAlaskaNative").asText()),
                                    Boolean.parseBoolean(demographicsNode.get("asian").asText()),
                                    Boolean.parseBoolean(demographicsNode.get("blackOrAfricanAmerican").asText()),
                                    Boolean.parseBoolean(demographicsNode.get("nativeHawaiianOrOtherPacificIslander").asText()),
                                    Boolean.parseBoolean(demographicsNode.get("white").asText()),
                                    Boolean.parseBoolean(demographicsNode.get("hispanicOrLatinoEthnicity").asText())
                                    
                                );

                                studentCount++;
                            }
                            
                
                        

                        }
                    
                        
                    }
                    else {
                        System.out.println("Not Array");
                    }

                    // next page
                    pageNumber++;

                    System.out.println("Getting Demographics page : " + pageNumber);
                    data = i.skywardOneRosterService.fetchResourcePageWithFilter1000( apiBase + "ims/oneroster/v1p1/demographics", filter, token, pageNumber);


                }
                i.importRepo.logInfo ("Demographics updated: " + studentCount);

            }

            
            

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


            i.importRepo.logInfo("Import " + importDefId + " () Complete in : " + duration.toSeconds() + " Seconds" );
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



