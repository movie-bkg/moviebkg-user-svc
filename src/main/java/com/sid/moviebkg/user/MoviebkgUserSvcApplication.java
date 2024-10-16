package com.sid.moviebkg.user;

import org.apache.camel.observation.starter.CamelObservation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.sid.moviebkg.*","com.sid.moviebkg.user.*","com.sid.moviebkg.common.*"})
@EnableJpaRepositories(basePackages = {"com.sid.moviebkg.*"})
@EntityScan(basePackages = {"com.sid.moviebkg.*"})
@CamelObservation
public class MoviebkgUserSvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoviebkgUserSvcApplication.class, args);
	}

}
