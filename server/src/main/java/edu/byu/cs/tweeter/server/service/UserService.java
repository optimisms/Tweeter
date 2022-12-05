package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.Database;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {
    public GetUserResponse getUser(GetUserRequest request) {
        if (request.getAlias() == null) {
            throw new RuntimeException("[BadRequest] Missing the user alias");
        }

        try {
            User user = getNewUserDAO().get(request.getAlias());

            if (user != null) { return new GetUserResponse(user); }
            else { return new GetUserResponse("Get user request failed. Reason unknown"); }
        } catch (DataAccessException e) {
            e.printStackTrace();
            //TODO: Check this is the right error handling
            if (e.getMessage().startsWith("Item not found at PartitionKey (")) {
                throw new RuntimeException("[BadRequest] " + e.getMessage());
            } else {
                throw new RuntimeException("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    //TODO: migrate below to new Dynamo DAOs

    public AuthResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }

        // TODO: Generates dummy data. Replace with a real implementation.
//        List<User> users = getFakeData().getFakeUsers();
//        for (User user : users) {
//            getNewUserDAO().add(user);
//        }

        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new AuthResponse(user, authToken);
    }

    public AuthResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        } else if(request.getFirstName() == null) {
            throw new RuntimeException("[BadRequest] Missing a first name");
        } else if(request.getLastName() == null) {
            throw new RuntimeException("[BadRequest] Missing a last name");
        } else if(request.getImage() == null) {
            throw new RuntimeException("[BadRequest] Missing an image");
        }

        //TODO: check that user is not already registered

        String imageURL = S3.putImage(request.getUsername().substring(1), request.getImage());

        User toAdd = new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageURL);
        getNewUserDAO().add(toAdd);

        AuthToken token = generateNewAuthToken();

        //TODO: implement error checking

        return new AuthResponse(toAdd, token);
    }

    private AuthToken generateNewAuthToken() {
        //TODO: generate AuthToken
        return null;
    }

    public LogoutResponse logout(LogoutRequest request) {
        if (request.getToken() == null) {
            throw new RuntimeException("[BadRequest] Missing the token to destroy");
        }

        //TODO: remove authToken from DB
        return new LogoutResponse();
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }

    Database<User> getNewUserDAO() { return new DynamoDAOFactory().getUsersDAO(); }
}
