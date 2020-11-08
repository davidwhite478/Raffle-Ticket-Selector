package com.raffle.application;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RaffleApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RaffleApplication.class, args);
	}
	
	public static void addPageTitles(Map<String, Object> model, String applicationName, String pageName) {		
		model.put("pageTitle", pageName);
		model.put("appTitle", applicationName);
	}
}
