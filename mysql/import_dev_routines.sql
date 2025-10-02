-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: import_dev
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping routines for database 'import_dev'
--
/*!50003 DROP PROCEDURE IF EXISTS `bu_student_sped_add` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `bu_student_sped_add`(
	p_districtId int,
	p_studentSourceId varchar(50),
    p_stateInstructionalSettingCode varchar(10),
    p_stateChildCountFundCode varchar(10),
    p_specialEducationEnrollmentTXID int,
    p_startDate varchar(50),
    p_endDate varchar(50),
    p_multiplyDisabled tinyint,
    p_entryComment varchar(500),
    p_exitComment varchar(500)
    )
BEGIN

	 declare isChanged int;
    
    if exists 
		(
			select 1 from student_sped
			where 
				districtId = p_districtId
                and studentSourceId = p_studentSourceId
                and stateInstructionalSettingCode = p_stateInstructionalSettingCode
                and stateChildCountFundCode = p_stateChildCountFundCode
                and specialEducationEnrollmentTXID = p_specialEducationEnrollmentTXID
                and startDate = p_startDate
                and endDate = p_endDate
                and multiplyDisabled = p_multiplyDisabled
                and entryComment = p_entryComment
                and exitComment = p_exitComment
		)
	then
		select 0 into isChanged;
	else
    
		-- this is where we need to update the import student to changed.
        
        -- SO here we need to delete the record if it exists.
        delete from student_sped where districtId = p_districtId and studentSourceId = p_studentSourceId;
        
        insert into student_sped (districtId, studentSourceId, stateInstructionalSettingCode, stateChildCountFundCode, specialEducationEnrollmentTXID, startDate, endDate, multiplyDisabled, entryComment, exitComment)
		values (p_districtId, p_studentSourceId, p_stateInstructionalSettingCode, p_stateChildCountFundCode, p_specialEducationEnrollmentTXID, p_startDate, p_endDate, p_multiplyDisabled, p_entryComment, p_exitComment);
		
        select 1 into isChanged;
            
	end if;
    
    select isChanged;
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `check_import_deltas` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `check_import_deltas`(
	p_districtId int,
    p_importDefinitionId varchar(50)
    
    )
BEGIN

	declare totalStudents int;
	declare changedStudents int;
    
    declare percentStudentChange decimal (10,2);
    
    
    
    declare v_maxDeltaPercent decimal(5,2);
    
    select
		maxDeltaPercent
	into
		v_maxDeltaPercent
	from
		import_definition
	where
		id = p_importDefinitionId;


	select
		count(*)
	into 
		totalStudents
	from
		student s
	where
		s.districtId = p_districtId;
		
		
		
	select
		count(*)
	into 
		changedStudents
	from
		student s
	where
		s.districtId = p_districtId
		and s.importStatus != 'OK';    -- CHANGED, DELETE, NEW
        
        
        
	-- TOOD: Same for Teachers....
    
    
    select
		(changedStudents * 100.0) / totalStudents
	into
		percentStudentChange;
    
    
    

	if (percentStudentChange > v_maxDeltaPercent) then
		select concat ('Too many changes ',  percentStudentChange , ' : ' ,  v_maxDeltaPercent) as 'res';
	else 
		select 'OK';
	end if;
	
        
	

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `guardian_add` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `guardian_add`(
	p_districtId int,
    p_sourceId varchar(50),  
    p_studentSourceId varchar(50),
    p_guardianId varchar(50),
    p_firstName varchar(50),
    p_lastName varchar(50),
    p_email varchar(255),
    p_type varchar(5)
    )
BEGIN
    
    -- This does not work for deletes.
    -- get the studentId from the studentSourceId
    
    select id  into @studentId from student where districtId = p_districtId and sourceId = p_studentSourceId;
    
    
    
    -- so for this one.
    -- if exists and is the same - DO NOTING
    -- if new - update Student (id) to 'CHANGED' if 'OK' -- KEEP NEW if NEW
    -- if changed - same as above!
    
    if exists 
		(
			select 1 from guardian
			where 
				districtId = p_districtId
                and sourceId = p_sourceId
                and studentId = @studentId
                and studentSourceId = p_studentSourceId
                and guardianId = p_guardianId
                and firstName = p_firstName
                and lastName = p_lastName
                and email = p_email
                and type = p_type
		)
	then
		update 
			guardian
		set
			importStatus = 'OK'
		where 
			districtId = p_districtId
			and sourceId = p_sourceId
			and studentId = @studentId
			and studentSourceId = p_studentSourceId
			and guardianId = p_guardianId
			and firstName = p_firstName
			and lastName = p_lastName
			and email = p_email
			and type = p_type;
			
    else 
		-- Not found or Different
        -- either way, lets change the student.impotStatus flag
        -- Only change OK.  NEW or CHANGE or DELETE, leave as is!
        
        -- this is done in the post import now.
        /*
        update
			student
		set
			importStatus = 'CHANGED'
		where
			id = @studentId
            and importStatus = 'OK';
        */
        
        insert into guardian(districtId, sourceId, studentId,  studentSourceId, guardianId, firstName, lastName, email, type, importStatus)
        values (p_districtId, p_sourceId, @studentId, p_studentSourceId, p_guardianId, p_firstName, p_lastName, p_email, p_type, 'NEW')
        on duplicate key update
			importStatus = 'CHANGED',
			studentId = @studentId,
			studentSourceId = p_studentSourceId,
            guardianId = p_guardianId,
            firstName = p_firstName,
            lastName = p_lastName,
            email = p_email,
            type = p_type
            
            ;
        
            
		
            
            
	end if;
    
    
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `post_sent_bosco` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `post_sent_bosco`(
	p_districtId int,
    p_importDefinitionId varchar(50),
    p_isRoster tinyint,
    p_isSisData tinyint
    
    )
BEGIN
	-- this will be called AFTER the data is sent to bosco (or atleast pepared in the case of sis_data
    
    update import_definition set lastRunDate = CURRENT_TIMESTAMP where id = p_importDefinitionId;
    
    
    SET SQL_SAFE_UPDATES = 0;
    
	if (p_isRoster > 0)	then 
		-- now we delete any DELETE ones.
    
		delete from student where districtId = p_districtId and importStatus = 'DELETE';
		delete from teacher where districtId = p_districtId and importStatus = 'DELETE';
    
		delete from student_teacher where districtId = p_districtId and importStatus = 'DELETE';
    
		delete from guardian where districtId = p_districtId and importStatus = 'DELETE';
        
        -- Figure this out.
        -- delete from sis_sped where districtId = p_districtId and importStatus = 'DELETE';
    
	end if;
        
	if (p_isSisData > 0) then
		
        
        -- WE need to think about this.
        -- We do updates only on bosco, we do not do any deletes.
        
        /*
        update
			sis_student ss
            join sis_staar sd on sd.id = ss.id
		set
			sd.dirty = 1
		where
			sd.districtId = p_districtId and sd.importStatus != 'OK';
          
		*/
		-- for now, we do the student too.
        
        -- this will change to sis_student being marked as dirty!
        
        update
			student s
            join sis_staar sd on sd.id = s.id
		set
			s.hasNewSisData = 1
		where
			sd.districtId = p_districtId and sd.importStatus != 'OK';
            
		-- follow this pattern for the rest
        
		update
			student s
            join sis_discipline sd on sd.id = s.id
		set
			s.hasNewSisData = 1
		where
			sd.districtId = p_districtId and sd.importStatus != 'OK';

		update
			student s
            join sis_grade sd on sd.id = s.id
		set
			s.hasNewSisData = 1
		where
			sd.districtId = p_districtId and sd.importStatus != 'OK';

		update
			student s
            join sis_map sd on sd.id = s.id
		set
			s.hasNewSisData = 1
		where
			sd.districtId = p_districtId and sd.importStatus != 'OK';

		update
			student s
            join sis_mclass sd on sd.id = s.id
		set
			s.hasNewSisData = 1
		where
			sd.districtId = p_districtId and sd.importStatus != 'OK';

		update
			student s
            join sis_telpas sd on sd.id = s.id
		set
			s.hasNewSisData = 1
		where
			sd.districtId = p_districtId and sd.importStatus != 'OK';
            
		/*
		update
			student s
            join sis_attendance sd on sd.id = s.id
		set
			s.hasNewSisData = 1
		where
			sd.districtId = p_districtId and sd.importStatus != 'OK';

        */
        

    
    end if;
		
    
    
    
		
	SET SQL_SAFE_UPDATES = 1;
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `prep_import` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `prep_import`(
	IN p_districtId int,
    IN p_importDefinitionId varchar(50),
    IN p_isRoster tinyint,
    IN p_isSisData tinyint,
    IN p_log varchar(500),
    OUT p_importId int
)
BEGIN

	insert into 
		import (districtId, importDefinitionId, log)
	values (p_districtId, p_importDefinitionId, p_log);
    
    set p_importId = LAST_INSERT_ID();

	SET SQL_SAFE_UPDATES = 0;
    
    if (p_isRoster > 0)
		then
    
			insert into log_info (importId, info) values (p_importId, 'Preping Roster data');
			-- for any that are set to OK already change to DELETE.
			update student set importStatus = 'DELETE'  where districtId = p_districtId; -- and importStatus = 'OK';
			update teacher set importStatus = 'DELETE'  where districtId = p_districtId; 
			
			update guardian set importStatus = 'DELETE'  where districtId = p_districtId; 
			
			update student_teacher set importStatus = 'DELETE'  where districtId = p_districtId;
            
            -- THIS is TODO: we need to figure out the sis data for this.
            -- for now, this is OK, but review.
            update sis_sped set importStatus = 'DELETE'  where districtId = p_districtId;
			-- do for other tables too, but maybe just Student and Teacher!
			
			delete from student_class where districtId = p_districtId;
			delete from teacher_class where districtId = p_districtId;
		end if;
	
    if (p_isSisData > 0)
		then
        
			insert into log_info (importId, info) values (p_importId, 'Preping Sis data');
            
            -- This will simply mark the students as having no new sis data.  
            -- This should be removed after the student registration is in place.
            update student set hasNewSisData = 0 where districtId = p_districtId;
			
		
			
        end if;
    
    SET SQL_SAFE_UPDATES = 1;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `prep_send_bosco` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `prep_send_bosco`(
	p_districtId int,
    p_importDefinitionId varchar(50),
    p_isRoster tinyint,
    p_isSisData tinyint
    
    )
BEGIN
	-- this will be called after the import has completed, but before data is sent to bosco
    -- for sis data ???
    
    
    
	SET SQL_SAFE_UPDATES = 0;
    
	if (p_isRoster > 0)
		then 
		
			
			update
				student s
				join guardian g on s.districtId = g.districtId and s.id = g.studentId
			set
				s.importStatus = 'CHANGED'
			where
				s.districtId = p_districtId
				and g.importStatus != "OK"
				and s.importStatus = 'OK';
			
			update
				student s
				join student_teacher st on s.districtId = st.districtId and s.id = st.studentId 
			set
				s.importStatus = 'CHANGED'
			where
				s.districtId = p_districtId
				and st.importStatus != "OK"
				and s.importStatus = 'OK';
        
        /*
			THis is not part of sis.  We do not update the sutdent based on this.
			update
				student s
                join sis_sped ss on s.districtId = ss.districtId and s.id = ss.id
			set
				s.importStatus = 'CHANGED'
			where
				s.districtId = p_districtId
				and ss.importStatus != "OK"
				and s.importStatus = 'OK';
          */      
		end if;
        
	-- if (p_isSisData > 0) then
		
		
    -- end if;
		
        
	
    
    
		
	SET SQL_SAFE_UPDATES = 1;
    
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sis_discipline_add` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sis_discipline_add`(
	p_districtId int,
	p_id varchar(255),
    p_issDays varchar(50),
    p_ossDays varchar(50),
    p_aepDays varchar(50),
    p_grade varchar(5),
    p_schoolYear varchar(50)
    )
BEGIN
    
    if not exists 
		(
			select 1 from sis_discipline
			where 
				districtId = p_districtId
				and id = p_id
                and issDays = p_issDays
                and ossDays = p_ossDays
                and aepDays = p_aepDays
                and grade = p_grade
                and schoolYear = p_schoolYear
		)
	then
		-- we did not find an exact match..
        
		insert into sis_discipline (districtId, id, issDays, ossDays, aepDays, grade, schoolYear, importStatus)  
        values (p_districtId, p_id, p_issDays, p_ossDays, p_aepDays, p_grade, p_schoolYear, 'NEW')
        on duplicate key update
        	issDays = p_issDays,
			ossDays = p_ossDays,
			aepDays = p_aepDays,
            grade = p_grade,
			schoolYear = p_schoolYear,
              importStatus = 'CHANGED'

            ;
	end if;
    
    
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sis_grade_add` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sis_grade_add`(
	p_districtId int,
	p_id varchar(255),
    p_schoolYear varchar(50),
    p_period varchar(50),
    p_code varchar(50),
    p_subject varchar(50),
    p_score int,
    p_csaCode varchar(5)
    )
BEGIN
    
    if not exists 
		(
			select 1 from sis_grade 
			where 
				districtId = p_districtId
				and id = p_id
				and schoolYear = p_schoolYear
				and period = p_period
				and code = p_code
				and subject = p_subject
 				and score = p_score
                and csaCode = p_csaCode
		)
	then
		-- we did not find an exact match..
        
        insert into sis_grade (districtId, id, schoolYear, period, code, subject, score, csaCode, importStatus)  
        values (p_districtId, p_id, p_schoolYear, p_period, p_code, p_subject, p_score, p_csaCode, 'NEW')
		on duplicate key update
			period = p_period,
			code = p_code,
			subject = p_subject,
			score = p_score,
            csaCode = p_csaCode,
            importStatus = 'CHANGED'

            ;

            
            
	end if;
    
    
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sis_map_add` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sis_map_add`(
	p_districtId int,
	p_id varchar(255),
    p_schoolYear varchar(50),
    p_period varchar(50),
    p_subject varchar(50),
    p_proficiency varchar(50),
    p_proficiencyCode varchar(5),
    p_score int,
    p_csaCode varchar(5)
    )
BEGIN
    
    if not exists 
		(
			select 1 from sis_map 
			where 
				districtId = p_districtId
				and id = p_id
				and schoolYear = p_schoolYear
				and period = p_period
                and subject = p_subject
                and proficiency = p_proficiency
                and proficiencyCode = p_proficiencyCode
                and score = p_score      
                and csaCode = p_csaCode
		)
	then
		-- we did not find an exact match..
            
		insert into sis_map (districtId, id, schoolYear, period, subject, proficiency, proficiencyCode, score, csaCode, importStatus)  
        values (p_districtId, p_id, p_schoolYear, p_period, p_subject, p_proficiency, p_proficiencyCode, p_score, p_csaCode, 'NEW')

		 on duplicate key update
            period = p_period,
            subject = p_subject, 
            proficiency = p_proficiency,
            proficiencyCode = p_proficiencyCode, 
            score = p_score,
            csaCode = p_csaCode,
			importStatus = 'CHANGED'

            ;
            
	end if;
    
    
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sis_mclass_add` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sis_mclass_add`(
	p_districtId int,
	p_id varchar(255),
    p_schoolYear varchar(50),
    p_period varchar(50),
    p_subject varchar(50),
    p_proficiency varchar(50),
	p_proficiencyCode varchar(5),
    p_score int,
    p_csaCode varchar(5)
    )
BEGIN
    
    if not exists 
		(
			select 1 from sis_mclass
			where 
				districtId = p_districtId
				and id = p_id
				and schoolYear = p_schoolYear
				and period = p_period
                and subject = p_subject
                and proficiency = p_proficiency
                and proficiencyCode = p_proficiencyCode
                and score = p_score      
                and csaCode = p_csaCode  
		)
	then
		-- we did not find an exact match..
              
		insert into sis_mclass (districtId, id, schoolYear, period, subject, proficiency, proficiencyCode, score, csaCode, importStatus) 
        values (p_districtId, p_id, p_schoolYear, p_period, p_subject, p_proficiency, p_proficiencyCode, p_score, p_csaCode, 'NEW')
		on duplicate key update
    
			period = p_period,
			subject = p_subject,
            proficiency = p_proficiency,
			proficiencyCode = p_proficiencyCode,
			score = p_score,
			csaCode = p_csaCode,
             importStatus = 'CHANGED'

            ;
	
	end if;
    
    
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sis_sped_add` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sis_sped_add`(
	p_districtId int,
    p_id varchar(45),
    p_specialEd tinyint,    
    p_bilingual tinyint,
    p_esl tinyint,
    p_section504 tinyint,
    p_iepStartDate varchar(45),
    p_iepEndDate varchar(45)
    )
BEGIN
    
    if exists 
		(
			select 1 from sis_sped
			where 
				districtId = p_districtId
                and id = p_id
                and specialEd = p_specialEd
                and bilingual = p_bilingual
                and esl = p_esl
                and section504 = p_section504
                and iepStartDate = p_iepStartDate
                and iepEndDate = p_iepEndDate                
		)
	then
		update 
			sis_sped
		set
			importStatus = 'OK'
		where 
			districtId = p_districtId
            and id = p_id
			and specialEd = p_specialEd
			and bilingual = p_bilingual
			and esl = p_esl
			and section504 = p_section504
			and iepStartDate = p_iepStartDate
			and iepEndDate = p_iepEndDate
                ;
			
    else 
    
		insert into sis_sped (districtId, id, specialEd, bilingual, esl, section504, iepStartDate, iepEndDate, importStatus)
        values (p_districtId, p_id, p_specialEd, p_bilingual, p_esl, p_section504, p_iepStartDate, p_iepEndDate, 'NEW')
        on duplicate key update
			importStatus = 'CHANGED',			
			specialEd = p_specialEd,
            bilingual = p_bilingual,
			esl = p_esl,
			section504 = p_section504,
			iepStartDate = p_iepStartDate,
			iepEndDate = p_iepEndDate			
            ;
        
            
		
            
            
	end if;
    
    
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sis_staar_add` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sis_staar_add`(
	p_districtId int,
	p_id varchar(255),
    p_schoolYear varchar(50),
    p_subject varchar(50),
    p_code varchar(50),
    p_grade varchar(5),
    p_proficiency varchar(50),
    p_proficiencyCode varchar(5),
    p_csaCode varchar(5)
    )
BEGIN
    
    if not exists 
		(
			select 1 from sis_staar
			where 
				districtId = p_districtId
				and id = p_id
                and schoolYear = p_schoolYear
                and subject = p_subject
                and code = p_code
                and grade = p_grade
                and proficiency = p_proficiency                
                and proficiencyCode = p_proficiencyCode
                and csaCode = p_csaCode
		)
	then
		-- we did not find an exact match..
        insert into sis_staar (districtId, id, schoolYear, subject, code, grade, proficiency, proficiencyCode, csaCode, importStatus)  
        values (p_districtId, p_id, p_schoolYear, p_subject, p_code, p_grade, p_proficiency, p_proficiencyCode, p_csaCode, 'NEW')
        on duplicate key update
            grade = p_grade,
            proficiency = p_proficiency, 
            proficiencyCode = p_proficiencyCode, 
            csaCode = p_csaCode,
			importStatus = 'CHANGED'

            ;
            
	end if;
    
    
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sis_telpas_add` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sis_telpas_add`(
	p_districtId int,
	p_id varchar(255),
    p_schoolYear varchar(50),
    p_grade varchar(5),
    p_proficiency varchar(50),
    p_listeningScore int,
    p_speakingScore int,
    p_readingScore int,
    p_writingScore int
    
    )
BEGIN
    
    if not exists 
		(
			select 1 from sis_telpas
			where 
				districtId = p_districtId
				and id = p_id
                and schoolYear = p_schoolYear
                and grade = p_grade
                and proficiency = p_proficiency        
                and listeningScore = p_listeningScore
                and speakingScore = p_speakingScore
                and readingScore = p_readingScore
                and writingScore = p_writingScore
		)
	then
		-- we did not find an exact match..
		insert into sis_telpas (districtId, id, schoolYear, grade, proficiency, listeningScore, speakingScore, readingScore, writingScore, importStatus)  
        values (p_districtId, p_id, p_schoolYear, p_grade, p_proficiency, p_listeningScore, p_speakingScore, p_readingScore, p_writingScore, 'NEW')
        on duplicate key update
			proficiency = p_proficiency, 
			listeningScore = p_listeningScore,
			speakingScore = p_speakingScore,
			readingScore = p_readingScore,
			writingScore = p_writingScore,
              importStatus = 'CHANGED'

            ;
            
            
	end if;
    
    
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `student_add` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `student_add`(
	p_id varchar(50),
	p_districtId int,
    p_sourceId varchar(50),
    p_studentNumber varchar(50),
    p_firstName varchar(50),
    p_lastName varchar(50),
    p_grade varchar(5),
    p_schoolSourceId varchar(50)
    )
BEGIN
    
    if exists 
		(
			select 1 from student
			where 
				id = p_id
				and districtId = p_districtId
                and sourceId = p_sourceId
                and studentNumber = p_studentNumber
                and firstName = p_firstName
                and lastName = p_lastName
                and grade = p_grade
                and schoolSourceId = p_schoolSourceId
		)
	then
		-- Found, exact match, so OK
        
        update
			student
		set
			importStatus = 'OK'
		where
			id = p_id;
	else
		-- Not found or Different
        
        insert into student (id, districtId, importStatus, sourceId, studentNumber, firstName, lastName, grade, schoolSourceId)
        values (p_id, p_districtId, 'NEW', p_sourceId, p_studentNumber, p_firstName, p_lastName, p_grade, p_schoolSourceId)
        on duplicate key update
			importStatus = 'CHANGED',
			sourceId = p_sourceId,
            studentNumber = p_studentNumber,
            firstName = p_firstName, 
            lastName = p_lastName, 
            grade = p_grade, 
            schoolSourceId = p_schoolSourceId
            
            ;
        
            
		
            
            
	end if;
    
    
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `student_demographics_save` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `student_demographics_save`(
    p_id varchar(45),
    p_dob varchar(45),
    p_gender varchar(5),
    p_americanIndianOrAlaskaNative tinyint,
    p_asian tinyint,
    p_blackOrAfricanAmerican tinyint,
    p_nativeHawaiianOrOtherPacificIslander tinyint,
    p_white tinyint,
    p_hispanicOrLatinoEthnicity tinyint
    
)
BEGIN

	-- This solution works fine with the case statement.
    

	-- so this will not INSERT.  It will only update.
    -- it will change the status if need be.
    
	if not exists 
		(
			select 1 from student
			where 
                id = p_id
                and dob = p_dob
                and gender = p_gender
                and americanIndianOrAlaskaNative = p_americanIndianOrAlaskaNative
                and asian = p_asian
                and blackOrAfricanAmerican = p_blackOrAfricanAmerican
                and nativeHawaiianOrOtherPacificIslander = p_nativeHawaiianOrOtherPacificIslander
                and white = p_white
                and hispanicOrLatinoEthnicity = p_hispanicOrLatinoEthnicity
		
        )
	then
		-- it is different or does not exists.
        -- we only update, we do not insert.
        
        -- we only want to update the importStatus for 'OK'
        
        update
			student
		set
			dob = p_dob,
            gender = p_gender,
            americanIndianOrAlaskaNative = p_americanIndianOrAlaskaNative,
			asian = p_asian,
            blackOrAfricanAmerican = p_blackOrAfricanAmerican,
            nativeHawaiianOrOtherPacificIslander = p_nativeHawaiianOrOtherPacificIslander,
            white = p_white,
            hispanicOrLatinoEthnicity = p_hispanicOrLatinoEthnicity,
            importStatus = case
				when importStatus = 'OK' then 'CHANGED' else importStatus
			end
		where
			id = p_id
    
                ;
			
            
	end if;
    
    

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `student_teacher_build` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `student_teacher_build`(
	p_districtId int
)
BEGIN
	
    
    -- delete from student_teacher where importId = p_importId;
    
    insert into 
		student_teacher (districtId, studentId, teacherId, importStatus)
	select
		distinct
			p_districtId, s.id, t.id, 'NEW'
		from
			student_class sc
            join student s on s.districtId = sc.districtId and s.sourceId = sc.studentSourceId
            join teacher_class tc on tc.districtId = sc.districtId and tc.classSourceId = sc.classSourceId
            join teacher t on t.districtId = tc.districtId and t.sourceId = tc.teacherSourceId            
		where
			sc.districtId = p_districtId
	on duplicate key update 
		importStatus = 'OK';
	
    
   
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `teacher_add` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `teacher_add`(
	p_id varchar(50),
	p_districtId int,
    p_sourceId varchar(50),
    p_teacherId varchar(50),
    p_firstName varchar(50),
    p_lastName varchar(50),
    p_email varchar(255),
    p_schoolSourceId varchar(45)
    )
BEGIN
    
    if exists 
		(
			select 1 from teacher
			where 
				id = p_id
				and districtId = p_districtId
                and sourceId = p_sourceId
                and teacherId = p_teacherId
                and firstName = p_firstName
                and lastName = p_lastName
                and email = p_email
                and schoolSourceId = p_schoolSourceId
		)
	then
		-- Found, exact match, so OK
        
        update
			teacher
		set
			importStatus = 'OK'
		where
			id = p_id;
	else
		-- Not found or Different
        
        insert into teacher (id, districtId, sourceId, teacherId, firstName, lastName, email, schoolSourceId, importStatus)
        values (p_id, p_districtId, p_sourceId, p_teacherId, p_firstName, p_lastName, p_email, p_schoolSourceId, 'NEW')
        on duplicate key update
			importStatus = 'CHANGED',
			sourceId = p_sourceId,
            teacherId = p_teacherId,
            firstName = p_firstName, 
            lastName = p_lastName, 
            email = p_email,
            schoolSourceId = p_schoolSourceId
            
            ;
        
            
		
            
            
	end if;
    
    
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `test` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `test`()
BEGIN

	declare countone int;
    declare counttwo int;
    
    declare per decimal (10,2);
    
    
    
    declare v_maxDeltaPercent decimal(5,2);
    
    select
		maxDeltaPercent
	into
		v_maxDeltaPercent
	from
		import_definition
	where
		id = 'Testing';


select
	count(*)
into 
	countone
from
	student s
where
	s.districtId = 4800030;
    
    
    
select
	count(*)
into 
	counttwo
from
	student s
where
	s.districtId = 4813290;
    
    
    select
		(countone * 100.0) / counttwo
	into
		per;
    
    
    

	if (per > v_maxDeltaPercent) then
		select 'GREATER', per, v_maxDeltaPercent;
	else 
		select 'LESS', per, v_maxDeltaPercent;
	end if;
	
        
        
	-- this should be able to check the tolarances in the procedure.
	-- the return of OK is good, antying else will log and throw an exception
        
	-- select countone, counttwo, per;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `x_clear_all_district_data` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `x_clear_all_district_data`(p_districtId int)
BEGIN


	SET SQL_SAFE_UPDATES = 0;
    
    delete from student where districtId = p_districtId;
    delete from guardian where districtId = p_districtId;
    delete from student_class where districtId = p_districtId;
    delete from teacher_class where districtId = p_districtId;
    delete from student_teacher where districtId = p_districtId;
    delete from student_sped where districtId = p_districtId;
    delete from teacher where districtId = p_districtId;
    
    delete from sis_grade where districtId = p_districtId;
    delete from sis_map where districtId = p_districtId;
    delete from sis_mclass where districtId = p_districtId;
    delete from sis_staar where districtId = p_districtId;
    delete from sis_discipline where districtId = p_districtId;
    
    delete from sis_telpas where  districtId = p_districtId;
    
    
	SET SQL_SAFE_UPDATES = 1;
    

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `x_clear_sis_district` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `x_clear_sis_district`(p_districtId int)
BEGIN


	SET SQL_SAFE_UPDATES = 0;
    
    delete from sis_grade where districtId = p_districtId;
    delete from sis_map where districtId = p_districtId;
    delete from sis_mclass where districtId = p_districtId;
    delete from sis_staar where districtId = p_districtId;
    delete from sis_discipline where districtId = p_districtId;
    
    delete from sis_telpas where  districtId = p_districtId;
    
    
	SET SQL_SAFE_UPDATES = 1;
    

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `x_truncate_tables` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `x_truncate_tables`()
BEGIN
	truncate table guardian;
	truncate table import;
	truncate table log_error;
	truncate table log_info;
	truncate table school;
	truncate table student;
	truncate table student_teacher;
	truncate table student_class;
	truncate table teacher_class;
	truncate table student;
	truncate table teacher;
    
    truncate table sis_grade;
    truncate table sis_discipline;
    truncate table sis_map;
    truncate table sis_mclass;
    truncate table sis_staar;
    
    truncate table sis_telpas;
    
    truncate table sis_sped;
    
    -- for now, we dont do this.
    -- truncate table sis_student;
    
    /*
    SET SQL_SAFE_UPDATES = 0;
	
    SET SQL_SAFE_UPDATES = 1;
    
    */

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-02  9:30:44
