package com.brand.practica_banco;

import ch.qos.logback.core.net.SyslogOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing	//Habilita el soporte para auditorias.
public class PracticaDeBancoApplication {

	//Crear un logger
	private static final Logger logger = LoggerFactory.getLogger(PracticaDeBancoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PracticaDeBancoApplication.class, args);
		logger.info("Hola mundo con logger");
		System.out.println("Hola mundo con println");

	}

}
