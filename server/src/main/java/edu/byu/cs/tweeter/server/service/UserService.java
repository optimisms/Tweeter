package edu.byu.cs.tweeter.server.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

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
    //TODO: Check right error handling for all methods

    public GetUserResponse getUser(GetUserRequest request) {
        if (request.getAlias() == null) {
            throw new RuntimeException("[BadRequest] Missing the user alias");
        }

        try {
            User user = getNewUserDAO().get(request.getAlias(), null);

            return new GetUserResponse(user);
        } catch (DataAccessException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("Item not found at PartitionKey (")) {
                return new GetUserResponse(e.getMessage());
            } else {
                throw new RuntimeException("[Internal Server Error] " + e.getMessage());
            }
        }
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

//        try {
//            String imageURL = S3.putImage(request.getUsername().substring(1), request.getImage());

            //Consider passing salt by reference so it only has to return one object? Can you even do that in Java?
//            byte[][] hashResults = hashPassword(request.getPassword(), null);

//            User toAdd = new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageURL, hashResults[0], hashResults[1]);

//            getNewUserDAO().add(toAdd);
//            getNewUserDAO().add(new User("d", "a", "@isdf", "asdfasdf"));

//            AuthToken token = generateNewAuthToken();
//            getNewAuthTokenDAO().add(token);
//
//            return new AuthResponse(toAdd, token);
        //TODO: talk to a TA about what on earth is happening here to make it time out?????
            return new AuthResponse(new User("", "", "", ""), new AuthToken());
//        } catch (DataAccessException e) {
//            e.printStackTrace();
//            if (e.getMessage().startsWith("The username") || e.getMessage().equals("This authToken already exists.")) {
//                return new AuthResponse(e.getMessage());
////            } else if (e.getClass() == NoSuchAlgorithmException.class) {
////                throw new RuntimeException("[Internal Server Error] Password hashing failed. Please try again later.");
//            } else {
//                throw new RuntimeException("[Internal Server Error] " + e.getMessage());
//            }
//        }
    }

    private byte[][] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        //If called by login, salt is retrieved from DB and passed in
        //Otherwise, salt should be generated for register
        if (salt == null) {
            SecureRandom random = new SecureRandom();
            salt = new byte[16];
            random.nextBytes(salt);
        }

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);

        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        //No idea if this is the right way to do this??
        byte[][] toReturn = new byte[2][];
        toReturn[0] = hashedPassword;
        toReturn[1] = salt;
        return toReturn;
    }

    private AuthToken generateNewAuthToken() {
        //TODO: add datetime to generation
        return new AuthToken(UUID.randomUUID().toString());
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

    Database<User> getNewUserDAO() { return DynamoDAOFactory.getInstance().getUsersDAO(); }
    Database<AuthToken> getNewAuthTokenDAO() { return DynamoDAOFactory.getInstance().getAuthTokenDAO(); }
}
