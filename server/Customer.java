package newbank.server;

import java.util.ArrayList;

public class Customer {
	
	private ArrayList<Account> accounts;
	private ArrayList<String> accountTypes; //stores the accountTypes in an arraylist for easy retrieval
	private String password;
	
	public Customer(String password) {
		this.password = password;
		accounts = new ArrayList<>();
		accountTypes = new ArrayList<>();
	}

	public ArrayList<String> getAccountTypes() {
		return accountTypes;
	}

	public String getPassword(){
		return password;
	}

	public String setPassword(){
		this.password = password;
		return null;
	}

	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	public void addAccount(Account account, String accountName) {
		accounts.add(account);
		accountTypes.add(accountName);
	}

	public void makePayment(String payment) {
		double pay = Double.valueOf(payment);
		for(Account a : accounts) {
			a.accountPayment(pay);
		}
	}

	public void makeDeduction(String payment) {
		double pay = -Double.valueOf(payment);
		for(Account a : accounts) {
			a.accountDeduction(pay);
		}
	}

	public double checkBalance() {
		double balance = 0;
		for(Account a : accounts) {
			balance += a.getCurrentBalance();
		}
		return balance;
	}
}
