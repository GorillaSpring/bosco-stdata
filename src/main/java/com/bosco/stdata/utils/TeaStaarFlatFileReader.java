package com.bosco.stdata.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.context.annotation.Bean;

import com.bosco.stdata.teaModel.CelinaCombo;
import com.bosco.stdata.teaModel.FixedTest;
import com.bosco.stdata.teaModel.Person;
import com.bosco.stdata.teaModel.Star2024;
import com.bosco.stdata.teaModel.Star2024Alt;
import com.bosco.stdata.teaModel.Star2024EOC;
import com.bosco.stdata.teaModel.Telpas2024;

//public class TeaStaarFlatFileReader implements ItemStream{

public class TeaStaarFlatFileReader {

    
  // This is the same as uplift (move to generic file.)
    public static String SchoolYearFromDate (String dateString) throws Exception {
        
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
         //try {
            LocalDate date = LocalDate.parse(dateString, formatter);
            Month month = date.getMonth();
            if (month == Month.AUGUST || month == Month.SEPTEMBER || month == Month.OCTOBER || month == Month.NOVEMBER || month == Month.DECEMBER)
                return date.getYear() + "-" + (date.getYear() + 1);
            else
                return date.getYear() - 1 + "-" + date.getYear();
        // } catch (DateTimeParseException e) {
        //     System.err.println("Error parsing date: " + e.getMessage());
        //     return "";
        // }
    }


    // Read this file:  SF_0524_3-8_043908_MELISSA ISD_V01
    // STAAR Grades 3–8 2024 Test Administration
    // https://tea.texas.gov/student-assessment/student-assessment-results/2024-staar-3-8-data-file.pdf



    // So we can just define the ones we need, the rest are fine.
    // we should define a throwaway for the rest of the line.'
    
    
// So lets try fror the csv.


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

    @Bean
    public FlatFileItemReader<Star2024Alt> star2024AltReader(String filePath) {

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

            BeanWrapperFieldSetMapper<Star2024Alt> mapper = new BeanWrapperFieldSetMapper<Star2024Alt>();
            mapper.setTargetType(Star2024Alt.class);



        return new FlatFileItemReaderBuilder<Star2024Alt>()
            .name("fixedItemReader")
            .resource(new FileSystemResource(filePath))
            .lineTokenizer(tokenizer)
            .fieldSetMapper (mapper)
            .build();

    }



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
    @Bean
    public FlatFileItemReader<FixedTest> ftReader() {

        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setNames(new String[]{"one", "two", "three", "four"}); // Names for the fields
            tokenizer.setStrict(true);  // should be default.
            tokenizer.setColumns(new Range[]{
                    new Range(1, 5),    // ID occupies characters 1-5 (inclusive)
                    new Range(6, 7),   // First Name occupies characters 6-15
                    new Range(11, 15),  // Last Name occupies characters 16-25
                    new Range(16)   // Birth Year occupies characters 26-29
            });


            // or
            //tokenizer.setNames("one", "two", "three", "four");
            //tokenizer.setColumns(new Range(1,5), new Range (6,10), new Range(11, 15), new Range(16,20));

            BeanWrapperFieldSetMapper<FixedTest> mapper = new BeanWrapperFieldSetMapper<FixedTest>();
            mapper.setTargetType(FixedTest.class);



        return new FlatFileItemReaderBuilder<FixedTest>()
            .name("fixedItemReader")
            .resource(new FileSystemResource("c:/test/ff/fixed.tst"))
            .lineTokenizer(tokenizer)
            .fieldSetMapper (mapper)
            .build();

    }


    // below can be removed.

    @Bean
    public FlatFileItemReader<Person> personItemReader() {
        return new FlatFileItemReaderBuilder<Person>()
            .name("personItemReader")
            .resource(new FileSystemResource("c:/test/ff/persons.csv")) // Path to your CSV file  Try FileSystemResource instead
            .linesToSkip(1) // Skip header line if present
            .delimited()
            .names(new String[]{"firstName", "lastName"}) // Map CSV columns to Person fields
            .targetType(Person.class) // Specify the target object type
            .build();
    }

    @Bean
    public ItemWriter<Person> itemWriter() {
        return items -> items.forEach(System.out::println);
    }


    public void TestTokenizer () {
          FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();

        // 2. Define the column ranges
        // Example:
        // - First 5 characters for 'ID'
        // - Next 10 characters for 'Name'
        // - Remaining characters for 'Description'
        Range[] ranges = new Range[] {
            new Range(1, 5),    // Column 1: ID (characters 1-5)
            new Range(6, 15),   // Column 2: Name (characters 6-15)
            new Range(16)       // Column 3: Description (from character 16 to end of line)
        };
        tokenizer.setColumns(ranges);

        // 3. (Optional) Set names for the fields for better readability
        tokenizer.setNames(new String[] {"ID", "Name", "Description"});

        // 4. (Optional) Configure strict mode (default is true)
        // If strict is true, lines must exactly match the defined length.
        // If false, shorter lines are padded, longer lines are truncated.
        tokenizer.setStrict(true);

        // 5. Tokenize a sample line
        String line = "12345John Doe   This is a description.";
        try {
            FieldSet fieldSet = tokenizer.tokenize(line);

            // 6. Access the tokenized data
            System.out.println("ID: " + fieldSet.readString("ID"));
            System.out.println("Name: " + fieldSet.readString("Name"));
            System.out.println("Description: " + fieldSet.readString("Description"));

            // You can also access by index
            System.out.println("ID (by index): " + fieldSet.readString(0));

        } catch (Exception e) {
            System.err.println("Error tokenizing line: " + e.getMessage());
        }
    
    }

    public void TestTokenizerFT () {


        // If we put a max range on the last one, it MUST be the exact number of chars in each line.
        // if we leave it, it will go tot he last one.

            FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setNames(new String[]{"one", "two", "three", "four"}); // Names for the fields
            tokenizer.setStrict(true);  // should be default.
            tokenizer.setColumns(new Range[]{
                    new Range(1, 5),    // ID occupies characters 1-5 (inclusive)
                    new Range(6, 10),   // First Name occupies characters 6-15
                    new Range(11, 15),  // Last Name occupies characters 16-25
                    new Range(16)   // Birth Year occupies characters 26-29
            });


        // 5. Tokenize a sample line
        String line = "11111222223333344444aa";
        try {
            FieldSet fieldSet = tokenizer.tokenize(line);

            // 6. Access the tokenized data
            System.out.println("one: " + fieldSet.readString("one"));
            System.out.println("two: " + fieldSet.readString("two"));
            System.out.println("three: " + fieldSet.readString("three"));
            System.out.println("four: " + fieldSet.readString("four"));

            // You can also access by index
            System.out.println("ID (by index): " + fieldSet.readString(0));

        } catch (Exception e) {
            System.err.println("Error tokenizing line: " + e.getMessage());
        }
    
    }





    @Bean
    public FlatFileItemReader<FixedTest> fixedLengthItemReader() {
        FlatFileItemReader<FixedTest> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("c:/test/ff/fixed.tst")); // Your input file
        reader.setEncoding("UTF-8");  // should be the default.

        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        tokenizer.setNames(new String[]{"one", "two", "three", "four"}); // Names for the fields
        tokenizer.setStrict(true);  // should be default.
        tokenizer.setColumns(new Range[]{
                new Range(1, 5),    // ID occupies characters 1-5 (inclusive)
                new Range(6, 10),   // First Name occupies characters 6-15
                new Range(11, 15),  // Last Name occupies characters 16-25
                new Range(16, 20)   // Birth Year occupies characters 26-29
        });


        DefaultLineMapper<FixedTest> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);

        reader.setLineMapper(lineMapper);
        //reader.setLineTokenizer(tokenizer);
        // You would typically also set a FieldSetMapper here to map the tokenized data to your 'YourObject'
        // reader.setFieldSetMapper(new BeanWrapperFieldSetMapper<YourObject>() {{ setTargetType(YourObject.class); }});

        return reader;
    }



    // @Bean
    // public Job job (JobBuilderFactory jobs, SteButilderFactory steps) {
    //     return jobs.get("job");
    // }

}
