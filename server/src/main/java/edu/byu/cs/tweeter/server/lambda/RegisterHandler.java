package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.server.service.UserService;

/**
 * An AWS lambda function that registers a user and returns the user object and an auth code for
 * a successful login.
 */
public class RegisterHandler implements RequestHandler<RegisterRequest, AuthResponse> {
    @Override
    public AuthResponse handleRequest(RegisterRequest registerRequest, Context context) {
        return new UserService().register(registerRequest);
    }
}