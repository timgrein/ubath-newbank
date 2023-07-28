package newbank.server;

import java.util.ArrayList;

public class Customer {
	
	private ArrayList<Account> accounts;
	private ArrayList<String> accountTypes; //stores the accountTypes in an arraylist for easy retrieval

	private ArrayList<Lender> lenders;
	private ArrayList<Borrower> borrowers;

	private ArrayList<Sender> senders;
	private ArrayList<Receiver> receivers;

	private String password;
	
	public Customer(String password) {
		this.password = password;

		accounts = new ArrayList<>();
		accountTypes = new ArrayList<>();

		lenders = new ArrayList<>();
		borrowers = new ArrayList<>();

		senders = new ArrayList<>();
		receivers = new ArrayList<>();
	}

	public void addLender(Lender lender) {
		lenders.add(lender);
	}

	public String lenderToString() {
		String s = "";
		for (Lender a : lenders) {
			s += a.toString();
		}
		return s;
	}

	public void addBorrower(Borrower borrower) {
		borrowers.add(borrower);
	}

	public String borrowerToString() {
		String s = "";
		for (Borrower a : borrowers) {
			s += a.toString();
		}
		return s;
	}

	public void addSender(Sender sender) {
		senders.add(sender);
	}

	public String senderToString() {
		String s = "";
		for (Sender a : senders) {
			s += a.toString();
		}
		return s;
	}

	public void addReceiver(Receiver receiver) {
		receivers.add(receiver);
	}

	public String ReceiversToString() {
		String s = "";
		for (Receiver a : receivers) {
			s += a.toString();
		}
		return s;
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
		for (Account account : accounts) {
			if (account.getAccountType().equals(targetAccount)) {
				account.accountPayment(pay);
			}
		}
	}

	public void makeDeduction(String payment, String payerAccount) {
		double pay = -Double.valueOf(payment);
		for (Account account : accounts) {
			if (account.getAccountType().equals(payerAccount)) {
				account.accountPayment(pay);
			}
		}
	}

	public double checkBalance(String payerAccount) {
		double balance = 0;
		for (Account account : accounts) {
			if (account.getAccountType().equals(payerAccount)) {
				balance = account.getCurrentBalance();
			}
		}
		return balance;
	}
}
