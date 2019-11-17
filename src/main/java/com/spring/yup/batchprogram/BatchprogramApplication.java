package com.spring.yup.batchprogram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class BatchprogramApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchprogramApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(){
		return "hello kyoujin's batch program";
	}

}
