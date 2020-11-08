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
public class GeneralPageController {
	
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
	private PlayerDAO playerDAO;

	@RequestMapping("/")
	public String home(Map<String, Object> model, @RequestParam(value="error", required=false) String error, 
			@RequestParam(value="info", required=false) String info) {
		
		int totalNumberOfTickets = Integer.parseInt(dbConfig.fetchConfigValue(dbConfig.TICKET_AMOUNT_NAME));
		
		int ticketsPurchased = playerDAO.getTicketsPurchasedCount();
		
		List<Ticket> tickets = new ArrayList<Ticket>();
		
		Map<String, ArrayList<Integer>> playerMap = new HashMap();
		
		Map<Integer, String> ticketPurchaserMap = ticketDAO.getTicketsAndPurchasersMap();
		
		for (int i = 1; i <= totalNumberOfTickets; i++) {
			Ticket ticket = new Ticket(i);
			
			if (ticketPurchaserMap.containsKey(i)) {
				String playerName = ticketPurchaserMap.get(i);
				ticket.setPurchaser(playerName);
				
				if (!playerMap.containsKey(playerName)) {
					playerMap.put(playerName, new ArrayList<Integer>());
				}
				
				playerMap.get(playerName).add(i);
			}
			
			tickets.add(ticket);
		}
		
		ArrayList<String> sortedPlayers = new ArrayList<String>();
		
		for (String player : new TreeSet<String>(playerMap.keySet())) {
			sortedPlayers.add(player);
		}
		
		model.put("tickets", tickets);
		
		model.put("totalSelected", ticketDAO.getTicketsSelectedCount());
		model.put("totalTickets", totalNumberOfTickets);
		model.put("totalSold", ticketsPurchased);
		model.put("totalRemaining", totalNumberOfTickets - ticketsPurchased);
		
		model.put("sortedPlayers", sortedPlayers);
		model.put("playerTicketMap", playerMap);
		
		model.put("info", info);
		
		RaffleApplication.addPageTitles(model, applicationName, "Home");
		
		return "homePage";
	}
	
	@RequestMapping("/select-tickets")
	public String selectTickets(Map<String, Object> model, @RequestParam(value="error", required=false) String error, 
			@RequestParam(value="info", required=false) String info, @RequestParam(value="code", required=true) String code) {
		
		Player player = playerDAO.getPlayerByCode(code);
		
		Map<Integer,Integer> selectedCountMap = ticketDAO.getSelectedCountMap();
		
		if (selectedCountMap.containsKey(player.getId())) {
			player.setSelectedTickets(selectedCountMap.get(player.getId()));
		}
		
		if (player == null || player.getPurchasedTickets() - player.getSelectedTickets() <= 0) {
			return "redirect:/";
		}
		
		int totalNumberOfTickets = Integer.parseInt(dbConfig.fetchConfigValue(dbConfig.TICKET_AMOUNT_NAME));
		Map<Integer, String> ticketPurchaserMap = ticketDAO.getTicketsAndPurchasersMap();
		List<Ticket> tickets = new ArrayList<Ticket>();
		
		for (int i = 1; i <= totalNumberOfTickets; i++) {
			Ticket ticket = new Ticket(i);
			
			if (ticketPurchaserMap.containsKey(i)) {
				String playerName = ticketPurchaserMap.get(i);
				ticket.setPurchaser(playerName);
			}
			
			tickets.add(ticket);
		}
		
		model.put("tickets", tickets);
		
		model.put("player", player);
		
		model.put("buyableTickets", player.getPurchasedTickets() - player.getSelectedTickets());
		
		model.put("error", error);
		
		RaffleApplication.addPageTitles(model, applicationName, "Select Tickets");
		
		return "selectTicketsPage";
	}

}

