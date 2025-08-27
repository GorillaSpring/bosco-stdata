package com.bosco.stdata.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;


public class UserFileService {


    public List<String[]> readCsvFile(String filePath) {
        List<String[]> records = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(
            new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))
        //new FileReader(filePath)
        
        )) {
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                records.add(nextRecord);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
        return records;
    }


        // Example of how to use it in a Spring Boot component
    // @Service
    
    // public class MySpringService {
    //     public void processCsvData() {
    //         List<String[]> data = readCsvFile("C:/test/user.csv");
    //         // Process the data
    //     }
    // }

    // public int importTeachers() throws Exception {
    //     File inputFile = new File("C:/test/user.csv");
    //     InputStreamReader input = new InputStreamReader(*inputFile.g)
    //     List<TeacherCsvRow> rows = new CsvToBeanBuilder<>(null)
    // }

}
