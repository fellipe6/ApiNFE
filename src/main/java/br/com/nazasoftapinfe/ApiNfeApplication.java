package br.com.nazasoftapinfe;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;


@SpringBootApplication
@EnableScheduling
public class ApiNfeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiNfeApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// Define o fuso horário padrão
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		System.out.println("Fuso horário configurado para: " + TimeZone.getDefault().getID());
	}
}