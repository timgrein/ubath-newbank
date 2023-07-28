package newbank.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private static HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}

	public HashMap<String, Customer> getCustomers() {
		return customers;
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
	public synchronized String processRequest(CustomerID customer, String request) throws IOException {
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
			case "NEWLOAN" :
				return newLoan(customer, requestParts);
			case "SHOWMYLOANS" :
				return showMyLenders(customer) + showMyBorrowers(customer);
			case "PAYLOAN" :
				return payLoan(customer, requestParts);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	private String showMyLenders(CustomerID customer) throws IOException {
		Customer currentCustomer = customers.get(customer.getKey());
		if (currentCustomer.getLenders().size() == 0) {
			return "\nYou are not loaning anything to anyone at the moment";
		} else {
			NewBankClientHandler.printMessage("\nYou lending loans:");
			return ("\n" + customers.get(customer.getKey()).lenderToString());
		}
	}

	private String showMyBorrowers(CustomerID customer) throws IOException {
		Customer currentCustomer = customers.get(customer.getKey());
		if (currentCustomer.getBorrowers().size() == 0) {
			return "\nYou do not have any loans";
		} else {
			NewBankClientHandler.printMessage("Your loans:\n");
			return ("\n" + customers.get(customer.getKey()).borrowerToString());
		}
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

				boolean accountAlreadyExists = currentCustomer.getAccountTypes().contains(requestParts[1]);
				if (accountAlreadyExists) {
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
	private String payCommand(CustomerID customer, String[] requestParts) throws IOException {
		Customer currentCustomer = customers.get(customer.getKey());
    	Customer payCustomer = customers.get(requestParts[1]);
    
    	// If user just enters PAY
		if (requestParts.length == 1) {
			return "Please enter in this format: PAY, accountName, amount";
		}
    
    	//checks if the second thing they entered is a username that exists
		if (!customers.containsKey(requestParts[1])) {
			return "Please make sure the accountName is correct\nPlease enter like this - 'PAY accountName amount'";
		}
    
    	boolean isNumeric = true;
		//checks if third thing they entered is a number
		try {
			Double num = Double.parseDouble(requestParts[2]);
		} catch (NumberFormatException e) {
			isNumeric = false;
		}
		if (!isNumeric) {
			return "Please check that you entered a correct value.\nPlease enter like this - 'PAY accountName amount'";
		}
    
    	if (requestParts[2].equals("0")) {
			 return "You cannot pay someone £0";
		}
    
		//get user input - asking them which account they would like to pay from and to
		String payerAccount = NewBankClientHandler.getUserInput("From which account?");
		if (!currentCustomer.getAccountTypes().contains(payerAccount)) {
			return "You don't have a " + payerAccount + " account";
		}

		String targetAccount = NewBankClientHandler.getUserInput("To which account?");
		if (!payCustomer.getAccountTypes().contains(targetAccount)) {
			return "The person your trying to pay does not have a " + targetAccount + " account";
		}


    	boolean isForeignAccount = !customer.getKey().equals(requestParts[1]);
		double checkCurrentBalance = currentCustomer.checkBalance(payerAccount);
		if(isForeignAccount) {
			boolean enoughBalancePresent = checkCurrentBalance >= Double.parseDouble(requestParts[2]);
			if (enoughBalancePresent) {

				//Make payment to person
				payCustomer.makePayment(requestParts[2], targetAccount);
				//Make deduction to the customer's account
				currentCustomer.makeDeduction(requestParts[2], payerAccount);
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
	private String newLoan(CustomerID customer, String[] requestParts) throws IOException {
		Customer currentCustomer = customers.get(customer.getKey());
		Customer receiver = customers.get(requestParts[1]);
		System.out.println(Arrays.toString(requestParts));

		if (requestParts.length == 1) {
			return "Please enter in format: NEWLOAN userName amount";
		}

		//checks if the second thing they entered is a username that exists
		if (!customers.containsKey(requestParts[1])) {
			return "Please make sure the accountName is correct\nPlease enter like this - 'NEWLOAN userName amount'";
		}

		boolean isNumeric = true;
		//checks if third thing they entered is a number
		try {
			Double num = Double.parseDouble(requestParts[2]);
		} catch (NumberFormatException e) {
			isNumeric = false;
		}
		if (!isNumeric) {
			return "Please check that you entered a correct value.\nPlease enter like this - 'PAY accountName amount'";
		}

		String currentCustomerName = null;
		for (Entry<String, Customer> entry: customers.entrySet()) {
			if (entry.getValue() == currentCustomer) {
				currentCustomerName = entry.getKey();
				break;
			}
		}

		String receiverName = null;
		for (Entry<String, Customer> entry: customers.entrySet()) {
			if (entry.getValue() == receiver) {
				receiverName = entry.getKey();
				break;
			}
		}

		String loanDestination = NewBankClientHandler.getUserInput("Which account would you like the loan paid into?");

		//checks if account exists
		if (!currentCustomer.getAccountTypes().contains(loanDestination)) {
			return "You do not have that type of account";
		}
		//create the requests
		currentCustomer.addSender(new Sender(currentCustomerName, requestParts[1], "Loan", Double.parseDouble(requestParts[2])));
		receiver.addReceiver(new Receiver(currentCustomerName, receiverName, "Loan", Double.parseDouble(requestParts[2]), loanDestination));

		return ("A request for a Loan of the amount £" + requestParts[2] + " has been sent to " + receiverName);
	}
	public void createLoan(Customer lender, Receiver receiver) throws IOException {

		Customer currentCustomer = customers.get(receiver.getReceiver());

		String loanOrigin = NewBankClientHandler.getUserInput("What account would you like this to come out of?");

		boolean accountExists = currentCustomer.getAccountTypes().contains(loanOrigin);
		if (!accountExists) {
			NewBankClientHandler.printMessage("You do not have that type of account");
		}

		//make deduction/payment
		lender.makeDeduction(Double.toString(receiver.getAmount()), loanOrigin);
		customers.get(receiver.getSender()).makePayment(Double.toString(receiver.getAmount()), receiver.getLoanDestination());

		//create Lender
		lender.addLender(new Lender(receiver.getAmount(), receiver.getReceiver(), receiver.getSender()));
		//create Borrower
		(customers.get(receiver.getSender())).addBorrower(new Borrower(receiver.getAmount(), receiver.getReceiver(), receiver.getSender()));

	}

	public String payLoan(CustomerID customer, String[] requestParts) throws IOException {
		Customer currentCustomer = customers.get(customer.getKey());

		boolean hasALoan = currentCustomer.getBorrowers().size() > 0;
		if (!hasALoan) {
			return "You don't have any loans";
		}

		currentCustomer.borrowerToString();
		double payAmount = Double.parseDouble(NewBankClientHandler.getUserInput("How much would you like to pay?"));
		String desiredAccount = NewBankClientHandler.getUserInput("From which account?");

		boolean desiredAccountExists = currentCustomer.getAccountTypes().contains(desiredAccount);
		if (!desiredAccountExists) {
			return "You don't have that type of account";
		}

		boolean payAmountNotValid = payAmount <= 0;
		if (payAmountNotValid) {
			return "You can pay £0 or less";
		}

		boolean notPayingTooMuch = payAmount <= currentCustomer.getBorrowers().get(0).getLoanAmount();
		if (!notPayingTooMuch) {
			return "Your paying too much";
		}

		currentCustomer.getBorrowers().get(0).changeAmount(payAmount);
		return "The new balance on the loan is £" + currentCustomer.getBorrowers().get(0).getLoanAmount();
	}

}
