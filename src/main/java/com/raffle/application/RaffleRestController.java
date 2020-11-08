package com.raffle.application;

import org.springframework.web.bind.annotation.RestController;

import java.lang.System;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RaffleRestController {
	
	@Autowired
	private DatabaseConfig dbConfig;
	
	@Autowired
	private TicketDAO ticketDAO;
	
	@Autowired
	private PlayerDAO playerDAO;
	
	@RequestMapping(value = "/select-tickets-action", method = RequestMethod.POST, headers = {"content-type=application/json"})
	@ResponseBody
	public int selectTicketAction(Map<String, Object> model,
			@RequestBody SelectTicketsBody selectTicketsBody) {
		
		Player player = playerDAO.getPlayerByCode(selectTicketsBody.getCode());
		
		if (player == null) {
			return -1;
		}
		
		int alreadySelectedTickets = ticketDAO.getPlayerTicketsCount(player.getId());
		
		int remainingTickets = player.getPurchasedTickets() - alreadySelectedTickets;
		
		if (selectTicketsBody.getTickets().size() > remainingTickets || selectTicketsBody.getTickets().isEmpty()) {
			// If selected too many tickets (or no tickets) - return 0
			return 0;
		}
		
		if (ticketDAO.anyTicketsAlreadyPurchased(selectTicketsBody.getTickets())) {
			// If any tickets already purchased, return 0
			return 0;
		}
		
		boolean successfulUpdate = ticketDAO.addPlayerTickets(player.getId(), selectTicketsBody.getTickets());
		
		if (successfulUpdate) {
			return 1;
		} else {
			return 0;
		}

	}
}