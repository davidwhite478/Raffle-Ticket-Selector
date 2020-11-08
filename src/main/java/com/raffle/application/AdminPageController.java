package com.raffle.application;
	
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminPageController {
	
	 @Autowired
	 private ErrorAttributes errorAttributes;

	 @Bean
	 public AppErrorController appErrorController(){return new AppErrorController(errorAttributes);}
	
	// inject via application.properties
	@Value("${applicationName}")
	private String applicationName = "";
	
	@Autowired
	private DatabaseConfig dbConfig;
	
	@Autowired
	private TicketDAO ticketDAO;
	
	@Autowired
	private PlayerDAO personDAO;
	
	@RequestMapping("/administration")
	public String admin(Map<String, Object> model, @RequestParam(value="error", required=false) String error, 
			@RequestParam(value="info", required=false) String info, @RequestParam(value="adminKey", required=false) String adminKey) {
		
		if (adminKey == null || !adminKey.equals(dbConfig.fetchConfigValue(DatabaseConfig.ADMIN_CODE_NAME))) {
			return "redirect:/";
		}
		
		List<Player> playerList = personDAO.getAllPlayers();
		
		Map<Integer,Integer> selectedCountMap = ticketDAO.getSelectedCountMap();
		
		int totalSelected = 0;
		
		for (Player player : playerList) {
			if (selectedCountMap.containsKey(player.getId())) {
				player.setSelectedTickets(selectedCountMap.get(player.getId()));
				
				totalSelected+=selectedCountMap.get(player.getId());
			}
		}
		
		int totalNumberOfTickets = Integer.parseInt(dbConfig.fetchConfigValue(dbConfig.TICKET_AMOUNT_NAME));
		
		int ticketsPurchased = personDAO.getTicketsPurchasedCount();
		
		model.put("playerList", playerList);
		model.put("totalSelected", totalSelected);
		model.put("totalTickets", totalNumberOfTickets);
		model.put("totalSold", ticketsPurchased);
		model.put("totalRemaining", totalNumberOfTickets - ticketsPurchased);
		
		model.put("adminKey", adminKey);
		
		model.put("error", error);
		model.put("info", info);
		
		RaffleApplication.addPageTitles(model, applicationName, "Admin");
		
		return "adminPage";
	}
	
	@RequestMapping("/administration/add-person")
	public String addPerson(Map<String, Object> model, @RequestParam(value="error", required=false) String error, 
			@RequestParam(value="info", required=false) String info, @RequestParam(value="adminKey", required=false) String adminKey) {
		
		if (adminKey == null || !adminKey.equals(dbConfig.fetchConfigValue(DatabaseConfig.ADMIN_CODE_NAME))) {
			return "redirect:/";
		}
		
		model.put("adminKey", adminKey);
		
		RaffleApplication.addPageTitles(model, applicationName, "Add Person");
		
		return "addPersonPage";
	}
	
	@RequestMapping(value="/administration/add-person-action", method = RequestMethod.POST)
	public String addPersonAction(Map<String, Object> model, @RequestParam(value="adminKey", required=false) String adminKey,
			@RequestParam(value="name", required=true) String name, @RequestParam(value="purchased", required=true) int purchased) {
		
		if (adminKey == null || !adminKey.equals(dbConfig.fetchConfigValue(DatabaseConfig.ADMIN_CODE_NAME))) {
			return "redirect:/";
		}
		
		boolean success = personDAO.addPlayer(name.strip(), purchased);

		String messageAppend = null;
		
		if (success) {
			messageAppend = "&info=Added "+name+" with "+purchased+" tickets.";
		} else {
			messageAppend = "&error=Failed to add new person!";
		}
		
		return "redirect:/administration?adminKey="+adminKey+messageAppend;
	}
	
}

