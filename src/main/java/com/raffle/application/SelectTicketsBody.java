package com.raffle.application;

import java.util.List;

public class SelectTicketsBody {
	
	private List<Integer> tickets;
	private String code;
	
	public List<Integer> getTickets() {
		return tickets;
	}
	
	public void setTickets(List<Integer> tickets) {
		this.tickets = tickets;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

}
