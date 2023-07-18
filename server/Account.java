package newbank.server;

public class Account {
	
	private String accountName;
	private double openingBalance;
	private double currentBalance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
		this.currentBalance = openingBalance;
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
