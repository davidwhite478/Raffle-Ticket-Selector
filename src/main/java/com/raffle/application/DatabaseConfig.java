package com.raffle.application;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfig {
	
	public String lastUpdated;
	
	public Integer currentStage;
	
	public static final String getConfigSql = "SELECT * FROM Config WHERE Key=?";
	
	public static final String setConfigSql = "UPDATE Config SET Value=? WHERE Key=?";
	
	public static final String GET_PLAYER_BY_CODE_SQL = "SELECT Name, ID FROM Player WHERE Code=?";
	
	public static final String GET_PLAYER_BY_ID_SQL = "SELECT Name FROM Player WHERE ID=?";
	
	public static final String ADMIN_CODE_NAME = "AdminCode";
	public static final String TICKET_AMOUNT_NAME = "TicketAmount";
	
	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	private static final boolean localSQLite = false;
	
	@Value("${sqliteURL}")
	public String SQLiteURL = "";
	
	public static String getCurrentDateTimeString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        return simpleDateFormat.format(new Date());
	}
	
	public String fetchConfigValue(String configName) {
		String value = null;
		
		Connection con = null;
        try {
            // create a connection to the database
            con = getConnection();
            
            PreparedStatement ps = con.prepareStatement(getConfigSql);
            
            ps.setString(1, configName);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	value = rs.getString("Value");
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
        
        return value;
	}
	
	public boolean setConfigValue(String configKey, String configValue) {
		int rowsAffected = 0;
		
		Connection con = null;
        try {
            // create a connection to the database
            con = getConnection();
            
            PreparedStatement ps = con.prepareStatement(setConfigSql);
            
            ps.setString(1, configValue);
            
            ps.setString(2, configKey);
            
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
	
	public Connection getConnection() throws URISyntaxException, SQLException {
		if (localSQLite) {
			return DriverManager.getConnection(SQLiteURL);
		} else {
		    URI dbUri = new URI(System.getenv("DATABASE_URL"));

		    String username = dbUri.getUserInfo().split(":")[0];
		    String password = dbUri.getUserInfo().split(":")[1];
		    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

		    return DriverManager.getConnection(dbUrl, username, password);
		}
	}

}
