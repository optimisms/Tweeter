package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.Database;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

public class UsersDAO implements Database<User> {
    private final DynamoDbEnhancedClient enhancedClient;

    public UsersDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    //TODO: implement
    @Override
    public User get() {
        return null;
    }

    @Override
    public void add(User toAdd) {

    }

    @Override
    public void update(User toUpdate) {

    }

    @Override
    public void delete(User toDelete) {

    }
}
