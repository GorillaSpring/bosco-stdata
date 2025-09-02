package com.bosco.stdata.service;

import com.bosco.stdata.model.Student;
import com.bosco.stdata.model.Teacher;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;


@Service
public class BoscoClient {
    private final RestTemplate restTemplate;

    public BoscoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public String getAccessToken(String clientId, String clientSecret, String tokenUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String auth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        headers.set("Authorization", "Basic " + auth);

        HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);

        ResponseEntity<Map> response =
            restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);
        return (String) response.getBody().get("access_token");
    }

    public JsonNode get(String url, String token) throws Exception {

        HttpHeaders headers = new HttpHeaders();

        
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        

        ResponseEntity<JsonNode> response =
            restTemplate.exchange(url, HttpMethod.GET, request, JsonNode.class);

        //System.err.println(response.getBody());

        JsonNode responseBody = response.getBody();

        return responseBody;




        // if (responseBody == null || !responseBody.fields().hasNext()) {
        // throw new IllegalStateException(
        //     "Empty or invalid response from Skyward API for: " + "students");
        // }

        // Map.Entry<String, JsonNode> entry = responseBody.fields().next();
        // return entry.getValue();
    }

    public String postStudent (String url, String token, Student student) throws Exception {
          HttpHeaders headers = new HttpHeaders();


          // This will get the same student back.
        
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

           // 3. Create an HttpEntity combining the request body and headers
        HttpEntity<Student> requestEntity = new HttpEntity<>(student, headers);


  // 4. Make the POST request using postForEntity()
        //    - First argument: URL
        //    - Second argument: HttpEntity containing the request body and headers
        //    - Third argument: Class representing the expected response body type
        //ResponseEntity<Person> responseEntity = restTemplate.postForEntity(url, requestEntity, Person.class);

         ResponseEntity<Student> responseEntity = restTemplate.postForEntity(url, requestEntity, Student.class);
    


        //HttpEntity<Void> request = new HttpEntity<>(headers);

        //ResponseEntity<Student> responseEntity = restTemplate.postForEntity(url, student, Student.class);
//        restTemplate.postForObject(url, request, null)
         if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            // System.out.println("Person created successfully!");
            // Student createdStudent = responseEntity.getBody();
            // System.out.println("Created Student: " + createdStudent.getFirstName());
            // System.out.println("Response Headers: " + responseEntity.getHeaders());
        } else {
            //System.out.println("Failed to create person. Status code: " + responseEntity.getStatusCode());
            throw new Exception("Failed to create Student. Status code: " + responseEntity.getStatusCode());
        }

        return "OK";
    }


    public String putStudent (String url, String token, Student student) throws Exception {
          HttpHeaders headers = new HttpHeaders();


          // This will get the same student back.
        
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

           // 3. Create an HttpEntity combining the request body and headers

        HttpEntity<Student> requestEntity = new HttpEntity<>(student, headers);

        
        String studentId = student.getId();

        // Perform the PUT request using exchange()
        // The third argument is the request entity, and the fourth is the expected response type
        ResponseEntity<Student> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.PUT,
            requestEntity,
            Student.class, // The class representing the expected response body
            student.getId() // Example URI variable for the {id} placeholder
        );

         if (responseEntity.getStatusCode().is2xxSuccessful()) {
            // System.out.println("Student updated  successfully!");
            // Student updatedStudent = responseEntity.getBody();
            // System.out.println("Updated Student: " + updatedStudent.getFirstName());
            // System.out.println("Response Headers: " + responseEntity.getHeaders());
        } else {
            //System.out.println("Failed to update student. Status code: " + responseEntity.getStatusCode());
            throw new Exception("Failed to Update Student. Status code: " + responseEntity.getStatusCode());
        }

        return "OK";
    }


     public String deleteStudent (String url, String token, String studentId) throws Exception {
        HttpHeaders headers = new HttpHeaders();

        // this will need to be worked on.


          // This will get the same student back.`
        
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

           // 3. Create an HttpEntity combining the request body and headers

        //HttpEntity<Student> requestEntity = new HttpEntity<>(student, headers);
        HttpEntity<Void> request = new HttpEntity<>(headers);


           // Use the exchange method for DELETE with headers
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            request,
            String.class,
            studentId // URI variable for the ID
        );

        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            // System.out.println("Student deleted successfully!");
            // String result = responseEntity.getBody();
            // System.out.println("Student Deleted response: " + result);
            // System.out.println("Response Headers: " + responseEntity.getHeaders());
        } else {
            //stem.out.println("Failed to Delete Student. Status code: " + responseEntity.getStatusCode());
            throw new Exception("Failed to DELETE Student. Status code: " + responseEntity.getStatusCode());
        }

        return "OK";

    }

    public String postTeacher (String url, String token, Teacher teacher) throws Exception {
          
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

           // 3. Create an HttpEntity combining the request body and headers
        HttpEntity<Teacher> requestEntity = new HttpEntity<>(teacher, headers);


  // 4. Make the POST request using postForEntity()
        //    - First argument: URL
        //    - Second argument: HttpEntity containing the request body and headers
        //    - Third argument: Class representing the expected response body type
        //ResponseEntity<Person> responseEntity = restTemplate.postForEntity(url, requestEntity, Person.class);

         ResponseEntity<Teacher> responseEntity = restTemplate.postForEntity(url, requestEntity, Teacher.class);
    


        //HttpEntity<Void> request = new HttpEntity<>(headers);

        //ResponseEntity<Student> responseEntity = restTemplate.postForEntity(url, student, Student.class);
//        restTemplate.postForObject(url, request, null)
         if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            // System.out.println("Teacher created successfully!");
            // Teacher teacherCreated = responseEntity.getBody();
            // System.out.println("Created Teacher: " + teacherCreated.getFirstName());
            // System.out.println("Response Headers: " + responseEntity.getHeaders());
        } else {
            //System.out.println("Failed to create Teacher. Status code: " + responseEntity.getStatusCode());
            throw new Exception("Failed to create Teacher. Status code: " + responseEntity.getStatusCode());
            
        }

        return "OK";
    }

    public String putTeacher (String url, String token, Teacher teacher) throws Exception {
          HttpHeaders headers = new HttpHeaders();


          // This will get the same student back.
        
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

           // 3. Create an HttpEntity combining the request body and headers

        HttpEntity<Teacher> requestEntity = new HttpEntity<>(teacher, headers);

        

        // Perform the PUT request using exchange()
        // The third argument is the request entity, and the fourth is the expected response type
        ResponseEntity<Teacher> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.PUT,
            requestEntity,
            Teacher.class, // The class representing the expected response body
            teacher.getId() // Example URI variable for the {id} placeholder
        );

         if (responseEntity.getStatusCode().is2xxSuccessful()) {
            // System.out.println("Teacher updated  successfully!");
            // Teacher updatedTeacher = responseEntity.getBody();
            // System.out.println("Updated Teacher: " + updatedTeacher.getFirstName());
            // System.out.println("Response Headers: " + responseEntity.getHeaders());
        } else {
            //System.out.println("Failed to update teacher. Status code: " + responseEntity.getStatusCode());
            throw new Exception("Failed to update Teacher. Status code: " + responseEntity.getStatusCode());
        }

        return "OK";
    }

    public String deleteTeacher (String url, String token, String teacherId) throws Exception {
        HttpHeaders headers = new HttpHeaders();

        // this will need to be worked on.


          // This will get the same student back.
        
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

           // 3. Create an HttpEntity combining the request body and headers

        //HttpEntity<Student> requestEntity = new HttpEntity<>(student, headers);
        HttpEntity<Void> request = new HttpEntity<>(headers);


           // Use the exchange method for DELETE with headers
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            request,
            String.class,
            teacherId // URI variable for the ID
        );

        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            // System.out.println("Teacher deleted successfully!");
            // String result = responseEntity.getBody();
            // System.out.println("Teacher Deleted response: " + result);
            // System.out.println("Response Headers: " + responseEntity.getHeaders());
        } else {
            throw new Exception("Failed to DELETE Teacher. Status code: " + responseEntity.getStatusCode());
            //System.out.println("Failed to DELETE Teacher. Status code: " + responseEntity.getStatusCode());
        }

        return "OK";

    }

}
