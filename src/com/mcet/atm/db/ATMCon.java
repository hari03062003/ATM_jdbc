package com.mcet.atm.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ATMCon {
	
	  private ATMCon() {}
	  public static Connection getConnection()  {
		  Connection obj = null;
		  if (obj == null) {
			  try {
	          Class.forName("com.mysql.cj.jdbc.Driver");  
	          obj= DriverManager.getConnection("jdbc:mysql://localhost:3306/atm", "root", "admin"); 
	    }catch(Exception e) {
	    	System.out.println(e.getMessage());
	    }
		  }
	    return obj;
	  }
	public static void closeConnection(Connection con) throws SQLException {
		// TODO Auto-generated method stub
		con.close();
	}
      
}
