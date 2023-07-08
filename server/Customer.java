package newbank.server;

import java.util.ArrayList;

public class Customer {
	
	private ArrayList<Account> accounts;
	private String password;
	
	public Customer(String password) {
		this.password = password;
		accounts = new ArrayList<>();
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

	public void addAccount(Account account) {
		accounts.add(account);		
	}
}
