package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread {

	private static NewBank bank;
	private static BufferedReader in;
	private static PrintWriter out;
	private static boolean isLoggedIn;
	private static boolean logOutRequested = false;


	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
		isLoggedIn = false; // Initialize login status to false
	}

	//Function which allows you to get userInput from another class
	public static String getUserInput(String message) throws IOException {
		out.println(message);
		return in.readLine();
	}

	public static void printMessage(String message) throws IOException {
		out.println(message);
	}

	//if user has requested to logout - exit while loop
	public static void logOut() {
		logOutRequested = true;
		isLoggedIn = false;
	}

	public void run() {
		// keep getting requests from the client and processing them
		// print welcome message
		out.println("\nWelcome to NewBank – Your Reliable Banking Solution");

		try {
			while (!isLoggedIn) {
				// login or create account
				out.println("\nPlease select an option from the menu below:\n");
				out.println("[1] Log In             [2] Create New Account             [3] Exit");

				String answer = in.readLine();

				if (answer.equals("1")) {
					// ask for username
					out.println("Enter Username");
					String userName = in.readLine();
					// ask for password
					out.println("Enter Password");
					String password = in.readLine();
					out.println("Checking Details...");
					// authenticate user and get customer ID token from bank for use in subsequent requests
					CustomerID customer = bank.checkLogInDetails(userName, password);
					// if the user is authenticated then get requests from the user and process them
					if (customer != null) {
						isLoggedIn = true; // Set the flag to true if login is successful
						out.println("Log In Successful. What do you want to do?");
						while (isLoggedIn) {
							String request = in.readLine();
							System.out.println("Request from " + customer.getKey());
							String response = bank.processRequest(customer, request);
							out.println(response);
						}
					} else {
						out.println("Log In Failed, try again");
						out.println();
					}
				} else if (answer.equals("2")) {
					out.println("Initiating new account registration. Please enter the required information:");
					out.println("Enter name:");
					String username = in.readLine();
					out.println("Enter password:");
					String password = in.readLine();

					String accountType;
					while (true) {
						out.println("What kind of account do you want to make with us?: Main, Savings, or Checking");
						accountType = in.readLine();
						if (accountType.equalsIgnoreCase("Main") || accountType.equalsIgnoreCase("Savings") || accountType.equalsIgnoreCase("Checking")) {
							break; // Valid input, exit the loop
						} else {
							out.println("Invalid account type. Please enter 'Main', 'Savings', or 'Checking'.");
						}
					}
					String response = NewBank.addNewAccount(username, password, accountType);
					out.println(response);

				} else if (answer.equals("3")) {
					out.println("\nThank you. Have a nice day");
					break;
				} else {
					if (logOutRequested) {
						out.println("Successfully logged out");
					} else {
						out.println("Invalid choice. Please enter a valid option.");
					}
					out.println();
				}
			} // End of while loop
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}


}
