// package com.bosco.stdata.controllers;

// import org.springframework.web.bind.annotation.RestController;

// import com.bosco.stdata.repo.ImportRepo;
// import com.bosco.stdata.tasks.ImportTask;

// import io.swagger.v3.oas.annotations.Operation;

// import java.util.ArrayList;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;

// import com.bosco.stdata.model.*; 


// @RestController
// @RequestMapping("/import/api/testing")
// public class StudentApi {
//     @Autowired
//     ImportRepo importRepo;

//     @Autowired 
//     ImportTask importTask;


    

//     // @Operation(
//     //     summary = "Register a student for SIS data",
//     //     description = "SIS data for this student will be sent SOON.  If any data changes during imports, it will be sent again",
//     //     tags = {"Bosco Endpoints"}
//     //     )


//     // @GetMapping("/student/studentDataRegister/{id}")
//     // public String studentDataRegister(@PathVariable String id) {


//     //     System.out.println("Param: " + id);
//     //     // id will be 66.838101615
//     //     String [] params = id.split("\\.");

//     //     //var x = params[0];

//     //     System.out.println("District: " + params[0] + "  - Student : " + params[1]);

//     //     int districId = Integer.parseInt(params[0]);


//     //     return "Registered";
//     // }
    
//     // @Operation(
//     //     summary = "UnRegister a student for SIS data",
//     //     description = "When data is no longer need for this student",
//     //     tags = {"Bosco Endpoints"}
//     //     )

//     // @GetMapping("/student/studentDataUnRegister/{id}")
//     // public String studentDataUnRegister(@PathVariable String id) {


//     //     System.out.println("Param: " + id);
//     //     // id will be 66.838101615
//     //     String [] params = id.split("\\.");

//     //     //var x = params[0];

//     //     System.out.println("District: " + params[0] + "  - Student : " + params[1]);

//     //     int districId = Integer.parseInt(params[0]);


//     //     return "Unregistered";
//     // }
    


    

// }
