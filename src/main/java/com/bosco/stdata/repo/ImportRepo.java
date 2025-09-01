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


@Repository
public class ImportRepo {

    private JdbcTemplate template;

    private int districtId;   // This is the district we are working on.  Will be used for repo saves
    private int importId;     //  This is the particular import Id  (ie the run for the district)
    

    ImportRepo() {}

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

    public int getSystemStatus (String key) {
        String sql = "select status from system_status where systemKey = ?;";

        int status = template.queryForObject(
                sql, 
                Integer.class, 
                key);

        return status;
    }

    public void setSystemStatus (String key, int status) {

        Object[] args = {
            status,
            key
        };

        String sql = "update system_status set status=? where systemKey = ?;";

        int rows = template.update(sql, args);
    }

    
    public void setImportDefActive (String id, Boolean active) {

        Object[] args = {
            active,
            id

        };

        String sql = "update import_definition set active=? where id = ?;";

        int rows = template.update(sql, args);
    }


    public void importSystemStartup () {
        // Object[] args = {
        //     importId,
        //     baseImportId
        // };


        String sql = "call import_system_startup ()";

        int rows = template.update(sql);


    }

    //#endregion


   
    public List<SisAcademicGrade> sisAcademicGradesGet (int forDistrictId, String id) {
          Object[] args = {
            //forDistrictId,
            id
        };

        String sql = """
           select * from sis_academic_grade where id = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(SisAcademicGrade.class), args);
    }

    public List<SisMap> sisMapsGet (int forDistrictId, String id) {
          Object[] args = {
            //forDistrictId,
            id
        };

        String sql = """
           select * from sis_map where id = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(SisMap.class), args);
    }


     public List<SisMclass> sisMclassGet (int forDistrictId, String id) {
          Object[] args = {
            //forDistrictId,
            id
        };

        String sql = """
           select * from sis_mclass where id = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(SisMclass.class), args);
    }

     public List<SisStaar> sisStaarsGet (int forDistrictId, String id) {
          Object[] args = {
            //forDistrictId,
            id
        };

        String sql = """
           select * from sis_staar where id = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(SisStaar.class), args);
    }

    public List<SisDiscipline> sisDisciplinesGet (int forDistrictId, String id) {
          Object[] args = {
            //forDistrictId,
            id
        };

        String sql = """
           select * from sis_discipline where id = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(SisDiscipline.class), args);
    }



    
    public List<String> studentSourceIdsForImport (int forImportId) {
          Object[] args = {
            
            forImportId            
        };


        String sql = """
               select sourceId from student where importId = ?;
                """; 


        return template.queryForList(sql, String.class, args);
    }

    public List<String> studentNumbersForImport (int forImportId) {
          Object[] args = {
            
            forImportId            
        };


        String sql = """
               select studentNumber from student where importId = ?;
                """; 


        return template.queryForList(sql, String.class, args);
    }

     public List<Student> boscoStudentsGet(int forImportId) {

        // 1 is changed
        // 2 is new.


        Object[] args = {
            forImportId
        };

        String sql = """
                select 	                    
                    concat (concat (i.districtId, '.') , s.studentNumber) as id,
                    s.firstName,
                    s.lastName,
                    s.dob,
                    s.gender,
                    s.studentNumber as studentId,
                    school.name as school,
                    school.schoolCode as schoolId,
                    i.districtId,
                    s.grade
                from 
                    student s 
                    left join school school on school.importId = s.importId and school.sourceId = s.schoolSourceId
                    join import i on i.id = s.importId
                where 
                    s.importId = ? 
                    
                """; //.formatted(districtId, districtId, importId, changedFlag);


        return template.query(sql, new BeanPropertyRowMapper<Student>(Student.class), args);

        // System.out.println(sql);
            
        //     List<BoscoStudent> students = template.query(
        //         sql,
        //         new BeanPropertyRowMapper(BoscoStudent.class));

        // return students;
    }


    
    //#region TESTS


    public void sisPrepData () {
        
        
        Object[] args = {
            districtId
        };

        String sql = "call prep_sis_data (?)";

        int rows = template.update(sql, args);


    }


     public void sisPostData () {
        
        
        Object[] args = {
            districtId
        };

        String sql = "call post_sis_data (?)";

        int rows = template.update(sql, args);


    }


    public void sisAcademicGradeAdd (String studentNumber, String schoolYear, String term, String courseNumber, String courseName, int grade) {
        
        String id = districtId + "." + studentNumber;
        
        Object[] args = {
            districtId,
            id,
            schoolYear,
            term,
            courseNumber,
            courseName,
            grade
        };

        String sql = "call sis_academic_grade_add (?, ?, ?, ?, ?, ?, ?)";

        int rows = template.update(sql, args);


    }

    public void sisMapAdd(String studentNumber, String schoolYear, String term, String subject, String level, int score ) {

        String id = districtId + "." + studentNumber;


          Object[] args = {
            districtId,
            id,
            schoolYear,
            term,
            subject,
            level, 
            score
        };

        
        String sql = "call sis_map_add (?, ?, ?, ?, ?, ?, ?)";


        int rows = template.update(sql, args);

    }


    public void sisMclassAdd(String studentNumber, String schoolYear, String term, String subject, String level, int score ) {

        String id = districtId + "." + studentNumber;


          Object[] args = {
            districtId,
            id,
            schoolYear,
            term,
            subject,
            level, 
            score
        };

           String sql = "call sis_mclass_add (?, ?, ?, ?, ?, ?, ?)";




        int rows = template.update(sql, args);

    }



     public void sisStaarAdd(String studentNumber, String testDate, String stateAssessmentSubject, String gradeDuringAssessment, String stateAssessmentScore ) {

        String id = districtId + "." + studentNumber;


          Object[] args = {
            districtId,
            id,
            testDate,
            stateAssessmentSubject,
            gradeDuringAssessment,
            stateAssessmentScore
        };

         String sql = "call sis_staar_add (?, ?, ?, ?, ?, ?)";
        


        int rows = template.update(sql, args);

    }

     public void sisDiscipline(String studentNumber, String issDays, String ossDays, String aepDays, String schoolYear ) {

        String id = districtId + "." + studentNumber;


          Object[] args = {
            districtId,
            id,
            issDays,
            ossDays,
            aepDays,
            schoolYear
        };

        
        String sql = "call sis_discipline_add (?, ?, ?, ?, ?, ?)";


   
        int rows = template.update(sql, args);

    }

    //#endregion


    //#region Import

    

    
    public int prepImport(int districtId, String log) {
        List<SqlParameter> params = Arrays.asList(
            new SqlParameter("p_districtId", Types.INTEGER),
            new SqlParameter("p_log", Types.VARCHAR),
            new SqlOutParameter("p_importId", Types.INTEGER) // Example for an OUT parameter
        );

        Map<String, Object> res = template.call(
            new CallableStatementCreator() {
                @Override
                public CallableStatement createCallableStatement(Connection con) throws SQLException {
                    CallableStatement cs = con.prepareCall("{call prep_import(?, ?, ?)}");
                    cs.setInt(1, districtId);
                    cs.setString(2, log);
                    cs.registerOutParameter(3, Types.INTEGER); // Register OUT parameter
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

    public void setAllNewImports() {

         Object[] args = {
            importId

        };

        String sql = "call set_all_new_imports(?)";

        int rows = template.update(sql, args);
    }



     public ImportChanges importChangesFromBase (int forImportId, int baseImportId) {
         String sql = "call check_diff_counts (?, ?)";


         Object[] args = {
            forImportId,
            baseImportId

        };


         ImportChanges impChanges = template.queryForObject(
                sql,
                args,

                new BeanPropertyRowMapper<>(ImportChanges.class)
                                );

        return impChanges;
    }


    public void diffImports (int baseImportId) {
        Object[] args = {
            importId,
            baseImportId
        };


        String sql = "call diff_imports (?, ?)";

        int rows = template.update(sql, args);

    }        




    public void setImportBase (String importDefId) {
        Object[] args = {
            importId,
            importDefId
        };

        String sql = "update import_definition set baseImportId = ? where id = ?; ";

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

    public int getBaseImportForDistrict(int districtId) {
        String sql = "select baseImportId from import_definition where districtId = ?;";


        int baseImportId = template.queryForObject(
                sql, 
                Integer.class, 
                districtId);

        return baseImportId;

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
         
    public List<Teacher> teachersBoscoForExport (int forImportId, int changedFlag) {

        Object[] args = {
            forImportId,
            changedFlag
        };

        String sql = """
                select 
                    concat (concat (i.districtId, '.') , t.teacherId) as id,
                    t.firstName,
                    t.lastName,
                    t.email
                from 
                    teacher t
                    join import i on i.id = t.importId
                where 
                    t.importId = ? 
                    and t.changed = ?;
                """;

        return template.query(sql, new BeanPropertyRowMapper<>(Teacher.class), args);
        
    }
    
    public List<Student> studentsBoscoForExport(int forImportId, int changedFlag) {

        // 1 is changed
        // 2 is new.



        Object[] args = {
            forImportId,
            changedFlag
        };

        String sql = """
                select 	                    
                    concat (concat (i.districtId, '.') , s.studentNumber) as id,
                    s.firstName,
                    s.lastName,
                    s.dob,
                    s.gender,
                    s.studentNumber as studentId,
                    school.name as school,
                    school.schoolCode as schoolId,
                    i.districtId,
                    s.grade
                from 
                    student s 
                    left join school school on school.importId = s.importId and school.sourceId = s.schoolSourceId
                    join import i on i.id = s.importId
                where 
                    s.importId = ? 
                    and s.changed = ?;
                """; //.formatted(districtId, districtId, importId, changedFlag);


        return template.query(sql, new BeanPropertyRowMapper<Student>(Student.class), args);

        // System.out.println(sql);
            
        //     List<BoscoStudent> students = template.query(
        //         sql,
        //         new BeanPropertyRowMapper(BoscoStudent.class));

        // return students;
    }



    // single student
    public Student studentBoscoForExport (int forImportId, String studentNumber) {

        // 1 is changed
        // 2 is new.



        Object[] args = {
            forImportId,
            studentNumber
        };

        String sql = """
                select 	                    
                    concat (concat (i.districtId, '.') , s.studentNumber) as id,
                    s.firstName,
                    s.lastName,
                    s.dob,
                    s.gender,
                    s.studentNumber as studentId,
                    school.name as school,
                    school.schoolCode as schoolId,
                    i.districtId,
                    s.grade,
                    s.americanIndianOrAlaskaNative,
                    s.asian,
                    s.blackOrAfricanAmerican,
                    s.nativeHawaiianOrOtherPacificIslander,
                    s.white,
                    s.hispanicOrLatinoEthnicity
                from 
                    student s 
                    left join school school on school.importId = s.importId and school.sourceId = s.schoolSourceId
                    join import i on i.id = s.importId
                where 
                    s.importId = ? 
                    and s.studentNumber = ?;
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

    public List<Teacher> teacherBoscoGetForExport(int forImportId, int changedFlag) {
          Object[] args = {
            forImportId,
            changedFlag
        };

        String sql = """
             select
                concat (concat (i.districtId, '.') , t.teacherId) as id,
                t.firstName,
                t.lastName,
                t.email
            from
                teacher t
                join import i on i.id = t.importId
            where
                t.importId = ?
                and t.changed = ?;
                """; 


        return template.query(sql, new BeanPropertyRowMapper<Teacher>(Teacher.class), args);
    }



    
    public List<Guardian> guardiansBoscoForStudent(int forImportId, String studentNumber) {

        Object[] args = {
            
            forImportId,
            studentNumber
        };

        String sql = """
                select
                    g.firstName,
                    g.lastName,
                    g.type,
                    g.email
                from
                    student s
                    join guardian g on g.importId = s.importId and g.studentSourceId = s.sourceId
                where
                    s.importId = ?
                    and s.studentNumber = ?;
                """;


        //return template.queryForList(sql, BoscoGuardian.class, args);

        return template.query(sql, new BeanPropertyRowMapper<Guardian>(Guardian.class), args);

        // List<BoscoGuardian> guardians = template.query(
        //     sql,
        //     new BeanPropertyRowMapper(BoscoGuardian.class));
        // return guardians;

    }

    public List<String> teacherIdsBoscoForStudent(int forImportId, String studentNumber) {

      

        Object[] args = {
            
            forImportId,
            studentNumber
        };


        String sql = """
                select
	                concat (concat (i.districtId, '.') , t.teacherId) as id
                from
                    student s
                    join student_teacher st on st.importId = s.importId and st.studentSourceId = s.sourceId
                    join teacher t on t.importId = st.importId and t.sourceId = st.teacherSourceId
                    join import i on i.id = s.importId
                where
                    s.importId = ?
                    and s.studentNumber = ?;
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


    public List<String> studentIdsDeletedFromImport(int forImportId, int baseImportId) {

      

        Object[] args = {
            baseImportId,
            forImportId

        };


        String sql = """

           select
                concat (concat (i.districtId, '.') , bs.studentNumber) as id
            from
                student bs
                join import i on i.id = bs.importId
            where
                bs.importId = ?
                and bs.sourceId not in (
                    select
                        s.sourceId
                    from
                        student s
                    where
                        s.importId = ?
                
                );
            """; 


        return template.queryForList(sql, String.class, args);



        

    }

    
    public List<String> teacherIdsDeletedFromImport(int forImportId, int baseImportId) {

      

        Object[] args = {
            baseImportId,
            forImportId

        };


        String sql = """

            select
                concat (concat (i.districtId, '.') , bt.teacherId) as id
            from
                teacher bt
                join import i on i.id = bt.importId
            where
                bt.importId = ?
                and bt.sourceId not in (
                    select
                        t.sourceId
                    from
                        teacher t
                    where
                        t.importId = ?
                
                );
            """; 


        return template.queryForList(sql, String.class, args);



        

    }




    public List<String> schoolIdsDeletedFromImport(int forImportId, int baseImportId) {

       // this needs to be sorted out!

        Object[] args = {
            baseImportId,
            forImportId

        };


        String sql = """

          
            """; 


        return template.queryForList(sql, String.class, args);



        

    }


   
    

    //#endregion


    //#region  Teacher Student

    // This is either build during the import OR built after the import.


    // this is used if the imports have student + class AND teacher + class
    public void buildStudentTeacher () {
        Object[] args = {
            importId
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
            importId,
            sourceId,
            name,
            schoolCode,
            name,
            schoolCode
        };


        
        String sql = """
            insert into
                school (importId, sourceId, name, schoolCode)
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
    
    


      public void saveStudent(String sourceId, String studentId, String firstName, String lastName, String grade, String schoolSourceId) {
    // String sourceId, String studentId, String firstName, String lastName, String grade, String schoolCode    

        Object[] args = {
            importId,
            sourceId,
            studentId,
            firstName,
            lastName,
            grade,
            schoolSourceId,

            studentId,
            firstName,
            lastName,
            grade,
            schoolSourceId
        };


        String sql = """
                
                insert into
                    student (importId, sourceId, studentNumber, firstName, lastName, grade, schoolSourceId)
                values (?, ?, ?, ?, ?, ?, ?)
                on duplicate key update
                    studentNumber = ?,
                    firstName = ?,
                    lastName = ?,
                    grade = ?,
                    schoolSourceId = ?;

                """;

        int rows = template.update(sql, args);

        //System.out.println(rows + " rows affected");
        
    }


    public void saveStudentProperty (String SourceId, String DbFieldName, String Value) {
        String sql = "update student set " + DbFieldName  + " = " + Value + "where importId = " + importId + " and sourceId='"  + SourceId + "'";
        template.update(sql);
    }

    public void saveStudentDemographics ( String sourceId, 
            String dob, 
            String gender, 
            Boolean americanIndianOrAlaskaNative, 
            Boolean asian,
            Boolean blackOrAfricanAmerican, 
            Boolean nativeHawaiianOrOtherPacificIslander, 
            Boolean white,
            Boolean hispanicOrLatinoEthnicity) {

       


         Object[] args = {

            dob,
            gender,
            americanIndianOrAlaskaNative,
            asian,
            blackOrAfricanAmerican,
            nativeHawaiianOrOtherPacificIslander,
            white,
            hispanicOrLatinoEthnicity,
            importId,
            sourceId

        };


        //String sql = "call student_demographics_update (?,?,?,?,?,?,?,?,?,?)";

        String sql = """
                update
                    student
                set
                    dob = ?,
                    gender =  ?,
                    americanIndianOrAlaskaNative = ?,
                    asian = ?,
                    blackOrAfricanAmerican = ?,
                    nativeHawaiianOrOtherPacificIslander = ?,
                    white = ?,
                    hispanicOrLatinoEthnicity = ?
                where
                    importId = ?
                    and sourceId = ?;
                """;

        int rows = template.update(sql, args);
    }


     


    //#endregion

    //#region Classes

    


    public void saveStudentClass (String studentSourceId, String classSourceId) {

        Object[] args = {
            importId,
            studentSourceId,
            classSourceId
        };


        String sql = """                        
          	insert ignore into 
		        student_class (importId, studentSourceId, classSourceId)
                values (?, ?, ?);
                """;

        int rows = template.update(sql, args);
    }

     public void saveTeacherClass (String teacherSourceId, String classSourceId) {

        Object[] args = {
            importId,
            teacherSourceId,
            classSourceId
        };

        String sql = """
          
            insert ignore into 
                teacher_class (importId, teacherSourceId, classSourceId)
            values (?, ?, ?);

                """;

        int rows = template.update(sql, args);
    }

    //#endregion

    //#region Guardians

    public void saveGuardian(String sourceId, String guardianId, String studentId, String firstName, String lastName, String email, String type) {
        // System.out.println("Added");

 
        Object[] args = {
            importId,
            sourceId,
            studentId,
            guardianId,
            firstName,
            lastName,
            email,
            type,

            guardianId,
            firstName,
            lastName,
            studentId,
            email,
            type
            };
        

        String sql = """
                insert into
                    guardian (importId, sourceId, studentSourceId, guardianId, firstName, lastName, email, type)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                on duplicate key update
                    guardianId = ?,
                    firstName = ?,
                    lastName = ?,
                    studentSourceId = ?,
                    email = ?,
                    type = ?;
                """;

        int rows = template.update(sql, args);


        //System.out.println(rows + " rows affected");
        
    }


    
    
    //#endregion

    //#region Teachers


    
    public void saveTeacher(String sourceid, String teacherId, String firstname, String lastname, String email) {
        // System.out.println("Added");

       Object[] args = {
            importId,
            sourceid,
            teacherId,
            firstname,
            lastname,
            email,

            teacherId,
            firstname,
            lastname,
            email


        };
        

        String sql = """
            insert into
                teacher (importId, sourceId, teacherId, firstName, lastName, email)
            values (?, ?, ?, ?, ?, ?)
            on duplicate key update
                teacherId = ?,
                firstName = ?,
                lastName = ?,
                email = ?;
                """;

        int rows = template.update(sql, args);

        
    }

  

    //#endregion

}
