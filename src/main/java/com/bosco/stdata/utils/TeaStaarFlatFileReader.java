package com.bosco.stdata.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bosco.stdata.teaModel.CelinaCombo;
import com.bosco.stdata.teaModel.DibelsMClass;
import com.bosco.stdata.teaModel.FindUsers;
import com.bosco.stdata.teaModel.GradeFileCelina;
import com.bosco.stdata.teaModel.GradeFileMelissa;
import com.bosco.stdata.teaModel.MapCourseNameCsaCode;
import com.bosco.stdata.teaModel.Star2024;

import com.bosco.stdata.teaModel.Star2024EOC;
import com.bosco.stdata.teaModel.Telpas2024;

//public class TeaStaarFlatFileReader implements ItemStream{


public class TeaStaarFlatFileReader {

    
  
    


    // Read this file:  SF_0524_3-8_043908_MELISSA ISD_V01
    // STAAR Grades 3–8 2024 Test Administration
    // https://tea.texas.gov/student-assessment/student-assessment-results/2024-staar-3-8-data-file.pdf



    // So we can just define the ones we need, the rest are fine.
    // we should define a throwaway for the rest of the line.'
    
    
// So lets try fror the csv.

 @Bean
    public FlatFileItemReader<FindUsers> findUsers(String filePath) {
        return new FlatFileItemReaderBuilder<FindUsers>()
            .name("findUsers")
            .resource(new FileSystemResource(filePath)) // Path to your CSV file  Try FileSystemResource instead
            .linesToSkip(1) // Skip header line if present
            .delimited()
            .names(new String[]{"id", "email"}) 
            .targetType(FindUsers.class) // Specify the target object type
            .build();
    }



    @Bean
    public FlatFileItemReader<MapCourseNameCsaCode> mapCourseNameCsaCodeReader(String filePath) {
        return new FlatFileItemReaderBuilder<MapCourseNameCsaCode>()
            .name("mapCourseNameCsaCodeReader")
            .resource(new FileSystemResource(filePath)) // Path to your CSV file  Try FileSystemResource instead
            .linesToSkip(1) // Skip header line if present
            .delimited()
            .names(new String[]{"districtId", "courseName", "csaCode"}) 
            .targetType(MapCourseNameCsaCode.class) // Specify the target object type
            .build();
    }


    @Bean
    public FlatFileItemReader<GradeFileMelissa> gradeMelissaReader(String filePath) {
        return new FlatFileItemReaderBuilder<GradeFileMelissa>()
            .name("gradeMelissaReader")
            .resource(new FileSystemResource(filePath)) // Path to your CSV file  Try FileSystemResource instead
            .linesToSkip(1) // Skip header line if present
            .delimited()            
            .names(new String[]{"studentSourceId", "studentNumber", "courseName", "courseId", "schoolYear", "term", "courseGrade", "changedDateTime"}) 
            .targetType(GradeFileMelissa.class) // Specify the target object type
            .build();
    }


    @Bean
    public FlatFileItemReader<GradeFileCelina> gradeCelinaReader(String filePath) {
        return new FlatFileItemReaderBuilder<GradeFileCelina>()
            .name("gradeCelinaReader")
            .resource(new FileSystemResource(filePath)) // Path to your CSV file  Try FileSystemResource instead
            .linesToSkip(1) // Skip header line if present
            .delimited()
            .delimiter("\t")
            .names(new String[]{"studentSourceId", "studentNumber", "courseName", "courseId", "schoolYear", "term", "courseGrade"}) 
            .targetType(GradeFileCelina.class) // Specify the target object type
            .build();
    }

    @Bean
    public FlatFileItemReader<DibelsMClass> dibelesMClassReader(String filePath) {
        return new FlatFileItemReaderBuilder<DibelsMClass>()
            .name("dibelesMClassReader")
            .resource(new FileSystemResource(filePath)) // Path to your CSV file  Try FileSystemResource instead
            .linesToSkip(1) // Skip header line if present
            .delimited()
            .names(new String[]{"schoolYear", "state", "multi_districtOrganizationName", "reportingGroupName", "districtName", "districtPrimaryID", "schoolName", "schoolPrimaryID", "studentLastName", "studentFirstName", "studentPrimaryID", "enrollmentTeacherName", "enrollmentTeacherStaffID", "enrollmentClassName", "enrollmentClassID", "enrollmentGrade", "assessingTeacherName", "assessingTeacherStaffID", "assessmentClassName", "assessmentClassID", "assessment", "assessmentEdition", "assessmentGrade", "benchmarkPeriod", "completionStatus", "deviceDate", "syncDate", "compositeLevel", "compositeScore", "compositeScore_lexile", "composite_localPercentile", "composite_nationalNormPercentile", "composite_semesterGrowth", "composite_yearGrowth", "letterNames_INF_level", "letterNames_INF_score", "letterNames_INF_localPercentile", "letterNames_INF_nationalNormPercentile", "letterNames_INF_semesterGrowth", "letterNames_INF_yearGrowth", "phonemicAwareness_pSF_level", "phonemicAwareness_pSF_score", "phonemicAwareness_pSF_localPercentile", "phonemicAwareness_pSF_nationalNormPercentile", "phonemicAwareness_pSF_semesterGrowth", "phonemicAwareness_pSF_yearGrowth", "letterSounds_nWF_cLS_level", "letterSounds_nWF_cLS_score", "letterSounds_nWF_cLS_localPercentile", "letterSounds_nWF_cLS_nationalNormPercentile", "letterSounds_nWF_cLS_semesterGrowth", "letterSounds_nWF_cLS_yearGrowth", "decoding_nWF_wRC_level", "decoding_nWF_wRC_score", "decoding_nWF_wRC_localPercentile", "decoding_nWF_wRC_nationalNormPercentile", "decoding_nWF_wRC_semesterGrowth", "decoding_nWF_wRC_yearGrowth", "wordReading_wRF_level", "wordReading_wRF_score", "wordReading_wRF_localPercentile", "wordReading_wRF_nationalNormPercentile", "wordReading_wRF_semesterGrowth", "wordReading_wRF_yearGrowth", "readingAccuracy_oRF_accu_level", "readingAccuracy_oRF_accu_score", "readingAccuracy_oRF_accu_localPercentile", "readingAccuracy_oRF_accu_nationalNormPercentile", "readingAccuracy_oRF_accu_semesterGrowth", "readingAccuracy_oRF_accu_yearGrowth", "readingFluency_oRF_level", "readingFluency_oRF_score", "readingFluency_oRF_localPercentile", "readingFluency_oRF_nationalNormPercentile", "readingFluency_oRF_semesterGrowth", "readingFluency_oRF_yearGrowth", "errorRate_oRF_score", "basicComprehension_maze_level", "basicComprehension_maze_score", "basicComprehension_maze_localPercentile", "basicComprehension_maze_nationalNormPercentile", "basicComprehension_maze_semesterGrowth", "basicComprehension_maze_yearGrowth", "correctResponses_maze_score", "incorrectResponses_maze_score", "vocabulary_level", "vocabulary_score", "spelling_level", "spelling_score", "rAN_level", "rAN_score", "riskIndicator_level", "oralLanguage_level", "oralLanguage_score", "dateofBirth", "gender", "race", "hispanicorLatinoEthnicity", "specialEducation", "disability", "specificDisability", "section504", "iEPStatus", "economicallyDisadvantaged", "mealStatus", "titleI", "migrant", "eLLStatus", "homeLanguage", "secondaryStudentI", "pABlending_pA_b_score"}) // Map CSV columns to Person fields
            .targetType(DibelsMClass.class) // Specify the target object type
            .build();
    }



    @Bean
    public FlatFileItemReader<CelinaCombo> celinaComboItemReader(String filePath) {
        return new FlatFileItemReaderBuilder<CelinaCombo>()
            .name("celinaComboItemReader")
            .resource(new FileSystemResource(filePath)) // Path to your CSV file  Try FileSystemResource instead
            .linesToSkip(1) // Skip header line if present
            .delimited()
            .names(new String[]{"termName", "districtName", "district_StateID", "schoolName", "school_StateID", "studentLastName", "studentFirstName", "studentMI", "studentID", "student_StateID", "studentDateOfBirth", "studentEthnicGroup", "nWEAStandard_EthnicGroup", "studentGender", "grade", "nWEAStandard_Grade", "subject", "course", "normsReferenceData", "wISelectedAYFall", "wISelectedAYWinter", "wISelectedAYSpring", "wIPreviousAYFall", "wIPreviousAYWinter", "wIPreviousAYSpring", "testType", "testName", "testStartDate", "testStartTime", "testDurationMinutes", "testRITScore", "testStandardError", "testPercentile", "achievementQuintile", "percentCorrect", "rapidGuessingPercentage", "fallToFallProjectedGrowth", "fallToFallObservedGrowth", "fallToFallObservedGrowthSE", "fallToFallMetProjectedGrowth", "fallToFallConditionalGrowthIndex", "fallToFallConditionalGrowthPercentile", "fallToFallGrowthQuintile", "fallToWinterProjectedGrowth", "fallToWinterObservedGrowth", "fallToWinterObservedGrowthSE", "fallToWinterMetProjectedGrowth", "fallToWinterConditionalGrowthIndex", "fallToWinterConditionalGrowthPercentile", "fallToWinterGrowthQuintile", "fallToSpringProjectedGrowth", "fallToSpringObservedGrowth", "fallToSpringObservedGrowthSE", "fallToSpringMetProjectedGrowth", "fallToSpringConditionalGrowthIndex", "fallToSpringConditionalGrowthPercentile", "fallToSpringGrowthQuintile", "winterToWinterProjectedGrowth", "winterToWinterObservedGrowth", "winterToWinterObservedGrowthSE", "winterToWinterMetProjectedGrowth", "winterToWinterConditionalGrowthIndex", "winterToWinterConditionalGrowthPercentile", "winterToWinterGrowthQuintile", "winterToSpringProjectedGrowth", "winterToSpringObservedGrowth", "winterToSpringObservedGrowthSE", "winterToSpringMetProjectedGrowth", "winterToSpringConditionalGrowthIndex", "winterToSpringConditionalGrowthPercentile", "winterToSpringGrowthQuintile", "springToSpringProjectedGrowth", "springToSpringObservedGrowth", "springToSpringObservedGrowthSE", "springToSpringMetProjectedGrowth", "springToSpringConditionalGrowthIndex", "springToSpringConditionalGrowthPercentile", "springToSpringGrowthQuintile", "lexileScore", "lexileMin", "lexileMax", "quantileScore", "quantileMin", "quantileMax", "goal1Name", "goal1RitScore", "goal1StdErr", "goal1Range", "goal1Adjective", "goal2Name", "goal2RitScore", "goal2StdErr", "goal2Range", "goal2Adjective", "goal3Name", "goal3RitScore", "goal3StdErr", "goal3Range", "goal3Adjective", "goal4Name", "goal4RitScore", "goal4StdErr", "goal4Range", "goal4Adjective", "goal5Name", "goal5RitScore", "goal5StdErr", "goal5Range", "goal5Adjective", "goal6Name", "goal6RitScore", "goal6StdErr", "goal6Range", "goal6Adjective", "goal7Name", "goal7RitScore", "goal7StdErr", "goal7Range", "goal7Adjective", "goal8Name", "goal8RitScore", "goal8StdErr", "goal8Range", "goal8Adjective", "accommodationCategories", "accommodations", "typicalFallToFallGrowth", "typicalFallToWinterGrowth", "typicalFallToSpringGrowth", "typicalWinterToWinterGrowth", "typicalWinterToSpringGrowth", "typicalSpringToSpringGrowth", "projectedProficiencyStudy1", "projectedProficiencyLevel1", "projectedProficiencyStudy2", "projectedProficiencyLevel2", "projectedProficiencyStudy3", "projectedProficiencyLevel3", "projectedProficiencyStudy4", "projectedProficiencyLevel4", "projectedProficiencyStudy5", "projectedProficiencyLevel5", "projectedProficiencyStudy6", "projectedProficiencyLevel6", "projectedProficiencyStudy7", "projectedProficiencyLevel7", "projectedProficiencyStudy8", "projectedProficiencyLevel8", "projectedProficiencyStudy9", "projectedProficiencyLevel9", "projectedProficiencyStudy10", "projectedProficiencyLevel10"}) // Map CSV columns to Person fields
            .targetType(CelinaCombo.class) // Specify the target object type
            .build();
    }



    // WORKIN ON THIS ONE.  NEED MORE INFO
    // NOT USED CURRELTY

    // @Bean
    // public FlatFileItemReader<Star2024Alt> star2024AltReader(String filePath) {

    //     FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    //         tokenizer.setNames(new String[]{"adminDate", "gradeLevel",  "studentId", "scoreReadingLanguageArts", "scoreMath", "scoreSocialStudies", "scoreScience", "pliReadingLanguageArts",  "pliMath", 
            
    //         "pliSocialStudies", "pliScience", "scoreAlgebra",
            
    //         "throwaway"}); // Names for the fields
    //         tokenizer.setStrict(true);  // should be default.
    //         tokenizer.setColumns(new Range[]{
    //                 new Range(1, 4),    // adminDate   C
    //                 new Range(5,6),  // gradeLevel      C
    //                 new Range(123, 131), // studentID   ** MAY BE BLANK         C
    //                 new Range(351, 351), //scoreReadingLanguageArts         C
    //                 new Range(352,352), //scoreMath                         C
    //                 new Range(354,354), // scoreSocialStudies               C
    //                 new Range(355,355), // scoreScience                 C
    //                 new Range(405,406),  // pliReadingLanguageArts      C (differnt)
                    
    //                 new Range(759,760), // pliMath                         C (differnt)
                    
    //                 new Range(1519, 1520), // pliSocialStudies          NO
    //                 new Range(1919,1920), // pliScience                 NO
    //                 new Range(3100,3100),  /// scoreAlgebra
    //                 new Range(3999)   // throway.                           C
    //         });


    //         // or
    //         //tokenizer.setNames("one", "two", "three", "four");
    //         //tokenizer.setColumns(new Range(1,5), new Range (6,10), new Range(11, 15), new Range(16,20));

    //         BeanWrapperFieldSetMapper<Star2024Alt> mapper = new BeanWrapperFieldSetMapper<Star2024Alt>();
    //         mapper.setTargetType(Star2024Alt.class);



    //     return new FlatFileItemReaderBuilder<Star2024Alt>()
    //         .name("fixedItemReader")
    //         .resource(new FileSystemResource(filePath))
    //         .lineTokenizer(tokenizer)
    //         .fieldSetMapper (mapper)
    //         .build();

    // }



    // this is working
   @Bean
    public FlatFileItemReader<Telpas2024> telpas2024Reader(String filePath) {

        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setNames(new String[]{"adminDate", "gradeLevel",  "studentId", 
            "listeningScore", "writingScore", "speakingScore", "readingScore", "compositeRating",            
            "throwaway"}); // Names for the fields
            tokenizer.setStrict(true);  // should be default.
            tokenizer.setColumns(new Range[]{
                    new Range(1, 4),    // adminDate
                    new Range(5,6),  // gradeLevel
                    new Range(145, 153), // studentID   ** MAY BE BLANK    CONFIRMD

                    new Range(309,312), // listeningScore;     // 309-312               CON
                    new Range(437,440),  // writingScore;       // 437-440          CON
                    new Range(507,510),  // speakingScore;      // 507-510      CON
                    new Range(709,712),  // readingScore;       //709-712       CON
                    new Range(908,908),   //  compositeRating;   // 908-908      CON

            
            
            
                    new Range(1199)   // throway.
            });


            // or
            //tokenizer.setNames("one", "two", "three", "four");
            //tokenizer.setColumns(new Range(1,5), new Range (6,10), new Range(11, 15), new Range(16,20));

            BeanWrapperFieldSetMapper<Telpas2024> mapper = new BeanWrapperFieldSetMapper<Telpas2024>();
            mapper.setTargetType(Telpas2024.class);



        return new FlatFileItemReaderBuilder<Telpas2024>()
            .name("fixedItemReader")
            .resource(new FileSystemResource(filePath))
            .lineTokenizer(tokenizer)
            .fieldSetMapper (mapper)
            .build();

    }


    // For 2023, the indexes are different, but the class is the same
   @Bean
    public FlatFileItemReader<Telpas2024> telpasAltReader(String filePath) {

        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setNames(new String[]{"adminDate", "gradeLevel",  "studentId", 
            "listeningScore", "writingScore", "speakingScore", "readingScore", "compositeRating",            
            "throwaway"}); // Names for the fields
            tokenizer.setStrict(true);  // should be default.
            tokenizer.setColumns(new Range[]{
                    new Range(1, 4),    // adminDate
                    new Range(5,6),  // gradeLevel
                    new Range(145, 153), // studentID   ** MAY BE BLANK    CONFIRMD

                    new Range(309,312), // listeningScore;     // 309-312               CON
                    new Range(507,510),  // speakingScore;      // 507-510      CON
                    new Range(709,712),  // readingScore;       //709-712       CON
                    new Range(759,762),  // private String writingScore;       // 437-440                  2 2023   *** NOT IN ALT   This is 759-762 for ALT    2023ALT same
                    new Range(908,908),   //  compositeRating;   // 908-908      CON

            
            
            
                    new Range(1199)   // throway.
            });


            // or
            //tokenizer.setNames("one", "two", "three", "four");
            //tokenizer.setColumns(new Range(1,5), new Range (6,10), new Range(11, 15), new Range(16,20));

            BeanWrapperFieldSetMapper<Telpas2024> mapper = new BeanWrapperFieldSetMapper<Telpas2024>();
            mapper.setTargetType(Telpas2024.class);



        return new FlatFileItemReaderBuilder<Telpas2024>()
            .name("fixedItemReader")
            .resource(new FileSystemResource(filePath))
            .lineTokenizer(tokenizer)
            .fieldSetMapper (mapper)
            .build();

    }



     @Bean
    public FlatFileItemReader<Star2024EOC> star2024EOCReader(String filePath) {

        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setNames(new String[]{"adminDate", "gradeLevel",  "studentId", "courseCode", "scoreCode", "pli",
            "throwaway"}); // Names for the fields
            tokenizer.setStrict(true);  // should be default.
            tokenizer.setColumns(new Range[]{
                    new Range(1, 4),    // adminDate
                    new Range(5,6),  // gradeLevel
                    new Range(123, 131), // studentID   ** MAY BE BLANK
                    new Range(201, 202), //courseCode
                    new Range(291,291), //scoreCode
                    new Range(363,364), // pli
                    new Range(1999)   // throway.
            });


            // or
            //tokenizer.setNames("one", "two", "three", "four");
            //tokenizer.setColumns(new Range(1,5), new Range (6,10), new Range(11, 15), new Range(16,20));

            BeanWrapperFieldSetMapper<Star2024EOC> mapper = new BeanWrapperFieldSetMapper<Star2024EOC>();
            mapper.setTargetType(Star2024EOC.class);



        return new FlatFileItemReaderBuilder<Star2024EOC>()
            .name("fixedItemReader")
            .resource(new FileSystemResource(filePath))
            .lineTokenizer(tokenizer)
            .fieldSetMapper (mapper)
            .build();

    }


   

    @Bean
    public FlatFileItemReader<Star2024> star2024Reader(String filePath) {

        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setNames(new String[]{"adminDate", "gradeLevel",  "studentId", "scoreReadingLanguageArts", "scoreMath", "scoreSocialStudies", "scoreScience", "pliReadingLanguageArts",  "pliMath", 
            
            "pliSocialStudies", "pliScience", "scoreAlgebra",
            
            "throwaway"}); // Names for the fields
            tokenizer.setStrict(true);  // should be default.
            tokenizer.setColumns(new Range[]{
                    new Range(1, 4),    // adminDate   C
                    new Range(5,6),  // gradeLevel      C
                    new Range(123, 131), // studentID   ** MAY BE BLANK         C
                    new Range(351, 351), //scoreReadingLanguageArts         C
                    new Range(352,352), //scoreMath                         C
                    new Range(354,354), // scoreSocialStudies               C
                    new Range(355,355), // scoreScience                 C
                    new Range(405,406),  // pliReadingLanguageArts      C (differnt)
                    new Range(759,760), // pliMath                         C (differnt)
                    new Range(1519, 1520), // pliSocialStudies          NO
                    new Range(1919,1920), // pliScience                 NO
                    new Range(3100,3100),  /// scoreAlgebra
                    new Range(3999)   // throway.                           C
            });


            // or
            //tokenizer.setNames("one", "two", "three", "four");
            //tokenizer.setColumns(new Range(1,5), new Range (6,10), new Range(11, 15), new Range(16,20));

            BeanWrapperFieldSetMapper<Star2024> mapper = new BeanWrapperFieldSetMapper<Star2024>();
            mapper.setTargetType(Star2024.class);



        return new FlatFileItemReaderBuilder<Star2024>()
            .name("fixedItemReader")
            .resource(new FileSystemResource(filePath))
            .lineTokenizer(tokenizer)
            .fieldSetMapper (mapper)
            .build();

    }

    

        // This is our model for reading files.
  

}
