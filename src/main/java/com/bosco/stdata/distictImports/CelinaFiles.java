// package com.bosco.stdata.distictImports;

// import java.io.FileNotFoundException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

// import com.bosco.stdata.model.*;
// import com.bosco.stdata.repo.ImportRepo;
// import com.bosco.stdata.service.BoscoApi;
// import com.bosco.stdata.service.UserFileService;
// import com.bosco.stdata.utils.ImportHelper;

// import jakarta.annotation.PostConstruct;

// @Component

// public class CelinaFiles {
    
//     @Autowired
//     ImportRepo importRepo;

//     @Autowired 
//     BoscoApi boscoApi;
    

//     private static CelinaFiles i;  // instance

//     @PostConstruct
//     public void init() {
//         System.out.println("TestFiles - init()");
//         i = this;
//     }

//     public static ImportResult Import(String importDefId)  {

// 		// This is now not going to be the roster stuff.  Just the 
// 		//  map
// 		// telpass (weired one.)


//         ImportResult result = new ImportResult();
//         try {
//               ImportDefinition importDef = i.importRepo.getImportDefinition(importDefId);

//             int baseImportId = importDef.getBaseImportId();

//             List<ImportSetting> importSettings = i.importRepo.getImportSettings(importDefId);

//             int districtId = importDef.getDistrictId();
//             int importId = i.importRepo.REMOVE_prepImport(districtId, "Import for " + importDefId);

//             result.importId = importId;
//             result.districtId = districtId;
//             result.baseImportId = baseImportId;
            
// 		    //String baseFileFolder = "C:/test/uplift/" + subFolder + "/";
//             String baseFileFolder = ImportHelper.ValueForSetting(importSettings, "baseFolder");

//             String archiveFolder =  ImportHelper.ValueForSetting(importSettings, "archiveFolder");

// 		    // Before we start, lets make sure there are files in the baseFolder
//             String[] files = {"demographics.csv", "enrollments.csv", "orgs.csv", "users.csv"};
//             if (!ImportHelper.CheckFilesExist(baseFileFolder, files)) {
//                 throw new FileNotFoundException("One or more import files missing!");
//             }


//             // TODO: lets do for the list of expected files;
//             Path filePath = Paths.get(baseFileFolder + "orgs.csv");
//             if (!Files.exists(filePath)) {
//                 throw new FileNotFoundException("Import File not found : " + filePath);
//             }

// 			 LocalDateTime startDateTime = LocalDateTime.now();
            
//             System.out.println("Import Id is : " + importId + " For District " + districtId);

// 		    UserFileService msp = new UserFileService();

//             int counter1 = 0;

// 			System.out.println("Importing Orgs File");

// 			List<String[]> data = msp.readCsvFile( baseFileFolder + "orgs.csv");

// 			// sourcedId			0
// 			// status				1
// 			// dateLastModified		2
// 			// name					3
// 			// type					4		school
// 			// identifier			5
// 			// parentSourcedId		6

// 			String[] fr = data.removeFirst();

// 			String[] colNames = new String[]{"sourcedId", "status", "dateLastModified", "name", "type", "identifier", "parentSourcedId"};
            
//             if (!ImportHelper.CheckColumnHeaders(fr, colNames))
//                 throw new Exception("File : orgs.csv does not match column specs" );


// 			for (String [] row : data) {

//                 if (!row[0].isBlank()) 
//                 {
// 					if (row[4].equals("school")) {
// 						i.importRepo.saveSchool(row[0], row[3], row[0]);
// 	    	            counter1++;

// 					}
// 				}

// 			};

//   			i.importRepo.logInfo("Imported Schools : " + counter1);





// 			System.out.println("Importing Users File");

// 			 int studentCount = 0;
//             int guardianCount = 0;
//             int teacherCount = 0;

// 			data = msp.readCsvFile( baseFileFolder + "users.csv");

// 			fr = data.removeFirst();

// 			// sourcedId	status	dateLastModified	enabledUserV1P1	orgSourcedIds	role	username	userIds	givenName	familyName	middleName	identifier	email	sms	phone	agentSourcedIds	grades	password


// 			colNames = new String[]{"sourcedId", "status", "dateLastModified", "enabledUserV1P1", "orgSourcedIds", "role", "username", "userIds", "givenName", "familyName", "middleName", "identifier", "email", "sms", "phone", "agentSourcedIds", "grades", "password"};
            
//             if (!ImportHelper.CheckColumnHeaders(fr, colNames))
//                 throw new Exception("File : users.csv does not match column specs" );


// 			// soureceId
// 			// status
// 			// dataLastModified
// 			// enabledUserV1P1
// 			// orgSourcedIds			4.   ** for students, this is the one we need.
// 			// role					5
// 			// username
// 			// userIds
// 			// givenName			8
// 			// familyName			9
// 			// middleName
// 			// identifier			11
// 			// email				12
// 			// sms
// 			// phone
// 			// agentSourcedIds		15
// 			// grades				16
// 			// password


// 			for (String [] row : data) {
//                 if (!row[0].isBlank()) 
//                 {

// 					switch (row[5]) {
						
// 						case "student":
// 								String[] orgs = ((String)row[4]).split(",");
// 								String schoolSourceId = orgs[0];


// 								//Student s = new Student(row[0], row[11], row[8], row[9], row[16], schoolCode);
// 								i.importRepo.REMOVE_saveStudent(row[0], row[11], row[8], row[9], row[16], schoolSourceId);
// 								studentCount++;
// 							break;
					
// 						case "guardian":
// 							String[] studentData = ((String)row[15]).split(",");
// 							List<String> students = Arrays.asList(studentData);
// 							for (String student : students) {
							
// 								//  sourceId, guardianId,  studentId,  firstName,  lastName,  email, type
// 								//Guardian g = new Guardian(row[0], row[11], student, row[8], row[9], row[12], "U");
// 								i.importRepo.REMOVE_saveGuardian(row[0], row[11], student, row[8], row[9], row[12], "U");
// 								guardianCount++;
// 							};
// 							break;
// 						case "teacher":
// 							// sourceid, teacherId, firstname, lastname,  email
// 							//Teacher t = new Teacher(row[0], row[11], row[8], row[9], row[12]);
// 							i.importRepo.REMOVE_saveTeacher(
// 								row[0], row[11], row[8], row[9], row[12]
// 							);
// 							teacherCount++;
							
// 							break;
// 						default:
// 							System.out.println("Found " + row[5]);
// 							break;
// 					}
// 				}
				
// 				//System.out.println(row[5]);
// 			};

//   			i.importRepo.logInfo("Imported Students : " + studentCount);
// 			i.importRepo.logInfo("Imported Guardians : " + guardianCount);
// 			i.importRepo.logInfo("Imported Teachers : " + teacherCount);



// 			// demographics
// 			System.out.println("Importing Demographics File");

// 			data = msp.readCsvFile(baseFileFolder + "demographics.csv");
// 			fr = data.removeFirst();
// 			colNames = new String[]{"sourcedId", "status", "dateLastModified", "birthDate", "sex", "americanIndianOrAlaskaNative", "asian", "blackOrAfricanAmerican", "nativeHawaiianOrOtherPacificIslander", "white", "demographicRaceTwoOrMoreRaces", "hispanicOrLatinoEthnicity", "countryOfBirthCode", "stateOfBirthAbbreviation", "cityOfBirth", "publicSchoolResidenceStatus"};
            
//             if (!ImportHelper.CheckColumnHeaders(fr, colNames))
//                 throw new Exception("File : demographics.csv does not match column specs" );


// 			// soureceId								0
// 			// status
// 			// dataLastModified  						2
// 			// birthDate								3
// 			// sex,										4
// 			// americanIndianOrAlaskaNative				5
// 			// asian									6
// 			// blackOrAfricanAmerican					7
// 			// nativeHawaiianOrOtherPacificIslander		8
// 			// white									9
// 			// demographicRaceTwoOrMoreRaces			10
// 			// hispanicOrLatinoEthnicity				11


// 			studentCount = 0;

// 			for (String [] row : data) {
// 				// we only import Student demographics.
// 				if (row[0].startsWith("Student_")) {
// 					//Demographics d = new Demographics(row[0], row[3], row[4], Boolean.parseBoolean(row[5]), Boolean.parseBoolean(row[6]), Boolean.parseBoolean(row[7]), Boolean.parseBoolean(row[8]), Boolean.parseBoolean(row[9]), Boolean.parseBoolean(row[11]));
// 					i.importRepo.REMOVE_saveStudentDemographics(row[0], row[3], row[4], Boolean.parseBoolean(row[5]), Boolean.parseBoolean(row[6]), Boolean.parseBoolean(row[7]), Boolean.parseBoolean(row[8]), Boolean.parseBoolean(row[9]), Boolean.parseBoolean(row[11]),
// 					false, false, false
					
					
// 					);
// 					studentCount++;
// 				}
// 			};

// 			i.importRepo.logInfo("Imported Student Demographics : " + studentCount);



// 			// enrollments  (Classes)
// 			System.out.println("Importing Enfollments File");

// 			data = msp.readCsvFile(baseFileFolder + "enrollments.csv");
// 			fr = data.removeFirst();

// 			colNames = new String[]{"sourcedId", "status", "dateLastModified", "classSourcedId", "schoolSourcedId", "userSourcedId", "role", "primary", "beginDate", "endDate"};
            
//             if (!ImportHelper.CheckColumnHeaders(fr, colNames))
//                 throw new Exception("File : enrollments.csv does not match column specs" );


// 			// soureceId								0
// 			// status
// 			// dataLastModified  						2
// 			// classSourceId							3
// 			// schoolSourceId							4   ** this will maps to orgs (schools)
// 			// userSourceId								5
// 			// role										6
// 			// primary									7   ** only set to TRUE for teachers.


// 			studentCount = 0;
// 			teacherCount = 0;

// 			for (String [] row : data) {
//                 if (!row[0].isBlank()) 
//                 {

// 					switch (row[6]) {
// 						case "teacher":
// 								i.importRepo.saveTeacherClass(row[5], row[3]);
// 								teacherCount++;
// 							break;
// 						case "student":
// 								i.importRepo.saveStudentClass(row[5], row[3]);
// 								studentCount++;
// 							break;
					
// 						default:
// 							// the header will be "role";
// 							System.out.println("Invalid Role " + row[6]);
// 							break;
// 					}
// 				}
// 			};


// 			i.importRepo.logInfo("Imported Student enrollments : " + studentCount);
// 			i.importRepo.logInfo("Imported Teacher enrollments : " + teacherCount);


//             // build the stuent teachedr
//             i.importRepo.buildStudentTeacher();

            


//             // Now we move the files to the archive Folder

//             ImportHelper.MoveFiles(baseFileFolder, archiveFolder);

//             i.importRepo.logInfo("Moved Files to archive");


// 			if (baseImportId == 0) {
// 				i.importRepo.logInfo("This is the BASE Import");
//                 i.importRepo.setAllNewImports();
//             }
//             else {
//                 i.importRepo.logInfo("Doing Diff with " + baseImportId);

//                 i.importRepo.diffImports(baseImportId);
//             }


            

//             // validation on the data.
//             // check number of diffs vs the cutoff.


//             // this will mark the importId as the base.
//             i.importRepo.setImportBase(importDefId);

        
//             LocalDateTime endDateTime = LocalDateTime.now();
    
//             Duration duration = Duration.between(startDateTime, endDateTime);

            
//             i.importRepo.logInfo("Import " + importDefId + " (" + importId + ") Complete in : " + duration.toSeconds() + " Seconds" );

//             System.out.println ("Import ID is: " + importId);



            


//             i.boscoApi.REMOVE_sendImportToBosco(importId, baseImportId);
//             result.success = true;

//         }
//         catch (Exception ex) {
//             i.importRepo.logError(ex.toString());
//             result.errorMessage = ex.toString();
//             result.success = false;
//             System.out.println(ex.toString());
//         }

//         return result;

//     }


// }
