package edu.byu.cs.tweeter.model.net.response;

public class CountResponse extends Response {
    int count;

    public CountResponse(int count) {
        super(true);
        this.count = count;
    }

    public CountResponse(String message) {
        super(false, message);
    }

    public int getCount() {
        return count;
    }
}
