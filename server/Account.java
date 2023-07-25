package newbank.server;

public class Account {
	
	private String accountName;
	private double openingBalance;
	private double currentBalance;
	private String accountType;

	public Account(String accountName, double currentBalance, String accountType) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
		this.currentBalance = currentBalance;
		this.accountType = accountType;
	}

	public String getAccountType() {
		return accountType;
	}
	
	public String toString() {
		return (accountName + ": " + currentBalance + "\n");
	}

	public void accountPayment(double payment){
		this.currentBalance = currentBalance + payment;
	}

	public void accountDeduction(double payment){
		this.currentBalance = currentBalance + payment;
	}
	public double getCurrentBalance(){
		return currentBalance;
	}

}
