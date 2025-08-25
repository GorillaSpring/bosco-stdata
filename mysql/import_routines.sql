CREATE DATABASE  IF NOT EXISTS `import` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `import`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: import
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
-- Dumping routines for database 'import'
--
/*!50003 DROP PROCEDURE IF EXISTS `diff_imports` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `diff_imports`(p_importId int, p_baseimportId int)
BEGIN
	
    -- school
    
	UPDATE 
		school s
	JOIN 
		school bs ON bs.importId = p_baseImportId AND bs.sourceId = s.sourceId 
	SET 
		s.changed = 0
	WHERE
		s.importId = p_importId
        AND s.name = bs.name;
        
	-- teacher
	UPDATE 
		teacher t
	JOIN 
		teacher bt ON bt.importId = p_baseImportId AND bt.sourceId = t.sourceId 
	SET 
		t.changed = 0
	WHERE
		t.importId = p_importId
        AND t.firstName = bt.firstName
        AND t.lastName = bt.lastName
        AND t.email = bt.email;
        
	-- student
    
	UPDATE 
		student s
	JOIN 
		student bs ON bs.importId = p_baseImportId AND bs.sourceId = s.sourceId 
	SET 
		s.changed = 0
	WHERE
		s.importId = p_importId
        AND s.studentNumber = bs.studentNumber
        AND s.firstName = bs.firstName
        AND s.lastName = bs.lastName
        AND s.grade = bs.grade
        AND s.dob = bs.dob
        AND s.gender = bs.gender
        AND s.schoolCode = bs.schoolCode
        AND s.americanIndianOrAlaskaNative = bs.americanIndianOrAlaskaNative
        AND s.asian = bs.asian
        AND s.blackOrAfricanAmerican = bs.blackOrAfricanAmerican
        AND s.nativeHawaiianOrOtherPacificIslander = bs.nativeHawaiianOrOtherPacificIslander
        AND s.white = bs.white
        AND s.hispanicOrLatinoEthnicity = bs.hispanicOrLatinoEthnicity
        AND s.is504 = bs.is504
        AND s.isEsl = bs.isEsl;
        
        
	-- guardian

        
	UPDATE 
		guardian g
	JOIN
		guardian bg ON bg.importId = p_baseImportId AND bg.sourceId = g.sourceId 
	SET 
		g.changed = 0
	WHERE
		g.importId = p_importId
        AND g.firstName = bg.firstName
        AND g.lastName = bg.lastName
        AND g.email = bg.email;
        
        
	-- student teacher
    update
		student_teacher t
	join 
		student_teacher bt on bt.importId = p_baseImportId and bt.studentSourceId = t.studentSourceId and bt.teacherSourceId = t.teacherSourceId
	set
		t.changed = 0
	where
		t.importId = p_importId;
        
	-- if any student teachers have changed, then the student has changed.
    update
		student s
	join
		student_teacher st on st.importId = s.importId and st.studentSourceId = s.sourceId
	set
		s.changed = 1
	where
		s.importId = p_importId
        and st.changed = 1;
        
	-- if any guardians have changed, then the student has changed.
	
    update
		student s
	join
		guardian g on g.importId = s.importId and g.studentSourceId = s.sourceId
	set
		s.changed = 1
	where
		s.importId = p_importId
        and g.changed = 1;
        
	
		
	-- removed guadians - mark student
    
	update
		student s
	set
		s.changed = 1
	where
		s.importId = p_importId
        and s.sourceId in (
			select
				g.studentSourceId
			from
				guardian g
			where
				g.importId = p_baseImportId
				and g.sourceId not in (
					select
						bg.sourceId
					from
						guardian bg
					where
						bg.importId = p_importId
					)
				);
                
                
			

	-- removed teachers - mark student

	update 
		student s
	set
		s.changed = 1
	where
		s.importId = p_importId
		and s.sourceId in (
			select 
				st.studentSourceId
			from
				student_teacher st
			where
				st.importId = p_baseImportId
				and st.teacherSourceId not in (
					select 
						bst.teacherSourceId
					from
						student_teacher bst
					where
						bst.importId = p_importId
					)
				);
    

	
    
	-- NEXT we will look for adds
    -- this is just student only.
    
    
   update
		student
	set
		changed = 2
	where
		importId = p_importId
        and sourceId not in (
        
			select 
				sourceId 
			from 
				(
				select
					s.sourceId
				from
					student s
				where
					s.importId = p_baseImportId
				) as temp
        
        );
        
        
	-- new teachers
	update
		teacher
	set
		changed = 2
	where
		importId = p_importId
        and sourceId not in (
			select
				sourceId
			from
				(
					select
						t.sourceId
					from
						teacher t
					where
						t.importId = p_baseImportId
                ) as temp
		);
    
    -- new schools
    
    update
		school
	set
		changed = 2
	where
		importId = p_importId
        and sourceId not in (
			select
				sourceId
			from
				(
					select
						s.sourceId
					from
						school s
					where
						s.importId = p_baseImportId
				) as temp
		);
     
  
		
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `import_get_difs` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `import_get_difs`(
	p_importId int
)
BEGIN
	select
		*
	from
		student s
	where
		s.importId = p_importId
		and s.changed > 0;
        
  -- the students above get guarians and teachers.
  
   select 
		*
	from
		guardian g
	where
		g.importId = p_importId
		and g.studentSourceId in (
			
			select
				s.sourceId
			from
				student s
			where
				s.importId = p_importId
				and s.changed > 0
        );
        
  -- teachers for changed students
	select
		*
	from
		studentteacher st
	where
		st.importId = p_importId
		and st.studentSourceId in (
			select
				s.sourceId
			from
				student s
			where
				s.importId = p_importId
				and s.changed > 0
        
        );
        
	
        
	select
		*
	from
		teacher t
	where
		t.importId = p_importId
		and t.changed = 1;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `import_system_startup` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `import_system_startup`()
BEGIN
	update
		system_status
	set
		status = 0
	where
		systemKey = 'Import';
	
	update
		system_status
	set
		status = 0
	where
		systemKey = 'DataRequest';
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `post_sis_data` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `post_sis_data`(p_districtId int)
BEGIN
	SET SQL_SAFE_UPDATES = 0;
    
    
--	update
--		sis_student
--	set
--		dirty = 1
--	where
--		id in (
--			select distinct id from sis_academic_grade where districtId = p_districtId and imported = 0
 --       );
        
        
	update
		sis_student sd
		join sis_academic_grade sag on sd.id = sag.id
	set
		sd.dirty = 1
	where
		
		sag.districtId = p_districtId and sag.imported = 0;
        
	
	delete from sis_academic_grade where districtId = p_districtId and imported = 0;
    
    update
		sis_student sd
		join sis_map sag on sd.id = sag.id
	set
		sd.dirty = 1
	where
		sag.districtId = p_districtId and sag.imported = 0;
        
	
	delete from sis_map where districtId = p_districtId and imported = 0;
    
    
	update
		sis_student sd
		join sis_mclass sag on sd.id = sag.id
	set
		sd.dirty = 1
	where
		sag.districtId = p_districtId and sag.imported = 0;
        
	
	delete from sis_mclass where districtId = p_districtId and imported = 0;

   
	update
		sis_student sd
		join sis_staar sag on sd.id = sag.id
	set
		sd.dirty = 1
	where
		sag.districtId = p_districtId and sag.imported = 0;
        
	
	delete from sis_staar where districtId = p_districtId and imported = 0;


	update
		sis_student sd
		join sis_discipline sag on sd.id = sag.id
	set
		sd.dirty = 1
	where
		sag.districtId = p_districtId and sag.imported = 0;
        
	
	delete from sis_discipline where districtId = p_districtId and imported = 0;

    
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
	IN p_distrcitId int,
    IN p_log varchar(500),
    OUT p_importId int
)
BEGIN
	insert into 
		import (districtId, log) 
	values (p_distrcitId, p_log);

	set p_importId = LAST_INSERT_ID();

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `prep_sis_data` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `prep_sis_data`(p_districtId int)
BEGIN
	-- this will do this for ALL data
    
	SET SQL_SAFE_UPDATES = 0;
	update sis_academic_grade set imported = 0 where districtId = p_districtId;
    
    update sis_map set imported = 0 where districtId = p_districtId;
    update sis_mclass set imported = 0 where districtId = p_districtId;
    update sis_staar set imported = 0 where districtId = p_districtId;
    update sis_discipline set imported = 0 where districtId = p_districtId;
    
    
	SET SQL_SAFE_UPDATES = 1;
    
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sis_academic_grade_add` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sis_academic_grade_add`(
	p_districtId int,
	p_id varchar(255),
    p_schoolYear varchar(50),
    p_term varchar(50),
    p_courseNumber varchar(50),
    p_courseName varchar(50),
    p_grade int
    )
BEGIN
    
    if exists 
		(
			select 1 from sis_academic_grade 
			where 
				districtId = p_districtId
				and id = p_id
				and schoolYear = p_schoolYear
				and term = p_term
				and courseNumber = p_courseNumber
				and courseName = p_courseName
 				and grade = p_grade
		)
	then
		-- set imported to true
        update
			sis_academic_grade
		set
			imported = 1
		where
			districtId = p_districtId
			and id = p_id
			and schoolYear = p_schoolYear
			and term = p_term
			and courseNumber = p_courseNumber
			and courseName = p_courseName
			and grade = p_grade;
	else
		-- we add it and set dirty to ture on the student_sis_data
        update
			sis_student
		set
			dirty = 1
		where
			id = p_id;
            
		insert into sis_academic_grade (districtId, id, schoolYear, term, courseNumber, courseName, grade)  
        values (p_districtId, p_id, p_schoolYear, p_term, p_courseNumber, p_courseName, p_grade);

            
            
	end if;
    
    
    
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
    p_schoolYear varchar(50)
    )
BEGIN
    
    if exists 
		(
			select 1 from sis_discipline
			where 
				districtId = p_districtId
				and id = p_id
                and issDays = p_issDays
                and ossDays = p_ossDays
                and aepDays = p_aepDays
                and schoolYear = p_schoolYear
		)
	then
		-- set imported to true
        update
			sis_discipline
		set
			imported = 1
		where
			districtId = p_districtId
			and id = p_id
			and issDays = p_issDays
			and ossDays = p_ossDays
			and aepDays = p_aepDays
			and schoolYear = p_schoolYear;
	else
		-- we add it and set dirty to ture on the student_sis_data
        update
			sis_student
		set
			dirty = 1
		where
			id = p_id;
            
		insert into sis_discipline (districtId, id, issDays, ossDays, aepDays, schoolYear)  
        values (p_districtId, p_id, p_issDays, p_ossDays, p_aepDays, p_schoolYear);

            
            
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
    p_term varchar(50),
    p_subject varchar(50),
    p_level varchar(50),
    p_score int
    )
BEGIN
    
    if exists 
		(
			select 1 from sis_map 
			where 
				districtId = p_districtId
				and id = p_id
				and schoolYear = p_schoolYear
				and term = p_term
                and subject = p_subject
                and score = p_score      
		)
	then
		-- set imported to true
        update
			sis_map
		set
			imported = 1
		where
			districtId = p_districtId
			and id = p_id
			and schoolYear = p_schoolYear
			and term = p_term
			and subject = p_subject
            and score = p_score;
	else
		-- we add it and set dirty to ture on the student_sis_data
        update
			sis_student
		set
			dirty = 1
		where
			id = p_id;
            
		insert into sis_map (districtId, id, schoolYear, term, subject, level, score)  
        values (p_districtId, p_id, p_schoolYear, p_term, p_subject, p_level, p_score);

            
            
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
    p_term varchar(50),
    p_subject varchar(50),
    p_level varchar(50),
    p_score int
    )
BEGIN
    
    if exists 
		(
			select 1 from sis_mclass
			where 
				districtId = p_districtId
				and id = p_id
				and schoolYear = p_schoolYear
				and term = p_term
                and subject = p_subject
                and score = p_score      
		)
	then
		-- set imported to true
        update
			sis_mclass
		set
			imported = 1
		where
			districtId = p_districtId
			and id = p_id
			and schoolYear = p_schoolYear
			and term = p_term
			and subject = p_subject
            and score = p_score;
	else
		-- we add it and set dirty to ture on the student_sis_data
        update
			sis_student
		set
			dirty = 1
		where
			id = p_id;
            
		insert into sis_mclass (districtId, id, schoolYear, term, subject, level, score)  
        values (p_districtId, p_id, p_schoolYear, p_term, p_subject, p_level, p_score);

            
            
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
    p_testDate varchar(50),
    p_stateAssessmentSubject varchar(50),
    p_gradeDuringAssessment varchar(50),
    p_stateAssessmentScore varchar(50)
    )
BEGIN
    
    if exists 
		(
			select 1 from sis_staar
			where 
				districtId = p_districtId
				and id = p_id
                and testDate = p_testDate
                and stateAssessmentSubject = p_stateAssessmentSubject
                and gradeDuringAssessment = p_gradeDuringAssessment
                and stateAssessmentScore = p_stateAssessmentScore
		)
	then
		-- set imported to true
        update
			sis_staar
		set
			imported = 1
		where
			districtId = p_districtId
			and id = p_id
			and testDate = p_testDate
			and stateAssessmentSubject = p_stateAssessmentSubject
			and gradeDuringAssessment = p_gradeDuringAssessment
			and stateAssessmentScore = p_stateAssessmentScore;
	else
		-- we add it and set dirty to ture on the student_sis_data
        update
			sis_student
		set
			dirty = 1
		where
			id = p_id;
            
		insert into sis_staar (districtId, id, testDate, stateAssessmentSubject, gradeDuringAssessment, stateAssessmentScore)  
        values (p_districtId, p_id, p_testDate, p_stateAssessmentSubject, p_gradeDuringAssessment, p_stateAssessmentScore);

            
            
	end if;
    
    
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `student_get` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `student_get`(
	importId int,
    studentId VARCHAR(255)
)
BEGIN


	select
		*
	from
		student s
		left join school sch on sch.sourceId = s.schoolCode and sch.ImportId = s.ImportId 
	where
		s.ImportId = importId 
		and s.SourceId = studentId;
		

	select
		*
	from
		guardian g
	where
		g.ImportId = importId 
		and g.studentSourceId = studentId;

	select
		t.*
	from
		studentClass sc
		join teacherClass tc on tc.ClassSourceId = sc.ClassSourceId and tc.ImportId = sc.ImportId
		join teacher t on t.SourceId = tc.TeacherSourceId and t.ImportId = tc.ImportId 
	where
		sc.ImportId = importId 
		and sc.StudentSourceId = studentId;
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
	p_importId int
)
BEGIN
	-- flush and fill
    
    delete from student_teacher where importId = p_importId;
	
	insert into 
		student_teacher (importId, studentSourceID, teacherSourceId, changed)
	select distinct
		sc.importId, sc.studentSourceId, tc.teacherSourceId, 1
	from
		student_class sc
		join teacher_class tc on tc.importId = sc.importId and tc.classSourceId = sc.classSourceId
	where
		sc.importId = p_importId;
		
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
	truncate table student_class;
	truncate table student_teacher;
	truncate table student;
	truncate table teacher;
	truncate table teacher_class;
    
    truncate table sis_academic_grade;
    truncate table sis_discipline;
    truncate table sis_map;
    truncate table sis_mclass;
    truncate table sis_staar;
    
    
    -- for now, we dont do this.
    -- truncate table sis_student;
    
    SET SQL_SAFE_UPDATES = 0;
	update import_definition set baseImportId = 0 ;
    SET SQL_SAFE_UPDATES = 1;

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

-- Dump completed on 2025-08-25 13:55:57
