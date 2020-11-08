package com.raffle.application;

public class Player {
	
	private String Name;
	private int id;
	private int purchasedTickets;
	private int selectedTickets = 0;
	private String code;
	
	public Player() {
		
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPurchasedTickets() {
		return purchasedTickets;
	}

	public void setPurchasedTickets(int purchasedTickets) {
		this.purchasedTickets = purchasedTickets;
	}

	public int getSelectedTickets() {
		return selectedTickets;
	}

	public void setSelectedTickets(int selectedTickets) {
		this.selectedTickets = selectedTickets;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
