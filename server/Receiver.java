package newbank.server;

public class Receiver extends Request {

    public Receiver(String sender, String receiver, String requestType, double amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.requestType = requestType;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return ("You have a request from " + sender + " for a " + requestType + " for the amount of £" + amount);
    }
}
