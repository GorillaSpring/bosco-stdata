package com.bosco.stdata.distictImports;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.bosco.stdata.config.AppConfig;
import com.bosco.stdata.model.ImportDefinition;
import com.bosco.stdata.model.ImportResult;
import com.bosco.stdata.model.ImportSetting;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.service.BoscoClient;
import com.bosco.stdata.service.SkywardOneRosterService;
import com.bosco.stdata.service.SkywardTokenService;
import com.bosco.stdata.utils.ImportHelper;
import com.bosco.stdata.utils.MappingHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import jakarta.annotation.PostConstruct;

@Component
public class SkywardOneRosterApi {

    

    

    private final BoscoClient boscoClient;

    private final AppConfig appConfig;

    @Autowired
    ImportRepo importRepo;

    
    @Autowired 
    BoscoApi boscoApi;

    @Autowired
    SkywardTokenService tokenService;

    @Autowired
    SkywardOneRosterService skywardOneRosterService;

    private static SkywardOneRosterApi i;  // instance

    

    static int districtId = 0;
	static String clientId = "";
    static String clientSecret = "";
	static String tokenUrl = "";

    static String apiBase = "";

    static Boolean useSkywardSpEd = false;  
    //static Boolean tempSkipSkyward = true;

   SkywardOneRosterApi(AppConfig appConfig, BoscoClient boscoClient) {
        this.appConfig = appConfig;
        this.boscoClient = boscoClient;
    }

    @PostConstruct
    public void init() {
        System.out.println("SkywardOneRosterApi - init()");
        i = this;
    }


    // NOT CURRENTLY USING.  STILL NEED TO SORT.

    // THIS IS ALL DONE BELOW.

    

    private static void GetDemographisViaSpedApi (String token, Boolean setNoEmails) throws Exception{
        System.out.println("Getting Student Demographics via SpecialEducation");
        JsonNode data;

        int demoCount = 0;
        int guardianCount = 0;
                 
        List<String> studentSourceIds =i.importRepo.studentSourceIdsForDistrict(districtId);

        //studentNumbers.add("218879766");

        for (String studentSourceId: studentSourceIds) {
            //data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/schools", filter, token, pageNumber);

            // trim the Student_ from it
            String[] sss = studentSourceId.split("_");

           // try {
                // https://sandbox.skyward.com/BoscoK12SandboxAPI/SpecialEducation/SpecialEducationEnrollmentTX/GetByStudent/25
                // https://sandbox.skyward.com/BoscoK12SandboxAPI/SpecialEducation/SpecialEducationEnrollmentTX/GetByStudent/25

                
                data = i.skywardOneRosterService.fetchSkywardApi(apiBase + "SpecialEducation/StudentDemographic/1/" + sss[1], token);

                //System.out.println("\nFOUND for student: " + studentSourceId);


                JsonNode sdNode = data.get("StudentDemographics");
                if (sdNode != null && sdNode.isArray()  && sdNode.size() > 0) {
                    

                        JsonNode sdData = sdNode.get(0);
                        

                        //System.out.println("ARRAY");
                        // do it this way..

                        // JsonNode theNode = arrayNode.get(0);

                        String studentNumber = sdData.get("StudentNumber").asText();
                        String dateOfBirth = sdData.get("DateOfBirth").asText();
                        String gender = sdData.get("Gender").asText();

                        String dob = ImportHelper.DateToStdFormat(dateOfBirth);

                        //System.out.println("StudentNumber : " + studentNumber);

                        // SEE WHAT WE NEED.

                        // We should gust get this and the guardian.

                        // be sure we get for EVERY student.


                        demoCount++;
                        
                        i.importRepo.saveStudentDemographics(
                            studentNumber,
                            dob,
                            gender,
                            Boolean.parseBoolean(sdData.get("IsAmericanIndianOrAlaskanNative").asText()),
                            Boolean.parseBoolean(sdData.get("IsAsian").asText()),
                            Boolean.parseBoolean(sdData.get("IsBlackOrAfricanAmerican").asText()),
                            Boolean.parseBoolean(sdData.get("IsNativeHawaiianOrOtherPacificIslander").asText()),
                            Boolean.parseBoolean(sdData.get("IsWhite").asText()),
                            Boolean.parseBoolean(sdData.get("IsHispanic").asText())
                        );

                        // TODO: save Boolean.parseBoolean(sdData.get("IsLEP").asText())
                    

                        // now parents

                        JsonNode parentsNode = sdData.get("Parents");
                        if (parentsNode != null && parentsNode.isArray() && parentsNode.size() > 0) {
                            // we found some parents.

                            ArrayNode arrayNode = (ArrayNode) parentsNode;
                            for (JsonNode parentNode: arrayNode) { 
                                String localId = parentNode.get("LocalID").asText();


                                String firstName = parentNode.get("FirstName").asText();
                                String lastName = parentNode.get("LastName").asText();
                                JsonNode relationshipNode = parentNode.get("Relationship");


                                String guardianType = "O";
                                if (relationshipNode != null && !relationshipNode.isNull())
                                    guardianType = MappingHelper.GuardianTypeFromString(relationshipNode.asText());

                                JsonNode emailNode = parentNode.get("EmailAddress");
                                String email = "";
                                if (emailNode != null && !emailNode.isNull()) {
                                    email = emailNode.asText();

                                    if (setNoEmails && email.length() >= 4) {
                                        String trimedEmail = email.substring(0, email.length() - 4);
                                        email = trimedEmail + "_no.no";
                                    }


                                }

                                String guardianId = "Guardian_" + localId;
                                
                                
                                // System.out.println("Got Parent: " + localId + " - " + firstName + " - " + email);



                                i.importRepo.saveGuardian(
                                            guardianId, 
                                            guardianId, 
                                            studentSourceId,                        // good.
                                            firstName, 
                                            lastName, 
                                            email, 
                                            guardianType
                                        );
                                        guardianCount++;

                                                
                            }

                        }

                        // see one note for the resulst and to pare.

                        
                    
                }

                else {
                    throw new Exception ("Invalid StudentDemographics from API : " + studentSourceId);
                }
                //System.out.println(data.toPrettyString());


            // }
            // catch (Exception ex) {
    
            //     System.out.print(ex.getMessage());
            //     // System.out.println("Did not find for student: " + studentSourceId);
            //     //  System.out.println(ex.toString());

            //     return true;
            // }
        }

        System.out.println ("Imported Student Demographics: " + demoCount + "  AND Guardians: " + guardianCount);

        
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

            useSkywardSpEd = Boolean.parseBoolean(ImportHelper.ValueForSetting(importSettings, "useSkywardSpEd"));

            LocalDateTime startDateTime = LocalDateTime.now();

            
            int importId = i.importRepo.prepImport(districtId, importDefId, isRoster, isSisData,  "SkywardOneRosterApi ");
            
            result.importId = importId;
            result.districtId = districtId;

            i.importRepo.logInfo("OneRoster API import : " + importDefId);


            System.out.println("Import For District " + districtId);

            



            System.out.println("Import One Roster Start");

            String token = i.tokenService.getAccessToken(clientId, clientSecret, tokenUrl);

// GetDemographisViaSpedApi(token);
            

            JsonNode data;

            
            int pageNumber = 0;

            
            // users

            pageNumber = 0;

            int studentCount = 0;
            int guardianCount = 0;
            int teacherCount = 0;



            // First the orgs
            System.out.println("Getting Schools");

            pageNumber = 0;
            studentCount = 0;

            String filter = "";

            int schoolCount = 0;

            filter = "status='active'/orgs?type='school'";
            filter = "status='active'";
            // data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/schools", filter, token, pageNumber);

            // while ( data.size() > 0) {
                    
                

            //     if (data.isArray()) {
            //         ArrayNode arrayNode = (ArrayNode) data;
            //         for (JsonNode orgsNode: arrayNode) {

            //             String sourceId = orgsNode.get("sourcedId").asText();
            //             String name = orgsNode.get("name").asText();

            //             String identifier = orgsNode.get("identifier").asText();
            //             // 

            //             i.importRepo.saveSchool(sourceId, name, identifier);
            //             schoolCount++;

                    
            //         }
                        
                    
            //     }
            //     else {
            //         System.out.println("Not Array");
            //     }

            //     // next page
            //     pageNumber++;

            //     System.out.println("Getting Orgs page : " + pageNumber);
            //     data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/schools", filter, token, pageNumber);


            // }
            // System.out.println ("Schools Imported: " + schoolCount);
            // i.importRepo.logInfo("Schools Imported: " + schoolCount);


            System.out.println("Getting USERS");

            pageNumber = 0;
            studentCount = 0;
            guardianCount = 0;
            teacherCount = 0;
            //String filter = "status='active'/users?role='guardian'";
            filter = "status='active'";

            data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/users", filter, token, pageNumber);

            //data = i.skywardOneRosterService.fetchResourcePage( apiBase + "users", token, pageNumber);


            // *** The student will work if we just load student via:
            // vhttps://sandbox.skyward.com/BoscoK12SandboxAPI/ims/oneroster/v1p1/students


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
                                // If we are using useSkywardSpEd then we will get the guardians THERE.
                                if (!useSkywardSpEd) {

                                    // we only pull in if the email is not null or empty
                                    String email = userNode.get("email").asText();
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

                                String teacherEmail = userNode.get("email").asText();


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


                                    if (setNoEmails && teacherEmail.length() >= 4) {
                                        String trimedEmail = teacherEmail.substring(0, teacherEmail.length() - 4);
                                        teacherEmail = trimedEmail + "_no.no";
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

      


            // now the demographics

            // So we can get all of this + more from the
            // 'https://sandbox.skyward.com/BoscoK12SandboxAPI/SpecialEducation/StudentDemographic/1/25'
            // these are one offs, and should be run only as needed (new students)
            // maybe updated once a week or so.


            // if we use the SkywardSpEd, this is done at the END of the imports
            // we only need to do for NEW studens.

            //if (tempSkipSkyward) {
            if (!useSkywardSpEd) {

                System.out.println("Getting Student Demographics");

                pageNumber = 0;
                studentCount = 0;


                filter = "status='active'";
                data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/demographics", filter, token, pageNumber);

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
                    data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/demographics", filter, token, pageNumber);


                }
                i.importRepo.logInfo ("Demographics updated: " + studentCount);

            }

      // build the student teacher table

            System.out.println("Calling buildStudentTeacher();");

            i.importRepo.buildStudentTeacher();
            


        // testing getting SpEd stuff.

            if (useSkywardSpEd) {


/*
                //JsonNode data;
                List<String> studentSourceIds =i.importRepo.studentSourceIdsForDistrict(districtId);

                //studentNumbers.add("218879766");

                for (String studentSourceId: studentSourceIds) {
                    //data = i.skywardOneRosterService.fetchResourcePageWithFilter( apiBase + "ims/oneroster/v1p1/schools", filter, token, pageNumber);

                    // trim the Student_ from it

                    // for Clint we have some that are USRstudent231311

                    // Student_10009

                    String studentId = studentSourceId.replace("Student_", "").replace("USRstudent", "");



                    // String[] sss = studentSourceId.split("_");

                    // if (sss.length > 1)
                    //     studentId = sss[1];
                    // else
                    //     studentId = student


                    try {
                        // https://sandbox.skyward.com/BoscoK12SandboxAPI/SpecialEducation/SpecialEducationEnrollmentTX/GetByStudent/25
                        // https://sandbox.skyward.com/BoscoK12SandboxAPI/SpecialEducation/SpecialEducationEnrollmentTX/GetByStudent/25

                        
                        data = i.skywardOneRosterService.fetchSkywardApi(apiBase + "SpecialEducation/SpecialEducationEnrollmentTX/GetByStudent/" + studentId, token);

                        //System.out.println("\nFOUND for student: " + studentSourceId);

                        //System.out.println(data.toPrettyString());

                        // now lets try to parse it
                        if (data.isArray()) {
                            ArrayNode arrayNode = (ArrayNode) data;
                            

                            // do it this way..

                            JsonNode theNode = arrayNode.get(0);

                            if (theNode != null && !theNode.isNull()) {

                                // String stateInstructionalSettingCode = theNode.get("StateInstructionalSettingCode").asText();

                                // System.out.println("StateInstructionalSettingCode : " + stateInstructionalSettingCode);


                                System.out.println("Adding SpEd " + studentSourceId);

                                String stateInstructionalSettingCode = "";
                                String stateChildCountFundCode = "";
                                int specialEducationEnrollmentTXID = 0;
                                String startDate = "";
                                String endDate = "";

                                Boolean multiplyDisabled = false;

                                String entryComment = "";
                                String exitComment = "";

                                JsonNode getIfExistsNode = theNode.get("EndDate");
                                if (getIfExistsNode != null && !getIfExistsNode.isNull()) {
                                    endDate = getIfExistsNode.asText();
                                }

                                getIfExistsNode = theNode.get("StartDate");
                                if (getIfExistsNode != null && !getIfExistsNode.isNull()) {
                                    startDate = getIfExistsNode.asText();
                                }


                                getIfExistsNode = theNode.get("StateInstructionalSettingCode");
                                if (getIfExistsNode != null && !getIfExistsNode.isNull()) {
                                    stateInstructionalSettingCode = getIfExistsNode.asText();
                                }

                                 getIfExistsNode = theNode.get("StateChildCountFundCode");
                                if (getIfExistsNode != null && !getIfExistsNode.isNull()) {
                                    stateChildCountFundCode = getIfExistsNode.asText();
                                }

                                getIfExistsNode = theNode.get("SpecialEducationEnrollmentTXID");
                                if (getIfExistsNode != null && !getIfExistsNode.isNull()) {
                                    specialEducationEnrollmentTXID = Integer.parseInt(getIfExistsNode.asText());
                                }
                                //String endDate = theNode.get("EndDate").asText();

                                
                                getIfExistsNode = theNode.get("MultiplyDisabled");
                                if (getIfExistsNode != null && !getIfExistsNode.isNull()) {
                                    multiplyDisabled = Boolean.parseBoolean(getIfExistsNode.asText());
                                }

                                getIfExistsNode = theNode.get("EntryComment");
                                if (getIfExistsNode != null && !getIfExistsNode.isNull()) {
                                    entryComment = getIfExistsNode.asText();
                                }

                                getIfExistsNode = theNode.get("ExitComment");
                                if (getIfExistsNode != null && !getIfExistsNode.isNull()) {
                                    exitComment = getIfExistsNode.asText();
                                }

                                
                                Boolean changed = i.importRepo.saveStudentSped(districtId, studentSourceId, 
                                    stateInstructionalSettingCode,
                                    stateChildCountFundCode,
                                    specialEducationEnrollmentTXID,
                                    startDate,
                                    
                                    endDate,
                                    multiplyDisabled,
                                    entryComment,
                                    exitComment
                                    
                                
                                );

                                if (changed)
                                    System.out.println("CHANGED SPED " + studentSourceId);

                                // see one note for the resulst and to pare.

                            }
                        }

                    }
                    catch (HttpClientErrorException  hcee) {
                        // System.out.println(".");
                        // this is expected. not found!

                        // if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        //                 System.err.println("Error 404: Resource not found at " + url);
                        //                 // Implement specific 404 handling logic here
                        //             } else {
                        //                 System.err.println("HTTP Client Error: " + e.getStatusCode() + " - " + e.getMessage());
                        //             }

                    }
                    catch (Exception ex) {
            
                        // this is another exception (ie bad data in Json Node)
                        System.out.println(studentSourceId);
                        System.out.println(ex.toString());
                        // System.out.println("Did not find for student: " + studentSourceId);
                        //  System.out.println(ex.toString());
                    }
                }   // End of useSky.... 
                

*/

                // and now
                
                GetDemographisViaSpedApi (token, setNoEmails);

            }


              
            //i.importRepo.diffImports(baseImportId);

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
