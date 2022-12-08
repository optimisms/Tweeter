package edu.byu.cs.tweeter.server.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import edu.byu.cs.tweeter.server.dao.dynamo.S3;
import edu.byu.cs.tweeter.server.dao.factory.DAOFactory;

public class UserService {
    DAOFactory factory;
    public UserService(DAOFactory factory) {
        this.factory = factory;
    }

    public GetUserResponse getUser(GetUserRequest request) {
        try {
            if (request.getAlias() == null) {
                throw new RuntimeException("[BadRequest] Missing the user alias");
            } else if (request.getToken() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have an authToken");
            } else if (!AuthService.tokenIsValid(request.getToken())) {
                throw new RuntimeException("[BadRequest] Request needs to have a valid authToken");
            }

            User user = getUserDAO().get(request.getAlias(), null).makeSecureUser();

            return new GetUserResponse(user);
        } catch (DataAccessException | RuntimeException | ParseException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("[BadRequest")) {
                return new GetUserResponse(e.getMessage());
            } else {
                return new GetUserResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    public AuthResponse register(RegisterRequest request) {
        try {
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

            String imageURL = S3.putImage(request.getUsername().substring(1), request.getImage());

            //Consider passing salt by reference so it only has to return one object? Can you even do that in Java?
            byte[][] hashResults = hashPassword(request.getPassword(), null);

            User toAdd = new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageURL, hashResults[0], hashResults[1]);

            getUserDAO().add(toAdd);

            AuthToken token = generateNewAuthToken();
            getAuthTokenDAO().add(token);

            return new AuthResponse(toAdd.makeSecureUser(), token);
            //TODO: ask TAs if I should be catching any and all exceptions or just specific types
        } catch (Exception e) {//DataAccessException | RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("[BadRequest]")) {
                return new AuthResponse(e.getMessage());
            } else {
                return new AuthResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    public AuthResponse login(LoginRequest request) {
        try{
            if(request.getUsername() == null) {
                throw new RuntimeException("[BadRequest] Missing a username");
            } else if(request.getPassword() == null) {
                throw new RuntimeException("[BadRequest] Missing a password");
            }

            User registered = getUserDAO().get(request.getUsername(), null);

            byte[][] hashResults = hashPassword(request.getPassword(), registered.getHashSalt());

            if (!Arrays.equals(hashResults[0], registered.getHashedPassword())) {
                return new AuthResponse("[BadRequest] Incorrect password; expected " + Arrays.toString(registered.getHashedPassword()) + " but got " + Arrays.toString(hashResults[0]));
            }

            AuthToken token = generateNewAuthToken();
            getAuthTokenDAO().add(token);

            User toReturn = registered.makeSecureUser();

            return new AuthResponse(toReturn, token);
        } catch (Exception e) {//DataAccessException | RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("[BadRequest]")) {
                return new AuthResponse(e.getMessage());
            } else {
                return new AuthResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    public LogoutResponse logout(LogoutRequest request) {
        try {
            if (request.getToken() == null) {
                throw new RuntimeException("[BadRequest] Missing the token to destroy");
            }

            getAuthTokenDAO().delete(request.getToken());
            return new LogoutResponse();
        } catch (DataAccessException | RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("[BadRequest")) {
                return new LogoutResponse(e.getMessage());
            } else {
                return new LogoutResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    private byte[][] hashPassword(String password, byte[] salt) throws RuntimeException {
        //If called by login, salt is retrieved from DB and passed in
        //Otherwise, salt should be generated for register
        if (salt == null) {
            SecureRandom random = new SecureRandom();
            salt = new byte[16];
            random.nextBytes(salt);
        }

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Password hashing failed. Please try again later.");
        }
        md.update(salt);

        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        //No idea if this is the right way to do this??
        byte[][] toReturn = new byte[2][];
        toReturn[0] = hashedPassword;
        toReturn[1] = salt;
        return toReturn;
    }

    private AuthToken generateNewAuthToken() {
        String token = UUID.randomUUID().toString();
        Date now = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        return new AuthToken(token, df.format(now));
    }

    Database<User> getUserDAO() { return factory.getUsersDAO(); }
    Database<AuthToken> getAuthTokenDAO() { return factory.getAuthTokenDAO(); }
}
