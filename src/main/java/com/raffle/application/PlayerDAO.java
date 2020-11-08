package com.raffle.application;

import java.net.URISyntaxException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerDAO {
	
	@Autowired
	private DatabaseConfig dbConfig;
	
	private static final String GET_PLAYERS_SQL = "SELECT P.ID, P.Name, P.PurchasedTickets, P.Code FROM Player P "
															   + "ORDER BY P.Name";
	
	private static final String GET_TICKET_PURCHASED_COUNT_SQL = "SELECT SUM(PurchasedTickets) AS X FROM Player";
	
	private static final String ADD_NEW_PLAYER_SQL = "INSERT INTO Player VALUES (?, ?, ?, ?, ?)";
	
	private static final String GET_PLAYER_BY_CODE = "SELECT P.ID, P.Name, P.PurchasedTickets, P.Code FROM Player P WHERE P.Code=?";
	
	private static final int MIN_ID = 1000000; 	
	private static final int MAX_ID = 10000000;
	
	public int getTicketsPurchasedCount() {
		int ticketsPurchasedCount = 0;	
		
		Connection con = null;
        try {
            con = dbConfig.getConnection();
            
            PreparedStatement ps = con.prepareStatement(GET_TICKET_PURCHASED_COUNT_SQL);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	ticketsPurchasedCount = rs.getInt("X");
            }
            
        } catch (SQLException|URISyntaxException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        return ticketsPurchasedCount;
	}
	
	public List<Player> getAllPlayers() {
		List<Player> playerList = new ArrayList<Player>();	
		
		Connection con = null;
        try {
            con = dbConfig.getConnection();
            
            PreparedStatement ps = con.prepareStatement(GET_PLAYERS_SQL);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	Player newPlayer = new Player();
            	newPlayer.setId(rs.getInt("ID"));
            	newPlayer.setName(rs.getString("Name"));
            	newPlayer.setPurchasedTickets(rs.getInt("PurchasedTickets"));
            	newPlayer.setCode(rs.getString("Code"));
            	
            	playerList.add(newPlayer);
            }
            
        } catch (SQLException|URISyntaxException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        return playerList;
	}
	
	public Player getPlayerByCode(String code) {
		Player player = null;	
		
		Connection con = null;
        try {
            con = dbConfig.getConnection();
            
            PreparedStatement ps = con.prepareStatement(GET_PLAYER_BY_CODE);
            
            ps.setString(1, code);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	player = new Player();
            	player.setId(rs.getInt("ID"));
            	player.setName(rs.getString("Name"));
            	player.setPurchasedTickets(rs.getInt("PurchasedTickets"));
            	player.setCode(rs.getString("Code"));
            }
            
        } catch (SQLException|URISyntaxException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        return player;
	}
	
	private int getNewID() {
		Random randomGen = new Random(new Date().getTime());
		return MIN_ID + randomGen.nextInt(MAX_ID - MIN_ID);
	}
	
	private String getCode() {
		return UUID.randomUUID().toString();
	}
	
	public boolean addPlayer(String name, int purchasedTickets) {
		int rowsAffected = 0;
		
		Connection con = null;
        try {
        	
            con = dbConfig.getConnection();
            
            PreparedStatement ps = con.prepareStatement(ADD_NEW_PLAYER_SQL);
            
            ps.setInt(1, getNewID());
            ps.setString(2, name);
            ps.setString(3, getCode());
            ps.setInt(4, purchasedTickets);            
            ps.setString(5, DatabaseConfig.getCurrentDateTimeString());
            
            rowsAffected = ps.executeUpdate();
            
        } catch (SQLException|URISyntaxException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        return rowsAffected > 0;
	}

}
