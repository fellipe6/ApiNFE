package br.com.nazasoftapinfe;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ApiNfeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiNfeApplication.class, args);
	}

}