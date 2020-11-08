package com.raffle.application;

public class Ticket {
	
	private int ticketNumber;
	
	private String purchaser = null;
	
	public Ticket(int ticketNumber) {
		this.setTicketNumber(ticketNumber);
	}

	public int getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber(int ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	public String getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}

}
