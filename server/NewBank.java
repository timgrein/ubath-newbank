package newbank.server;

import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private static HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}

	// add a new account if user doesn't have one
	// checks if there is another account under the same name
	// (collects data from NewBankClientHandler)
	public static String addNewAccount(String username, String password, String accountType) {
		if (customers.containsKey(username)) {
			System.out.println("The username already exists in the Hashmap");
			return "An account with this name already exists, please try again.";
		}
		else {
			Customer newCustomer = new Customer(password);
			newCustomer.addAccount(new Account(accountType, 50.0, accountType), accountType);
			customers.put(username, newCustomer);
			return "Account successfully created";
		}
	}
	
	private void addTestData() {
		Customer bhagy = new Customer("password03");
		bhagy.addAccount(new Account("Main", 1200.0, "Main"), "Main");
		customers.put("Bhagy", bhagy);
		
		Customer christina = new Customer("password02");
		christina.addAccount(new Account("Savings", 1500.0, "Savings"), "Savings");
		customers.put("Christina", christina);
		
		Customer john = new Customer("password01");
		john.addAccount(new Account("Checking", 250.0, "Checking"), "Checking");
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
			case "LOGOUT":
				return logOut();
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	private String logOut() {
		NewBankClientHandler.logOut();
		return "Logging out";
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
		Customer currentCustomer = customers.get(customer.getKey());

		if(requestParts.length > 1) { //if the request only has the command and type of account execute this
			//if the type of account exist in the bank then create account
			if ( ("Main".equals(requestParts[1]) || "Savings".equals(requestParts[1]) || "Checking".equals(requestParts[1]))) {
				//checks if the accountType already exists
				if (currentCustomer.getAccountTypes().contains(requestParts[1])) {
					return "You already have a " + requestParts[1] + " account.\nWhat would you like to do?";
				} else {
					String accountType = requestParts[1];
					double initialBalance = 50.0;
					currentCustomer.addAccount(new Account(accountType, initialBalance, accountType), accountType);
					return String.format("Your %s account has successfully been created. The initial balance is %.2f", accountType, initialBalance);
				}
			} else { //Otherwise, display message
				return """
				The type of account chosen does not exist. we have the following accounts available:
				- Main
				- Savings
				- Checking
					 """;
			}
		}
		else {// Otherwise, if the request only has the command, display message
			return """
				Please specify the type of account along with the command e.g. NEWACCOUNT Savings
				we have the following accounts available:
				- Main
				- Savings
				- Checking
				""";
		}
	}

	/**
	 * This method allow customer to make payments to other bank users
	 * The method first check if the payments is not to customer's own account and also if funds are available
	 * In order to make payment the following format should be used: PAY accountNAME amount (example: PAY John 20)
	 * @param customer
	 * @param requestParts
	 * @return success or fail messages
	 */
	private String payCommand(CustomerID customer, String[] requestParts) {
		// If user just enters PAY
		if (requestParts.length == 1) {
			return "Please enter in this format: PAY yourAccountType, accountName, amount";
		}

		// If the user enters PAY without specifying an account type
		if (requestParts.length < 4) {
			return "Please enter the account type (Main, Savings, or Checking), the recipient's name, and the amount to transfer\n" +
					"in this format: PAY yourAccountType, accountName, amount";
		}

		// Extract the yourAccountType, accountName of recipient, and amount from the requestParts array
		String[] paymentDetails = requestParts[1].split(",");
		if (paymentDetails.length != 3) {
			return "Invalid input format. Please enter in this format: yourAccountType, accountName, amount";
		}

		String yourAccountType = paymentDetails[0].trim();

		// Check if the yourAccountType is valid (Main, Savings, or Checking)
		if (!("Main".equalsIgnoreCase(yourAccountType) || "Savings".equalsIgnoreCase(yourAccountType) || "Checking".equalsIgnoreCase(yourAccountType))) {
			return "Invalid account type. Please use one of the following: Main, Savings, or Checking";
		}

		// Check if the specified account exists for the customer
		//if (!customers.containsKey(customer.getKey()) || !customers.get(customer.getKey()).hasAccountOfType(yourAccountType)) {
		//	return "You don't have an account of type " + yourAccountType + " to make the payment.";
		//}


		Customer currentCustomer = customers.get(customer.getKey());
		double checkCurrentBalance = currentCustomer.checkBalance();
		boolean isForeignAccount = !customer.getKey().equals(requestParts[1]);
		boolean isNumeric = true;

		//checks if the second thing they entered is a username that exists
		if (!customers.containsKey(requestParts[1])) {
			return "Please make sure the accountName is correct\nPlease enter like this - 'PAY accountName amount'";
		}

		//checks if third thing they entered is a number
		try {
			Double num = Double.parseDouble(requestParts[2]);
		} catch (NumberFormatException e) {
			isNumeric = false;
		}
		if (!isNumeric) {
			return "Please check that you entered a correct value.\nPlease enter like this - 'PAY accountName amount'";
		}

		if(isForeignAccount) {
			boolean enoughBalancePresent = checkCurrentBalance > Double.parseDouble(requestParts[2]);
			if (enoughBalancePresent) {
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
