package com.raffle.application;

import java.net.URISyntaxException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketDAO {
	
	@Autowired
	private DatabaseConfig dbConfig;
	
	private static final String GET_TICKETS_AND_PURCHASERS_SQL = "SELECT T.TicketNum, P.Name FROM Ticket T "
															   + "INNER JOIN Player P ON T.PlayerID = P.ID "
															   + "ORDER BY T.TicketNum";
	
	private static final String GET_SELECTED_COUNTS_SQL = "SELECT PlayerID, COUNT(*) AS Count FROM Ticket GROUP BY PlayerID";
	
	private static final String GET_TOTAL_SELECTED_COUNT_SQL = "SELECT COUNT(*) AS Count FROM Ticket";
	
	private static final String ADD_TICKET_SQL = "INSERT INTO Ticket VALUES (?, ?, ?)";
	
	private static final String GET_PLAYER_TICKET_COUNT_SQL = "SELECT COUNT(*) AS Count FROM Ticket WHERE PlayerID=?";
	
	private static final String GET_TICKET_IN_LIST_COUNT_SQL = "SELECT COUNT(*) AS Count FROM Ticket WHERE TicketNum IN ";
	
	
	public int getTicketsSelectedCount() {
		int ticketsSelectedCount = 0;	
		
		Connection con = null;
        try {
            con = dbConfig.getConnection();
            
            PreparedStatement ps = con.prepareStatement(GET_TOTAL_SELECTED_COUNT_SQL);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	ticketsSelectedCount = rs.getInt("Count");
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
        
        return ticketsSelectedCount;
	}
	
	public int getPlayerTicketsCount(int playerId) {
		int ticketsSelectedCount = 0;	
		
		Connection con = null;
        try {
            con = dbConfig.getConnection();
            
            PreparedStatement ps = con.prepareStatement(GET_PLAYER_TICKET_COUNT_SQL);
            
            ps.setInt(1, playerId);
            
            ResultSet rs = ps.executeQuery();            
            
            if (rs.next()) {
            	ticketsSelectedCount = rs.getInt("Count");
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
        
        return ticketsSelectedCount;
	}
	
	public boolean anyTicketsAlreadyPurchased(List<Integer> ticketsList) {
		int ticketsSelectedCount = 0;	
		
		Connection con = null;
        try {
            con = dbConfig.getConnection();
            
            String sql = GET_TICKET_IN_LIST_COUNT_SQL + "(";
            
            int count = 0;
            
            for (int ticketNum : ticketsList) {
            	if (count > 0) {
            		sql+=", ";
            	}
            	sql+=ticketNum;
            	
            	count+=1;
            }
            
            sql+=")";
            
            PreparedStatement ps = con.prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	ticketsSelectedCount = rs.getInt("Count");
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
        
        return ticketsSelectedCount > 0;
	}
													
	public Map<Integer, String> getTicketsAndPurchasersMap() {
		Map<Integer, String> ticketPurchaserMap = new HashMap<Integer, String>();		
		
		Connection con = null;
        try {
            con = dbConfig.getConnection();
            
            PreparedStatement ps = con.prepareStatement(GET_TICKETS_AND_PURCHASERS_SQL);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	ticketPurchaserMap.put(rs.getInt("TicketNum"), rs.getString("Name"));
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
        
        return ticketPurchaserMap;
	}
	
	public Map<Integer, Integer> getSelectedCountMap() {
		Map<Integer, Integer> selectedCountMap = new HashMap<Integer, Integer>();		
		
		Connection con = null;
        try {
            con = dbConfig.getConnection();
            
            PreparedStatement ps = con.prepareStatement(GET_SELECTED_COUNTS_SQL);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	selectedCountMap.put(rs.getInt("PlayerID"), rs.getInt("Count"));
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
        
        return selectedCountMap;
	}
	
	public boolean addPlayerTickets(int playerId, List<Integer> tickets) {
		int rowsAffected = 0;
		
		String currentDate = DatabaseConfig.getCurrentDateTimeString();
		
		Connection con = null;
        try {
        	
            con = dbConfig.getConnection();
            
            for (int ticketNum : tickets) {
                PreparedStatement ps = con.prepareStatement(ADD_TICKET_SQL);
                
                ps.setInt(1, playerId);
                ps.setInt(2, ticketNum);
                
                ps.setString(3, currentDate);
                
                rowsAffected += ps.executeUpdate();
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
        
        return rowsAffected == tickets.size();
	}

}
