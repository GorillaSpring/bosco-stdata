package com.bosco.stdata.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
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

import com.bosco.stdata.teaModel.FixedTest;
import com.bosco.stdata.teaModel.Person;
import com.bosco.stdata.teaModel.Star2024;

//public class TeaStaarFlatFileReader implements ItemStream{

public class TeaStaarFlatFileReader {

    
    // Read this file:  SF_0524_3-8_043908_MELISSA ISD_V01
    // STAAR Grades 3–8 2024 Test Administration
    // https://tea.texas.gov/student-assessment/student-assessment-results/2024-staar-3-8-data-file.pdf



    // So we can just define the ones we need, the rest are fine.
    // we should define a throwaway for the rest of the line.

    @Bean
    public FlatFileItemReader<Star2024> star2024Reader(String filePath) {

        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setNames(new String[]{"adminDate", "gradeLevel", "districtName", "studentLast", "studentFirst", "studentId", "mathPercentile",  "mathQuantile", "throwaway"}); // Names for the fields
            tokenizer.setStrict(true);  // should be default.
            tokenizer.setColumns(new Range[]{
                    new Range(1, 4),    // adminDate
                    new Range(5, 6),   // gradeLevel
                    new Range(18, 32),  // distrctName
                    new Range(48,62),   // studentLast
                    new Range(63,72),  // studnetFirst
                    new Range(123, 131), // studentID   ** MAY BE BLANK
                    new Range(969, 971),  // mathPercentile
                    new Range(972, 976), // mathQuantile
                    new Range(3999)   // throway.
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
