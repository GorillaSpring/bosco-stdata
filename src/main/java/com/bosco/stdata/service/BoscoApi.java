package com.bosco.stdata.service;


import org.springframework.stereotype.Service;

import com.bosco.stdata.model.*;
import com.bosco.stdata.repo.ImportRepo;
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
    

    BoscoApi(ImportRepo importRepo, EmailService emailService) {
        this.importRepo = importRepo;
        this.emailService = emailService;
        
        
    }


    public Boolean sendImportToBosco (int importId, int baseImportId) throws IOException {

        // for now we just write to a file
        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

        String outFileName = "c:/test/importLog/bosco_sync" + dateFolder + ".txt";

        System.out.println("Generating log file : " + outFileName);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();


        // if there are any new or changed schools, we will simply send an email.

        String newSchools = "<ul>";
        Boolean areNewSchools = false;


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
                }

                writer.write("------------ CHANGED SCHOOLS ----------------\n");
                schools = importRepo.schoolsBoscoForExport(importId, 1);
                for (School bs : schools) {
                    areNewSchools = true;
                    newSchools += "<li> CHANGED: " + bs.getSchoolCode() + " - " + bs.getName() + "</li>\n";

                    writer.write(ow.writeValueAsString(bs));
                    writer.write("\n");
                }


                writer.write("------------ NEW TEACHERS ----------------\n");

                List<Teacher> teachers = importRepo.teacherBoscoGetForExport(importId, 2);
                for (Teacher bu : teachers) {
                    writer.write(ow.writeValueAsString(bu));
                    writer.write("\n");
                }

                writer.write("------------ CHANGED TEACHERS ----------------\n");
                teachers = importRepo.teacherBoscoGetForExport(importId, 1);
                for (Teacher bu : teachers) {
                    writer.write(ow.writeValueAsString(bu));
                    writer.write("\n");
                }
                

                writer.write("------------ NEW STUDENTS ----------------\n");
                List<Student> bss = importRepo.studentsBoscoForExport(importId, 2);

                for (Student bs : bss) {

                    
                    bs.setGuardians(importRepo.guardiansBoscoForStudent(importId, bs.getStudentId()));
                    bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(importId, bs.getStudentId()));

                    // make this pretty json and log.

                    writer.write(ow.writeValueAsString(bs));
                    writer.write("\n");


                }


                writer.write("------------ CHANGED STUDENTS ----------------\n");
                bss = importRepo.studentsBoscoForExport(importId, 1);

                for (Student bs : bss) {

                    
                    bs.setGuardians(importRepo.guardiansBoscoForStudent(importId, bs.getStudentId()));
                    bs.setTeacherIds(importRepo.teacherIdsBoscoForStudent(importId, bs.getStudentId()));

                    // make this pretty json and log.

                    writer.write(ow.writeValueAsString(bs));
                    writer.write("\n");


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
                    }

                    writer.write("------------ DELETED STUDENTS ----------------\n");

                    dss = importRepo.studentIdsDeletedFromImport(importId, baseImportId);
                    for (String ds : dss) {
                        writer.write(ds + "\n");
                    }

                }

                writer.write("---------------------------------\n");
            }
        

            newSchools += "</ul>";


            if (areNewSchools) {
                System.out.println("Sending new or changed schools email");
                emailService.sendSimpleMessage("BenLevy3@gmail.com",  "New or Changed Schools", newSchools);
            }

        
        






        return true;

    }

}
