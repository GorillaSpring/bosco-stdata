package com.bosco.stdata.repo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import com.bosco.stdata.model.*;
import com.bosco.stdata.teaModel.Star2024;


@Repository
public class ImportRepo {

    private JdbcTemplate template;

    private int districtId;   // This is the district we are working on.  Will be used for repo saves
    private int importId;     //  This is the particular import Id  (ie the run for the district)
    

    ImportRepo() {}

    // we do this if we reload the bosco imports from the api.
    // just for logging.
    public void setImportId(int importId) {
        this.importId = importId;
    }

    public JdbcTemplate getTemplate() {
        return template;
    }

    @Autowired
    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }


    //#region Logs

    public List<ImportLog> getInfoLogs(int importId) {
        Object[] args = {
            //forDistrictId,
            importId
        };

        String sql = """
           select * from log_info where importId = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(ImportLog.class), args);
    }


     public List<ImportLog> getErrorLogs(int importId) {
        Object[] args = {
            //forDistrictId,
            importId
        };

        String sql = """
           select * from log_error where importId = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(ImportLog.class), args);
    }
    //#endregion

    //#region Testing

    

    //#endregion

    //#region Import System

    // public int getSystemStatus (String key) {
    //     String sql = "select status from system_status where systemKey = ?;";

    //     int status = template.queryForObject(
    //             sql, 
    //             Integer.class, 
    //             key);

    //     return status;
    // }

    // public void setSystemStatus (String key, int status) {

    //     Object[] args = {
    //         status,
    //         key
    //     };

    //     String sql = "update system_status set status=? where systemKey = ?;";

    //     int rows = template.update(sql, args);
    // }

    
    public void setImportDefActive (String id, Boolean active) {

        Object[] args = {
            active,
            id

        };

        String sql = "update import_definition set active=? where id = ?;";

        int rows = template.update(sql, args);
    }


    

    //#endregion

    public void setMapCourseCsaCode (int forDistrictId, String courseName, String csaCode) {
        Object[] args = {
            forDistrictId,
            courseName, 
            csaCode,
            csaCode
        };

        String sql = """
            insert into
                map_course_csacode (districtId, courseName, csaCode)
            values (?, ?, ?)
            on duplicate key update
                csaCode = ?
                """;

        int rows = template.update(sql, args);

    }


   
    public List<SisGrades> sisGradesGet (String id) {
          Object[] args = {
            //forDistrictId,
            id
        };

        String sql = """
           select 
                schoolYear,
                period,
                code,
                subject,
                score,
                csaCode
            

            from 
                sis_grade 
            where 
                id = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(SisGrades.class), args);
    }

    public List<SisMap> sisMapsGet (String id) {
          Object[] args = {
            //forDistrictId,
            id
        };

        String sql = """
           select 
                schoolYear,
                period,
                subject,
                proficiency,
                proficiencyCode,
                score,
                csaCode

            from 
                sis_map 
            where 
                id = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(SisMap.class), args);
    }

   


     public List<SisMclass> sisMclassGet (String id) {
          Object[] args = {
            //forDistrictId,
            id
        };

        String sql = """
           select 
                schoolYear,
                period,
                subject,
                proficiency,
                proficiencyCode,
                score,
                csaCode

            from 
                sis_mclass 
            where 
                id = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(SisMclass.class), args);
    }

     public List<SisStaar> sisStaarsGet (String id) {
          Object[] args = {
            //forDistrictId,
            id
        };

        String sql = """
           select 
                schoolYear,
                subject,
                code,
                grade,
                proficiency,
                proficiencyCode,
                csaCode
            from 
                sis_staar 
            where 
                id = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(SisStaar.class), args);
    }


    public List<SisTelpas> sisTelpasGet (String id) {
          Object[] args = {
            //forDistrictId,
            id
        };

        String sql = """
           select 
               *
            from 
                sis_telpas
            where 
                id = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(SisTelpas.class), args);
    }


    public List<SisDisciplineHelper> sisDisciplinesGet (int forDistrictId, String id) {
          Object[] args = {
            //forDistrictId,
            id
        };


        // check how used and what to add.
        String sql = """    
           select * from sis_discipline where id = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(SisDisciplineHelper.class), args);
    }


    public String studentNumberFromDemographics (int forDistrictId, String firstName, String lastName, String dob ) {

        
        Object[] args = {
            forDistrictId,
            firstName,
            lastName,
            dob

        };

        String sql = """
                
                select
                    s.studentNumber
                from
                    student s
                where
                    s.districtId = ?
                    and s.firstName = ?
                    and s.lastName = ?
                    and s.dob = ?
    
                        
                        ;

                """;

            // this will throw an exception if nothing found.

            String studentNumber = null;

            try {
                studentNumber = template.queryForObject(
                            sql,
                            String.class,
                            args
                );
            }
            catch (Exception ex) {

                //studentNumber = null;

            }

            return studentNumber;

    }

    // NOT USED AT THE MOMENT
    public String schoolSourceIdForStudentNumber(String studentNumber) {
        String id = districtId + "." + studentNumber;
         Object[] args = {
            id

        };


        String sql = """
                 select 
                    s.schoolSourceId
                from 
                    student s
                where 
                    s.id = ?
                """;



        try {
            String schoolSourceId = template.queryForObject(
                    sql, 
                    String.class, 
                    args);

            return schoolSourceId;

        }
        catch (Exception ex) {
            return null;
        }
    }

    // NOT USED AT THE MOMENT
    public String schoolSourceIdForTeacherId(String teacherId) {
        String id = districtId + "." + teacherId;
        Object[] args = {
            id
        };


        String sql = """
                 select 
                    t.schoolSourceId
                from 
                    teacher t
                where 
                    t.id = ?
                """;


        try {

        String schoolSourceId = template.queryForObject(
                sql, 
                String.class, 
                args);

        return schoolSourceId;
        }
        catch (Exception ex) {
            return null;
        }
    }


    public String studentNumberFromSourceId (String studentSourceId) {



        Object[] args = {
            districtId,
            studentSourceId

        };


        String sql = """
                 select 
                    s.studentNumber
                from 
                    student s
                where 
                    s.districtId = ?
                    and s.sourceId = ?;
                """;



        try {
            String studentNumber = template.queryForObject(
                    sql, 
                    String.class, 
                    args);

            return studentNumber;
        }
        catch (Exception ex) {
            return null;
        }

    }
    
    public List<String> studentSourceIdsForDistrict (int districtId) {
          Object[] args = {
            
            districtId            
        };


        String sql = """
               select sourceId from student where districtId =  ?;
                """; 


        return template.queryForList(sql, String.class, args);
    }

    public List<String> studentNumbersForDistrict (int districtId) {
          Object[] args = {
            
            districtId            
        };


        String sql = """
               select studentNumber from student where districtId = ?;
                """; 


        return template.queryForList(sql, String.class, args);
    }

    public List<String> studentIdsForDistrict (int districtId) {
          Object[] args = {
            
            districtId            
        };


        String sql = """
               select id from student where districtId = ?;
                """; 


        return template.queryForList(sql, String.class, args);
    }

    

    
    //#region TESTS


    // This shoudl be replaced by the new prep_import
   



    public String csaCodeForCourseName (int forDistrictId, String courseName) throws Exception {



        Object[] args = {
            forDistrictId,
            courseName

        };


        String sql = """
                select csaCode from map_course_csacode where districtId = ? and courseName = ?;
                """;

        String csaCode = template.queryForObject(
                sql, 
                String.class, 
                args);

        return csaCode;

    }

    public void sisGradeAdd (String studentNumber, String schoolYear, String period, String code, String subject, int score, String csaCode) {
        
        String id = districtId + "." + studentNumber;
        
        Object[] args = {
            districtId,
            id,
            schoolYear,
            period,
            code,
            subject,
            score,
            csaCode
        };

        String sql = "call sis_grade_add (?, ?, ?, ?, ?, ?, ?, ?)";

        int rows = template.update(sql, args);


    }

    public void sisMapAdd(String studentNumber, String schoolYear, String period, String subject, String proficiency, String proficiencyCode, int score, String csaCode ) {

        String id = districtId + "." + studentNumber;


          Object[] args = {
            districtId,
            id,
            schoolYear,
            period,
            subject,
            proficiency, 
            proficiencyCode,
            score,
            csaCode
        };

        
        String sql = "call sis_map_add (?, ?, ?, ?, ?, ?, ?, ?, ?)";


        int rows = template.update(sql, args);

    }


    public void sisMclassAdd(String studentNumber, String schoolYear, String period, String subject, String proficiency, String proficiencyCode, int score, String csaCode ) {

        String id = districtId + "." + studentNumber;


          Object[] args = {
            districtId,
            id,
            schoolYear,
            period,
            subject,
            proficiency, 
            proficiencyCode,
            score,
            csaCode
        };

           String sql = "call sis_mclass_add (?, ?, ?, ?, ?, ?, ?, ?, ?)";




        int rows = template.update(sql, args);

    }



     public void sisStaarAdd(String studentNumber, String schoolYear, String subject, String code, String grade, String proficiency, String proficiencyCode, String csaCode) {

        String id = districtId + "." + studentNumber;


          Object[] args = {
            districtId,
            id,
            schoolYear,
            subject,
            code,
            grade,
            proficiency,
            proficiencyCode,
            csaCode
        };

         String sql = "call sis_staar_add (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        


        int rows = template.update(sql, args);

    }




     public void sisTelpasAdd(String studentNumber, String schoolYear, String grade, String proficiency, int listeningScore, int speakingScore, int readingScore, int writingScore) {

        String id = districtId + "." + studentNumber;


          Object[] args = {
            districtId,
            id,
            schoolYear,
            grade,
            proficiency,
            listeningScore,
            speakingScore,
            readingScore,
            writingScore
        };

         String sql = "call sis_telpas_add (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        


        int rows = template.update(sql, args);

    }

     public void sisDiscipline(String studentNumber, String issDays, String ossDays, String aepDays, String grade, String schoolYear ) {

        String id = districtId + "." + studentNumber;


          Object[] args = {
            districtId,
            id,
            issDays,
            ossDays,
            aepDays,
            grade,
            schoolYear
        };

        
        String sql = "call sis_discipline_add (?, ?, ?, ?, ?, ?, ?)";


   
        int rows = template.update(sql, args);

    }

    //#endregion


    //#region Import

    
    public String checkImportDeltas (int districtId, String importDefinitionId) {



        Object[] args = {
            districtId,
            importDefinitionId

        };


        String sql = """
                 call check_import_deltas (?,?);
                """;



        try {
            String studentNumber = template.queryForObject(
                    sql, 
                    String.class, 
                    args);

            return studentNumber;
        }
        catch (Exception ex) {
            return null;
        }

    }



    public int prepImport (int districtId, String importDefinitionId, Boolean isRoster, Boolean isSisData, String log) {

        List<SqlParameter> params = Arrays.asList(
            new SqlParameter("p_districtId", Types.INTEGER),
            new SqlParameter("p_importDefinitionId", Types.VARCHAR),
            new SqlParameter("p_isRoster", Types.BOOLEAN),
            new SqlParameter("p_isSisData", Types.BOOLEAN),
            new SqlParameter("p_log", Types.VARCHAR),
            new SqlOutParameter("p_importId", Types.INTEGER) 
        );

        Map<String, Object> res = template.call(
            new CallableStatementCreator() {
                @Override
                public CallableStatement createCallableStatement(Connection con) throws SQLException {
                    CallableStatement cs = con.prepareCall("{call prep_import(?, ?, ?, ?, ?, ?)}");
                    cs.setInt(1, districtId);
                    cs.setString(2, importDefinitionId);
                    // cs.setInt(3, iIsRoster);
                    // cs.setInt(4, iIsSis);

                    cs.setBoolean(3, isRoster);
                    cs.setBoolean(4, isSisData);

                    cs.setString(5, log);
                    cs.registerOutParameter(6, Types.INTEGER); // Register OUT parameter
                    return cs;
                }
            },
            params
        );

        // set the importRepo importId 
        this.importId =  (int) res.get("p_importId");        

        // aslso set the inportRepo districtId
        this.districtId = districtId;

        return this.importId;

    }

    public void prepSendBosco (int districtId, String importDefinitionId, Boolean isRoster, Boolean isSisData) {
        Object[] args = {
            districtId,
            importDefinitionId,
            isRoster,
            isSisData
        };

        String sql = "call prep_send_bosco (?, ?, ?, ?)";

        int rows = template.update(sql, args);
    }

    public void postSendBosco (int districtId, String importDefinitionId, Boolean isRoster, Boolean isSisData) {
           Object[] args = {
            districtId,
            importDefinitionId,
            isRoster,
            isSisData
        };

        String sql = "call post_sent_bosco (?, ?, ?, ?)";

        int rows = template.update(sql, args);

    }





  


    


     public Boolean saveStudentSped (int districtId, String studentScourceId, 
        String stateInstructionalSettingCode, 
        String stateChildCountFundCode,
        int specialEducationEnrollmentTXID,
        String startDate,
        String endDate,
        Boolean multiplyDisabled,
        String entryComment,
        String exitComment
     
     ) {
         String sql = "call student_sped_add (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


         Object[] args = {
            districtId,
            studentScourceId,
            stateInstructionalSettingCode,
            stateChildCountFundCode,
            specialEducationEnrollmentTXID,
            startDate,
            endDate,
            multiplyDisabled,
            entryComment,
            exitComment

        };


         int isChanged = template.queryForObject(
                sql,
                args,
                Integer.class

                                );

        return (isChanged > 0);
    }




    
    public void boscoStudentAdd (int forDistrictId, String id, String studentNumber) {
           Object[] args = {
            forDistrictId,
            id,
            studentNumber
        };

        String sql = "insert ignore into bosco_student (districtId, id, studentNumber) values (?, ?, ?);";
        int rows = template.update(sql, args);


    }

    public void boscoStudentRemove (int forDistrictId, String id, String studentNumber ) {
            Object[] args = {
            forDistrictId,
            id,
            studentNumber
        };

        String sql = "delete from  bosco_student where districtId = ? and id = ? and studentNumber = ?;";
        int rows = template.update(sql, args);
    }

     public void boscoUserAdd (int forDistrictId, String id, String role, String email) {
           Object[] args = {
            forDistrictId,
            id,
            role,
            email
        };

        String sql = "insert ignore into bosco_user (districtId, id, role, email) values (?, ?, ?, ?);";
        int rows = template.update(sql, args);


    }

    public void boscoUserRemove (int forDistrictId, String id, String role, String email ) {
            Object[] args = {
            forDistrictId,
            id,
            role,
            email
        };

        String sql = "delete from  bosco_user where districtId = ? and id = ? and role = ? and email = ?;";
        int rows = template.update(sql, args);
    }


    
    public void logError (String error) {
 
        
        Object[] args = {
            importId,
            error
        };

        String sql = "insert into log_error (importId, error) values (?, ?)";
       


        int rows = template.update(sql, args);

    }


    public void logInfo (String info) {
        
        
        
        Object[] args = {
            importId,
            info
        };

        String sql = "insert into log_info (importId, info) values (?, ?)";

        int rows = template.update(sql, args);

    }

    // TEA testing

    public void logTea (String fileName, String log) {
          Object[] args = {
            fileName,
            log
        };
        String sql = "insert into temp_log_tea (fileName, log) values (?, ?)";
        int rows = template.update(sql, args);
    }

    public void logTeaStar2024 (String testName, Star2024 s) {

        Object[] args = {
            testName,
            s.getStudentId(),
            s.getAdminDate(),
            s.getPliReadingLanguageArts(),
            s.getScoreReadingLanguageArts(),
            s.getPliMath(),
            s.getScoreMath(),
            s.getPliSocialStudies(),
            s.getScoreSocialStudies(),
            s.getPliScience(),
            s.getScoreScience(),
            s.getScoreAlgebra()
            
        };

        String sql = """
                                
                INSERT INTO temp_star_2024
                (`testName`,
                `studentId`,
                `adminDate`,
                `pliReadingLanguageArts`,
                `scoreReadingLanguageArts`,
                `pliMath`,
                `scoreMath`,
                `pliSocialStudies`,
                `scoreSocialStudies`,
                `pliScience`,
                `scoreScience`,
                `scoreAlgebra`)
                VALUES 
                (
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?);


                """;

                 int rows = template.update(sql, args);

    }

    public List<ImportDefinition> getActiveImportDefinitions() {
        
         String sql = "select * from import_definition where active = 1";

         List<ImportDefinition> impDefs = template.query(
                sql,
                new BeanPropertyRowMapper<ImportDefinition>(ImportDefinition.class));

        return impDefs;
    }

     public List<ImportDefinition> getAllImportDefinitions() {
        
         String sql = "select * from import_definition";

         List<ImportDefinition> impDefs = template.query(
                sql,
                new BeanPropertyRowMapper<ImportDefinition>(ImportDefinition.class));

        return impDefs;
    }

    public ImportDefinition getImportDefinition (String importDefId) {
         String sql = "select * from import_definition where Id = ?";


         ImportDefinition impDef = template.queryForObject(
                sql,
                new BeanPropertyRowMapper<ImportDefinition>(ImportDefinition.class),
                importDefId
                );

        return impDef;
    }

   

    public List<ImportSetting> getImportSettings(String importDefId) {
         String sql = "select * from import_setting where importDefId = '" + importDefId + "';";

         List<ImportSetting> impSettings = template.query(
                sql,
                new BeanPropertyRowMapper<>(ImportSetting.class)                
                );

        return impSettings;
    }

    //#endregion


    
    //#region Bosco Export



    


    // public void TEMP_SetImport (int importId, int districtId) {
    //     this.importId = importId;
    //     this.districtId = districtId;
    // }

    public List<School> schoolsBoscoForExport (int forImportId, int changedFlag) {
         Object[] args = {
            forImportId,
            changedFlag
        };

        String sql = """
              select 
                    i.districtId,
                    s.name,
                    s.schoolCode
                from 
                    school s
                    join import i on i.id = s.importId
                where 
                    s.importId = ? 
                    and s.changed = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(School.class), args);
    }
         
    // public List<Teacher> teachersBoscoForExport (int forImportId, int changedFlag) {

    //     Object[] args = {
    //         forImportId,
    //         changedFlag
    //     };

    //     String sql = """
    //             select 
    //                 concat (concat (i.districtId, '.') , t.teacherId) as id,
    //                 t.firstName,
    //                 t.lastName,
    //                 t.email,
    //                 'TEACHER' as role,
    //                 i.districtId as organizationId
    //             from 
    //                 teacher t
    //                 join import i on i.id = t.importId
    //             where 
    //                 t.importId = ? 
    //                 and t.changed = ?;
    //             """;

    //     return template.query(sql, new BeanPropertyRowMapper<>(Teacher.class), args);
        
    // }

    // THIS ONE.

    public List<Teacher> teacherBoscoGetForExport(int districtId) {
          Object[] args = {
            districtId
        };

        String sql = """
             select
                concat (concat (i.districtId, '.') , t.teacherId) as id,
                t.firstName,
                t.lastName,
                LOWER(t.email) as email,
                'TEACHER' as role,
                i.districtId as organizationId,
                t.teacherId as userId
            from
                teacher t
                join import i on i.id = t.importId
            where
                t.importId = ?
                and t.changed = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<Teacher>(Teacher.class), args);
    }


    public List<Teacher> teacherBoscoGetForExport(int districtId, String importStatus) {
        Object[] args = {
            districtId,
            importStatus
        };

        String sql = """
             select
                t.id,                
                t.firstName,
                t.lastName,
                LOWER(t.email) as email,
                'TEACHER' as role,
                t.districtId as organizationId,
                t.teacherId as userId
            from
                teacher t                
            where
                t.districtId = ?
                and t.importStatus = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<Teacher>(Teacher.class), args);
    }
    

 
    
    public List<Student> studentsBoscoForExport(int districtId, String importStatus) {

        // importStatus
        // CHANGE 
        // NEW
        // DELETE
        
       

        Object[] args = {
            districtId,
            importStatus
        };


        // bilingual : bool 
        // esl
        // specialEd
        // section504
        // iepDate  : string

        String sql = """
                select 	                    
                    id,
                    s.firstName,
                    s.lastName,
                    s.dob,
                    s.gender,
                    s.studentNumber as studentId,
                    school.name as school,                    
                    ms.ncesSchoolId as schoolId,
                    s.districtId,
                    s.grade,
                    s.americanIndianOrAlaskaNative,
                    s.asian,
                    s.blackOrAfricanAmerican,
                    s.nativeHawaiianOrOtherPacificIslander,
                    s.white,
                    s.hispanicOrLatinoEthnicity as hispanicOrLatino
                from 
                    student s                     
                    join school school on school.districtId = s.districtId and school.sourceId = s.schoolSourceId
                    join map_school_code_nces_school_id ms on ms.districtId = school.districtId and ms.schoolCode = school.schoolCode
                where 
                    s.districtId = ? 
                    and s.importStatus = ?;
                """; //.formatted(districtId, districtId, importId, changedFlag);


        return template.query(sql, new BeanPropertyRowMapper<Student>(Student.class), args);

        // System.out.println(sql);
            
        //     List<BoscoStudent> students = template.query(
        //         sql,
        //         new BeanPropertyRowMapper(BoscoStudent.class));

        // return students;
    }


    




public Student studentBoscoForExport (String id) {

        // 1 is changed
        // 2 is new.



        Object[] args = {
            id
        };

        String sql = """
               select 	                    
                    id,
                    s.firstName,
                    s.lastName,
                    s.dob,
                    s.gender,
                    s.studentNumber as studentId,
                    school.name as school,                    
                    ms.ncesSchoolId as schoolId,
                    s.districtId,
                    s.grade,
                    s.americanIndianOrAlaskaNative,
                    s.asian,
                    s.blackOrAfricanAmerican,
                    s.nativeHawaiianOrOtherPacificIslander,
                    s.white,
                    s.hispanicOrLatinoEthnicity as hispanicOrLatino
                    
                from 
                    student s                     
                    join school school on school.districtId = s.districtId and school.sourceId = s.schoolSourceId
                    join map_school_code_nces_school_id ms on ms.districtId = school.districtId and ms.schoolCode = school.schoolCode
                where                     
                    s.id = ? 
                """; //.formatted(districtId, districtId, importId, changedFlag);



            Student bst = template.queryForObject(
                sql,
                new BeanPropertyRowMapper<>(Student.class),
                args
                );

        return bst;

        //return template.query(sql, new BeanPropertyRowMapper<BoscoStudent>(BoscoStudent.class), args);

        // System.out.println(sql);
            
        //     List<BoscoStudent> students = template.query(
        //         sql,
        //         new BeanPropertyRowMapper(BoscoStudent.class));

        // return students;
    }

    // single student
   
  


    
    public List<Guardian> guardiansBoscoForStudent(String id) {

        Object[] args = {
            
            id
        };


        // this would be best if we can get the guarians based on teh student id.

        String sql = """

                select
                    g.firstName,
                    g.lastName,
                    g.type,
                    LOWER(g.email) as email
                from
                
                    guardian g
                where
                    g.studentId = ?
                """;


        //return template.queryForList(sql, BoscoGuardian.class, args);

        return template.query(sql, new BeanPropertyRowMapper<Guardian>(Guardian.class), args);

        // List<BoscoGuardian> guardians = template.query(
        //     sql,
        //     new BeanPropertyRowMapper(BoscoGuardian.class));
        // return guardians;

    }

    
    public List<String> schoolsForTeacher (String teacherId) {
        
        Object[] args = {
            
            teacherId
        };


        String NOsql = """
        
        select
        	distinct  msc.ncesSchoolId
        from
            teacher t
            join student_teacher st on st.teacherId = t.id
            join student s on s.id = st.studentId
            join map_school_code_nces_school_id msc on msc.districtId = t.districtId and msc.schoolCode = s.schoolSourceId
        where
            t.id = ? ;
            """; 


        String sql = """
                select 
                    msc.ncesSchoolId
                from 
                    teacher t
                    join school sch on sch.districtId = t.districtId and sch.sourceId = t.schoolSourceId
                    join map_school_code_nces_school_id msc on msc.districtId = t.districtId and msc.schoolCode = sch.schoolCode

                where 
                    t.id = ?;
                """;

        return template.queryForList(sql, String.class, args);

    }
    

    public List<String> teacherIdsBoscoForStudent(String id) {

      

        Object[] args = {
            
            id
        };


        String sql = """
            select
                teacherId
            from
                student_teacher
            where
                studentId = ?
            """; 


        return template.queryForList(sql, String.class, args);



        // to use params ?
        // jdbcTemplate.queryForList(sql, String.class, filterValue);
        // List<String> teacherIds = template.query(
        //     sql,
        //     new BeanPropertyRowMapper(String.class)
        //     );
        // return teacherIds;

    }


   

    
    

   
    

    //#endregion


    //#region  Teacher Student

    // This is either build during the import OR built after the import.


    // this is used if the imports have student + class AND teacher + class
    public void buildStudentTeacher () {
        Object[] args = {
            districtId
        };


        String sql = "call student_teacher_build (?)";

        int rows = template.update(sql, args);

    }        

    


    // this is used if the imports have the direct relationship.
     public void saveStudentTeacher (String studentSourceId, String teacherSourceId) {

        Object[] args = {
            importId,
            studentSourceId,
            teacherSourceId
        };

        

        String sql = """
                
                insert ignore into 
                    student_teacher (importId, studentSourceId, teacherSourceId)
                values (?, ?, ?);

                """;

        int rows = template.update(sql, args);
    }


    //#endregion


    //#region School

    public void saveSchool(String sourceId, String name, String schoolCode) {
        //System.out.println("Added");

        Object[] args = {
            districtId,
            sourceId,
            name,
            schoolCode,
            name,
            schoolCode
        };


        
        String sql = """
            insert into
                school (districtId, sourceId, name, schoolCode)
            values (?, ?, ?, ?)
            on duplicate key update
                name = ?,
                schoolCode = ?;
                """;

        int rows = template.update(sql, args);

        //System.out.println(rows + " rows affected");
        
    }

    //#endregion

    //#region Students

    public void deleteAllStudents() {
              String sql = "delete from student";

              template.execute(sql);
    }
    
    

    public void saveStudent(String sourceId, String studentNumber, String firstName, String lastName, String grade, String schoolSourceId) {
    // String sourceId, String studentId, String firstName, String lastName, String grade, String schoolCode    

        String id = districtId + "." + studentNumber;

        Object[] args = {
            id,
            districtId,
            sourceId,
            studentNumber,
            firstName,
            lastName,
            grade,
            schoolSourceId
        };



        String sql = "call student_add (?, ?, ?, ?, ?, ?, ?, ?)";

        int rows = template.update(sql, args);

        
    }



      


    // public void saveStudentProperty (String studentNumber, String dbFieldName, String value) {
    //     String id = districtId + "." + studentNumber;
    //     String sql = "update student set " + dbFieldName  + " = " + value + " where id = '"  + id + "';";
    //     template.update(sql);
    // }

    // public void saveStudentPropertyString (String studentNumber, String dbFieldName, String value) {
    //     String id = districtId + "." + studentNumber;

    //     String sql = "update student set " + dbFieldName  + " = '" + value + "' where id = '"  + id + "';";
    //     template.update(sql);
    // }



    public void saveStudentDemographics ( 
            String studentNumber, 
            String dob, 
            String gender, 
            Boolean americanIndianOrAlaskaNative, 
            Boolean asian,
            Boolean blackOrAfricanAmerican, 
            Boolean nativeHawaiianOrOtherPacificIslander, 
            Boolean white,
            Boolean hispanicOrLatinoEthnicity
            ) {

        String id = districtId + "." + studentNumber;



         Object[] args = {

            id,
            dob,
            gender,
            americanIndianOrAlaskaNative,
            asian,
            blackOrAfricanAmerican,
            nativeHawaiianOrOtherPacificIslander,
            white,
            hispanicOrLatinoEthnicity
            

        };

        // This WILL NOT update the importStatus.   

        //String sql = "call student_demographics_update (?,?,?,?,?,?,?,?,?,?)";

        String sql = """
                call student_demographics_save (?,?,?,?,?,?,?,?,?);
                """;

        int rows = template.update(sql, args);
    }



    


       

     


    //#endregion

    //#region Classes

    


    public void saveStudentClass (String studentSourceId, String classSourceId) {

        Object[] args = {
            districtId,
            studentSourceId,
            classSourceId
        };


        String sql = """                        
          	insert ignore into 
		        student_class (districtId, studentSourceId, classSourceId)
                values (?, ?, ?);
                """;

        int rows = template.update(sql, args);
    }

     public void saveTeacherClass (String teacherSourceId, String classSourceId) {

        Object[] args = {
            districtId,
            teacherSourceId,
            classSourceId
        };

        String sql = """
          
            insert ignore into 
                teacher_class (districtId, teacherSourceId, classSourceId)
            values (?, ?, ?);

                """;

        int rows = template.update(sql, args);
    }

    //#endregion

    //#region Guardians

     public void saveGuardian(String sourceId, String guardianId, String studentSourceId, String firstName, String lastName, String email, String type) {
        // System.out.println("Added");

        /*
         
          p_districtId int,
            p_sourceId varchar(50),     This is the Guardian SourceId 
            p_studentSourceId varchar(50),  This is the Student Source Id
            p_guardianId varchar(50),       The guaridan id ??  Do we need this?
            p_firstName varchar(50),        
            p_lastName varchar(50),
            p_email varchar(255),
            p_type varchar(5)
         */

 
        Object[] args = {
            districtId,
            sourceId,
            studentSourceId,
            guardianId,
            firstName,
            lastName,
            email,
            type
            };
        

        String sql = """
                call guardian_add (?, ?, ?, ?, ?, ?, ?, ?);

           
                """;

        int rows = template.update(sql, args);


        //System.out.println(rows + " rows affected");
        
    }


    


    
    
    //#endregion

    //#region Teachers


    public void saveTeacher(String sourceId, String teacherId, String firstName, String lastName, String email, String schoolSourceId) {
        // System.out.println("Added");

        String id = districtId + "." + teacherId;

       Object[] args = {

            id, 
            districtId,
            sourceId,
            teacherId,

            firstName,
            lastName,
            email,
            schoolSourceId

        };
        

        String sql = """

            call teacher_add (?, ?, ?, ?, ?, ?, ?, ?);

           
                """;

        int rows = template.update(sql, args);

        
    }



    
    

  

    //#endregion

}
