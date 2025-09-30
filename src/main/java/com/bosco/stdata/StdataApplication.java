package com.bosco.stdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.bosco.stdata.repo.ImportRepo;

@SpringBootApplication
@EnableScheduling
@ComponentScan ("com.bosco")
public class StdataApplication {

	  
    @Value("${bosco.api.instance}")
    private String boscoInstance;

	@Autowired
	ImportRepo importRepo;

	public static void main(String[] args) {
		SpringApplication.run(StdataApplication.class, args);
	}

	
	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		System.out.println("hello world, I have just started up - " + boscoInstance);
		//importRepo.importSystemStartup();
	}


}
