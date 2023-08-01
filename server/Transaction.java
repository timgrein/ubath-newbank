package newbank.server;

public class Transaction {

    private String type;
    private double amount;
    private String fromUser;
    private String toUser;

    public Transaction(String type, double amount, String fromUser, String toUser) {
        this.type = type;
        this.amount = amount;
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public String toString() {
        return (type + " £" + amount + " from " + fromUser + " to " + toUser + "\n");
    }
}
