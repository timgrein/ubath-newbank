package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread{
	
	private newbank.server.NewBank bank;
	private BufferedReader in;
	private PrintWriter out;
	private boolean isLoggedIn;
	
	
	public NewBankClientHandler(Socket s) throws IOException {
		bank = newbank.server.NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
		isLoggedIn = false; // Initialize login status to false
	}
	
	public void run() {
		// keep getting requests from the client and processing them
		try {
			while(!isLoggedIn){
				// login or create account
				out.println("Do you have an account with us?");
				String answer = in.readLine();
				if (answer.equals("yes")) {
					// ask for username
					out.println("Enter Username");
					String userName = in.readLine();
					// ask for password
					out.println("Enter Password");
					String password = in.readLine();
					out.println("Checking Details...");
					// authenticate user and get customer ID token from bank for use in subsequent requests
					newbank.server.CustomerID customer = bank.checkLogInDetails(userName, password);
					// if the user is authenticated then get requests from the user and process them
					if(customer != null) {
						isLoggedIn = true; // Set the flag to true if login is successful
						out.println("Log In Successful. What do you want to do?");
						while(isLoggedIn) {
							String request = in.readLine();
							System.out.println("Request from " + customer.getKey());
							String response = bank.processRequest(customer, request);
							out.println(response);
						}
					}
					else {
						out.println("Log In Failed, try again");
						out.println();
					}
				}
				// if they don't have an account
				// let them create a name and password and what type of account they would like to open
				if (answer.equals("no")) {
					out.println("Enter name:");
					String username = in.readLine();
					out.println("Enter password:");
					String password = in.readLine();
					out.println("What kind of account do want to make with us?: Main, Savings, or Checking");
					String accountType = in.readLine();
					String response = newbank.server.NewBank.addNewAccount(username, password, accountType);
					out.println(response);
				}
				else {
					out.println("Log In Failed, try again");
					out.println();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
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
