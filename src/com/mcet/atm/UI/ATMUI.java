package com.mcet.atm.UI;

import java.util.Scanner;

import com.mcet.atm.servicesImpl.ATMServicesImpl;
public class ATMUI {

	public static void main(String[] args)  {
		// TODO Auto-generated method stub
        Scanner s = new Scanner(System.in);
        System.out.println("Insert the card");
        s.nextLine();
        System.out.println("1.Create Account");
        System.out.println("2.Login to Account");
        System.out.println("3.Exit Menu");
        int k=s.nextInt();
        ATMServicesImpl atmServices = new ATMServicesImpl();
        switch(k) {
        case 1: atmServices.createAcc(); break;
        case 2: atmServices.loginAcc();break;
        case 3: System.exit(1); break;
        }
	}
}

