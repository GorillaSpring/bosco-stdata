package com.bosco.stdata;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class StdataApiController {

    @GetMapping("hello")
    public static String hello() {
        return "Hello There";
    }
    

}
