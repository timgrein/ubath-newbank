package newbank.server;

abstract class Loan {

    double loanAmount;
    String lender;
    String borrower;

    public double getLoanAmount() {
        return loanAmount;
    }

    public String getLender() {
        return lender;
    }

    public String getBorrower() {
        return borrower;
    }

    public abstract String toString();

}


