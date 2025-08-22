package com.bosco.stdata.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bosco.stdata.model.BoscoStudent;
import com.bosco.stdata.model.TestMap;


@Repository
public class ImportRepo {

    private JdbcTemplate template;

    ImportRepo() {}

    public JdbcTemplate getTemplate() {
        return template;
    }

    @Autowired
    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }


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


    public List<TestMap> studentMapsGetForStudent (int districId, String studentNumber) {
        Object[] args = {
            districId,
            studentNumber
        };

        String sql = """
            select 
                districtId,
                studentNumber,
                schoolYear,
                term,
                subject,
                level,
                testScore
            from student_map where districtId=? and studentNumber = ?;

                    
                """; 


        return template.query(sql, new BeanPropertyRowMapper<>(TestMap.class), args);

    }

     public List<BoscoStudent> boscoStudentsGet(int forImportId) {

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
                    s.schoolCode as schoolId,
                    i.districtId,
                    s.grade
                from 
                    student s 
                    left join school school on school.importId = s.importId and school.sourceId = s.schoolCode
                    join import i on i.id = s.importId
                where 
                    s.importId = ? 
                    
                """; //.formatted(districtId, districtId, importId, changedFlag);


        return template.query(sql, new BeanPropertyRowMapper<BoscoStudent>(BoscoStudent.class), args);

        // System.out.println(sql);
            
        //     List<BoscoStudent> students = template.query(
        //         sql,
        //         new BeanPropertyRowMapper(BoscoStudent.class));

        // return students;
    }

}
