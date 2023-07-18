package newbank.server;

import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer("password03");
		bhagy.addAccount(new Account("Main", 1200.0));
		customers.put("Bhagy", bhagy);
		
		Customer christina = new Customer("password02");
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);
		
		Customer john = new Customer("password01");
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			Customer customer = customers.get(userName); //get object associated with key(userName) in hashmap
			if(customer.getPassword().equals(password)) { //check if password matches the one assigned to object
				return new CustomerID(userName);
			}
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) {
			String[] requestParts = request.split(" "); //Split request from user in sentences and put them in array
			switch(requestParts[0]) { //first element of array "[0]" is user's command
			case "SHOWMYACCOUNTS" :
				return showMyAccounts(customer);
			case "NEWACCOUNT" :
				return newAccountCreation(customer, requestParts);
			case "PAY" :
				return payCommand(customer, requestParts);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	/**
	 * This method creates a new account for an existing customer
	 * The account is created with a default amount of 50.0 if the chosen type of account exist in the bank
	 * Otherwise this return a message with instructions
	 * @param customer
	 * @param requestParts
	 * @return success or fail messages
	 */
	private String newAccountCreation(CustomerID customer, String[] requestParts) {
		//if the type of account exist in the bank then create account
		if(requestParts[1].equals("Main")|| requestParts[1].equals("Savings")|| requestParts[1].equals("Checking")) {
			Customer currentCustomer = customers.get(customer.getKey());
			currentCustomer.addAccount(new Account(requestParts[1], 50.0));
			return "The account has successfully been created";
		}
		else{
			return "The chose type of account does not exist, Please choose between Main, Savings and Checking" ;
		}
	}

	/**
	 * This method allow customer to make payments to other bank users
	 * The method first check if the payments is not to customer's own account and also if funds are available
	 * @param customer
	 * @param requestParts
	 * @return success or fail messages
	 */
	private String payCommand(CustomerID customer, String[] requestParts) {
		Customer currentCustomer = customers.get(customer.getKey());
		double checkCurrentBalance = currentCustomer.checkBalance();
		//If account is not being made to own account
		if(!customer.getKey().equals(requestParts[1])) {
			//If the current balance is greater than payment being made
			if (checkCurrentBalance > Double.parseDouble(requestParts[2])) {
				//Make payment to person
				Customer payCustomer = customers.get(requestParts[1]);
				payCustomer.makePayment(requestParts[2]);
				//Make deduction to the customer's account
				currentCustomer.makeDeduction(requestParts[2]);
				return "The Payment has been made";
			} else {
				return "There's not enough funds in the account";
			}
		}
		//Otherwise display message
		else{
			return "Payment cannot be made to own account";
		}
	}

}
