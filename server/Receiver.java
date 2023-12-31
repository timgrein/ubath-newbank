package newbank.server;

public class Receiver extends Request {

    String loanDestination;

    public Receiver(String sender, String receiver, String requestType, double amount, String loanDestination) {
        this.sender = sender;
        this.receiver = receiver;
        this.requestType = requestType;
        this.amount = amount;
        this.loanDestination = loanDestination;
    }

    @Override
    public String toString() {
        return ("You have a request from " + sender + " for a " + requestType + " for the amount of £" + amount);
    }

    public String getLoanDestination() {
        return loanDestination;
    }
}
