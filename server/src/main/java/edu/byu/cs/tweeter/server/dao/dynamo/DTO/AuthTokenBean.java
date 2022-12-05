package edu.byu.cs.tweeter.server.dao.dynamo.DTO;

public class AuthTokenBean {
    private String token;
    private String datetime;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
