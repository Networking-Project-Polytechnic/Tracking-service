package com.example.tracking.travyotei.tracking_service_travyotei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TrackingServiceTravyoteiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrackingServiceTravyoteiApplication.class, args);
	}

}
