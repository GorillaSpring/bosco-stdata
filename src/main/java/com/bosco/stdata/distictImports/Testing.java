package com.bosco.stdata.distictImports;

import java.util.List;

//import org.springframework.batch.core.repository.persistence.ExecutionContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.bosco.stdata.model.ImportDefinition;
import com.bosco.stdata.model.ImportResult;
import com.bosco.stdata.model.ImportSetting;
import com.bosco.stdata.repo.ImportRepo;
import com.bosco.stdata.service.BoscoApi;
import com.bosco.stdata.teaModel.FixedTest;
import com.bosco.stdata.teaModel.Person;
import com.bosco.stdata.teaModel.Star2024;
import com.bosco.stdata.utils.TeaStaarFlatFileReader;

import jakarta.annotation.PostConstruct;

@Component
public class Testing {

    // this is just for testing while we are doing dev.
    // not a real import

     @Autowired
    ImportRepo importRepo;

    @Autowired 
    BoscoApi boscoApi;
    

    private static Testing i;  // instance

    @PostConstruct
    public void init() {
        System.out.println("Testing - init()");
        i = this;
    }

    public static void Test(String importDefId) {
        System.out.println("Test Starting");

        // try to create a file reader


     

        TeaStaarFlatFileReader tsfr = new TeaStaarFlatFileReader();

        //FlatFileItemReader<Star2024> s24 = tsfr.star2024Reader("C:/test/importBase/tea/SF_0524_3-8_043908_MELISSA ISD_V01.txt");
        FlatFileItemReader<Star2024> s24 = tsfr.star2024Reader("C:/test/importBase/tea/SF_0525_3_8_043903_CELINA_ISD_V03.txt");

        s24.open(new ExecutionContext());

        System.out.println(("-----------------------"));
        System.out.println(("------ STAR 2024 ------"));

        int count = 0;

        try {
            Star2024 t = s24.read();

            while (t != null) {

                count++;
                System.out.println(t);

                t = s24.read();
            }
            

            System.out.println("Done");
        }
        catch (Exception ex) {
            System.out.println("EXCEPTION : " + ex.getMessage());
            System.out.println(ex.getStackTrace());
        };


        System.out.println(" -- Read " + count + "  Students");

        System.out.println(("-----------------------"));


        // tsfr.TestTokenizerFT();


        // // OK, this is working so we can use it as a model.
        // FlatFileItemReader<FixedTest> ft = tsfr.ftReader();


        // //FlatFileItemReader<FixedTest> ft = tsfr.fixedLengthItemReader();

        // ft.open(new ExecutionContext());

        // try {
        //     FixedTest t = ft.read();

        //     while (t != null) {
        //         System.out.println(" READ FixedTest : " + t);

        //         t = ft.read();
        //     }
            

        //     System.out.println("Done");
        // }
        // catch (Exception ex) {
        //     System.out.println("EXCEPTION : " + ex.getMessage());
        //     System.out.println(ex.getStackTrace());
        // }

        // FlatFileItemReader<Person> ps = tsfr.personItemReader();

        // ps.open(new ExecutionContext());

        // int count = 0;

        // try {
        //     //ps.open(new ExecutionContext());
        //     Person p = ps.read();

        //     while (p != null) {
        //         System.out.println(" READ Person : " + p.getFirstName());

        //         p = ps.read();
        //     }
            
            

            


        //     System.out.println("Test Ending");

        // }
        // catch (Exception ex) {
        //     System.out.println("EXCEPTION : " + ex.getMessage());
        //     System.out.println(ex.getStackTrace());
        // }

    }


    public static ImportResult Import(String importDefId) {
        System.out.println("TESTING HERE");

        ImportResult result = new ImportResult();

        result.success = true;

        return result;
    }

}
