package edu.byu.cs.tweeter.model.net.response;

public class GetFollowingCountResponse extends Response {
    int count;

    public GetFollowingCountResponse(int count) {
        super(true);
        this.count = count;
    }

    public GetFollowingCountResponse(String message) {
        super(false, message);
    }
}
