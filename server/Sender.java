package newbank.server;

public class Sender extends Request {

    public Sender(String sender, String receiver, String requestType, double amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.requestType = requestType;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return ("You have an active request:\n" + "You sent a request to " + receiver + " for a " + requestType + " for the amount of £" + amount);
    }
}
