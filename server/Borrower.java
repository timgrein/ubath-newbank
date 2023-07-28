package newbank.server;

public class Borrower extends Loan {

    public Borrower(double loanAmount, String lender, String borrower) {
        this.loanAmount = loanAmount;
        this.lender = lender;
        this.borrower = borrower;
    }

    @Override
    public String toString() {
        return ("You owe " + lender + " £" + loanAmount);
    }
}
