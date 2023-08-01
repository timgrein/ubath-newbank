package newbank.server;

public class Lender extends Loan {

    public Lender(double loanAmount, String lender, String borrower) {
        this.loanAmount = loanAmount;
        this.lender = lender;
        this.borrower = borrower;
    }

    @Override
    public String toString() {
        return ("You are loaning " + borrower + " £" + loanAmount);
    }
}
