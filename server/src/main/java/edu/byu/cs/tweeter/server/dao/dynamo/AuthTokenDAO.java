package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.Database;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

public class AuthTokenDAO implements Database<AuthToken> {
    private final DynamoDbEnhancedClient enhancedClient;

    public AuthTokenDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    //TODO: implement
    @Override
    public AuthToken get(String partition, String sort) throws DataAccessException {
        return null;
    }

    @Override
    public void add(AuthToken toAdd) {

    }

    @Override
    public void update(AuthToken toUpdate) {

    }

    @Override
    public void delete(AuthToken toDelete) {

    }
}
