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

	public void makePayment(String payment, String targetAccount) {
		double pay = Double.valueOf(payment);
		for (int i = 0; i < accounts.size(); i++) {
			if (accounts.get(i).getAccountType().equals(targetAccount)) {
				accounts.get(i).accountPayment(pay);
			}
		}
	}

	public void makeDeduction(String payment, String payerAccount) {
		double pay = -Double.valueOf(payment);
		for (int i = 0; i < accounts.size(); i++) {
			if (accounts.get(i).getAccountType().equals(payerAccount)) {
				accounts.get(i).accountPayment(pay);
			}
		}
	}

	public double checkBalance(String payerAccount) {
		double balance = 0;
		for (int i = 0; i < accounts.size(); i++) {
			if (accounts.get(i).getAccountType().equals(payerAccount)) {
				balance = accounts.get(i).getCurrentBalance();
			}
		}
		return balance;
	}
}
