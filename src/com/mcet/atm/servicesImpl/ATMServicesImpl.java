package com.mcet.atm.servicesImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.mcet.atm.db.ATMCon;
import com.mcet.atm.exceptions.InvalidAccException;
import com.mcet.atm.exceptions.LoginFailedException;
import com.mcet.atm.exceptions.WithdrawalException;
import com.mcet.atm.services.ATMServices;
public class ATMServicesImpl implements ATMServices{
	int WITHDRAWAL = 1;
	int DEPOSIT = 2;
	int CHECKBALANCE = 3;

	public void createAcc() {
		Scanner s = new Scanner(System.in);
		System.out.println("Enter Name");
		String name = s.next();
		System.out.println("Enter Account Number");
		int accNum = s.nextInt();
		System.out.println("Enter UserName");
		String userName = s.next();
		System.out.println("Enter Password");
		String password = s.next();
		Connection con = null;
		try {
			con = ATMCon.getConnection();
			if (checkAcc(accNum)) {
				PreparedStatement stmt = con
						.prepareStatement("insert into account(accName,accNum,userName,password) values(?,?,?,?)");
				stmt.setString(1, name);
				stmt.setInt(2, accNum);// 1 specifies the first parameter in the query
				stmt.setString(3, userName);
				stmt.setString(4, password);
				int i = stmt.executeUpdate();
				System.out.println(i + " Account created successfully ");
				loginAcc();
			} else
				throw new InvalidAccException("Account No. already generated, kindly change it");
		} catch (InvalidAccException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				ATMCon.closeConnection(con);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean checkAcc(int accNum)  {
		// TODO Auto-generated method stub
		int count = 0;
		boolean flag = true;
		Connection con = ATMCon.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement("select count(accNum) from account where accNum=?");
			ps.setInt(1, accNum);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				count = rs.getInt(1);
			if (count > 0)
				flag = false;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				ATMCon.closeConnection(con);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}

	public void withdrawal()  {
		Scanner s = new Scanner(System.in);
		System.out.println("Enter Account Number");
		int accNum = s.nextInt();
		System.out.println("Enter Amount to Withdraw");
		int amount = s.nextInt();
		Connection con = null;
		con = ATMCon.getConnection();
		try {
			int currentBal = checkBalance(accNum);
			if (currentBal < amount)
				throw new WithdrawalException("Insufficient Balance");
			int count = checkWithdrawls(accNum, WITHDRAWAL);
			if (count > 3)
				throw new WithdrawalException("Daily Withdrawal Limit Reached");
			PreparedStatement ps = con.prepareStatement("update account set accBal=? where accNum=?");
			ps.setInt(1, currentBal - amount);
			ps.setInt(2, accNum);
			int i = ps.executeUpdate();
			transactionUpdate(accNum, 1);
			System.out.println(amount + " Amount Withdraw successfull");
			System.out.println("Current Balance is: " + checkBalance(accNum));
		} catch (WithdrawalException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				ATMCon.closeConnection(con);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			loginMenu();
		}

	}

	public int checkWithdrawls(int accNum, int transtype)  {
		int count = 0;
		Connection con = null;
		con = ATMCon.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(
					"select count(tid) from transaction where date(transactiondate)=curdate() and transactiontype=? and accNum=?");
			ps.setInt(1, transtype);
			ps.setInt(2, accNum);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				ATMCon.closeConnection(con);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return count;
	}

	public void transactionUpdate(int accNum, int transtype)  {

		Connection con = null;
		con = ATMCon.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(
					"insert into transaction(transactiondate,transactiontype,accNum) values (now(),?,?)");
			ps.setInt(1, transtype);
			ps.setInt(2, accNum);
			int i = ps.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				ATMCon.closeConnection(con);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void deposit() {
		Scanner s = new Scanner(System.in);
		System.out.println("Enter Account Number");
		int accNum = s.nextInt();
		System.out.println("Enter Amount to Deposit");
		int amount = s.nextInt();
		Connection con = null;
		try {
			con = ATMCon.getConnection();
			int currentBal = checkBalance(accNum);
			PreparedStatement ps = con.prepareStatement("update account set accBal=? where accNum=?");
			ps.setInt(1, amount + currentBal);
			ps.setInt(2, accNum);
			int i = ps.executeUpdate();
			transactionUpdate(accNum, DEPOSIT);
			System.out.println(amount + " Amount deposited successfully");
			System.out.println("Current Balance is: " + checkBalance(accNum));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				ATMCon.closeConnection(con);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			loginMenu();
		}
	}

	public int checkBal() {
		int accBal = 0;
		Scanner s = new Scanner(System.in);
		System.out.println("Enter Account Number");
		int accNum = s.nextInt();
		Connection con = null;
		try {
			con = ATMCon.getConnection();
			accBal = checkBalance(accNum);
			transactionUpdate(accNum, CHECKBALANCE);
			System.out.println("Account Balance is " + accBal);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				ATMCon.closeConnection(con);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			loginMenu();
		}
		return accBal;
	}

	public void loginAcc() {
		Scanner s = new Scanner(System.in);
		System.out.println("Enter UserName");
		String username = s.next();
		System.out.println("Enter Password");
		String password = s.next();
		try {
			if (!checkLogin(username, password)) 
				throw new LoginFailedException("Login Failed.Please provide correct details!!");
				loginMenu();
			
		} catch (LoginFailedException e) {
			System.out.println(e.getMessage());
			loginAcc();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			loginAcc();
		}
	}

	public void loginMenu()  {
		Scanner s = new Scanner(System.in);
		System.out.println("Press Enter");
		s.nextLine();
		System.out.println(" 1. Withdrawal \n 2. Deposit \n 3. Check Balance \n 4. Exit Menu");
		int i = s.nextInt();
		switch (i) {
		case 1:
			withdrawal();
			break;
		case 2:
			deposit();
			break;
		case 3:
			checkBal();
			break;
		case 4:
			System.exit(1);
		}

	}

	public boolean checkLogin(String username, String password)  {
		boolean flag = false;
		int count = 0;
		Connection con = null;
		con = ATMCon.getConnection();
		try {
			PreparedStatement ps = con
					.prepareStatement("select count(accId) from account where userName=? and password=?");
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			if (count == 1)
				flag = true;
			else
				loginAcc();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				ATMCon.closeConnection(con);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}

	public int checkBalance(int accNum) {
		Connection con = null;
		int accBal = 0;
		try {
			con = ATMCon.getConnection();
			PreparedStatement ps = con.prepareStatement("select accBal from account where accNum=?");
			ps.setInt(1, accNum);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				accBal = rs.getInt(1);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				ATMCon.closeConnection(con);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return accBal;
	}


}