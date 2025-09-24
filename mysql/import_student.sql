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
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `id` varchar(50) NOT NULL,
  `districtId` int NOT NULL,
  `importStatus` varchar(10) NOT NULL,
  `sourceId` varchar(50) NOT NULL,
  `studentNumber` varchar(50) NOT NULL,
  `firstName` varchar(50) NOT NULL,
  `lastName` varchar(50) NOT NULL,
  `grade` varchar(5) NOT NULL DEFAULT '',
  `dob` varchar(50) NOT NULL DEFAULT '',
  `gender` varchar(50) NOT NULL DEFAULT '',
  `schoolSourceId` varchar(50) NOT NULL DEFAULT '',
  `americanIndianOrAlaskaNative` tinyint NOT NULL DEFAULT '0',
  `asian` tinyint NOT NULL DEFAULT '0',
  `blackOrAfricanAmerican` tinyint NOT NULL DEFAULT '0',
  `nativeHawaiianOrOtherPacificIslander` tinyint NOT NULL DEFAULT '0',
  `white` tinyint NOT NULL DEFAULT '0',
  `hispanicOrLatinoEthnicity` tinyint NOT NULL DEFAULT '0',
  `demographicRaceTwoOrMoreRaces` tinyint NOT NULL DEFAULT '0',
  `isEsl` tinyint NOT NULL DEFAULT '0',
  `is504` tinyint NOT NULL DEFAULT '0',
  `isBilingual` tinyint NOT NULL DEFAULT '0',
  `isSpecialEd` tinyint NOT NULL DEFAULT '0',
  `entryIepDate` varchar(50) NOT NULL DEFAULT '',
  `hasNewSisData` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-24  7:26:39
