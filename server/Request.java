package newbank.server;

abstract class Request {

    String sender;
    String receiver;
    String requestType;
    double amount;

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getRequestType() {
        return requestType;
    }

    public double getAmount() {
        return amount;
    }

    public abstract String toString();

}
