package com.sid.moviebkg.user;

import org.apache.camel.observation.starter.CamelObservation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.sid.moviebkg.*","com.sid.moviebkg.user.*","com.sid.moviebkg.common.*"})
@CamelObservation
public class MoviebkgUserSvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoviebkgUserSvcApplication.class, args);
	}

}
