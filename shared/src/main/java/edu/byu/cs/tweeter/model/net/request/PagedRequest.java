package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class PagedRequest<T> extends Request {

    private AuthToken authToken;
    //TODO: change this to User type object
    private String targetUserAlias;
    private int limit;
    private T lastItem;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private PagedRequest() {}

    /**
     * Creates an instance.
     *
     * @param targetUserAlias the alias of the user whose followees are to be returned.
     * @param limit the maximum number of followees to return.
     * @param lastItem the alias of the last followee that was returned in the previous request (null if
     *                     there was no previous request or if no followees were returned in the
     *                     previous request).
     */
    public PagedRequest(AuthToken authToken, String targetUserAlias, int limit, T lastItem) {
        this.authToken = authToken;
        this.targetUserAlias = targetUserAlias;
        this.limit = limit;
        this.lastItem = lastItem;
    }

    /**
     * Returns the auth token of the user who is making the request.
     *
     * @return the auth token.
     */
    public AuthToken getAuthToken() {
        return authToken;
    }

    /**
     * Sets the auth token.
     *
     * @param authToken the auth token.
     */
    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    /**
     * Returns the follower whose followees are to be returned by this request.
     *
     * @return the follower.
     */
    public String getTargetUserAlias() {
        return targetUserAlias;
    }

    /**
     * Sets the follower.
     *
     * @param targetUserAlias the follower.
     */
    public void setTargetUserAlias(String targetUserAlias) {
        this.targetUserAlias = targetUserAlias;
    }

    /**
     * Returns the number representing the maximum number of followees to be returned by this request.
     *
     * @return the limit.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     *
     * @param limit the limit.
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Returns the last followee that was returned in the previous request or null if there was no
     * previous request or if no followees were returned in the previous request.
     *
     * @return the last followee.
     */
    public T getLastItem() {
        return lastItem;
    }

    /**
     * Sets the last followee.
     *
     * @param lastItem the last followee.
     */
    public void setLastItem(T lastItem) {
        this.lastItem = lastItem;
    }
}

