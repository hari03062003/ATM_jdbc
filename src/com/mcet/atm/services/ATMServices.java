package com.mcet.atm.services;

import java.sql.SQLException;

import com.mcet.atm.exceptions.InvalidAccException;

public interface ATMServices {
	void createAcc() ;
	boolean checkAcc(int accNum) ;
	void withdrawal() ;
	int checkWithdrawls(int accNum, int transtype) ;
	void transactionUpdate(int accNum, int transtype) ;
	void deposit() ;
	int checkBal() throws SQLException, ClassNotFoundException;
	void loginAcc();
	void loginMenu() ;
	boolean checkLogin(String username, String password) ;
	int checkBalance(int accNum);	
}
