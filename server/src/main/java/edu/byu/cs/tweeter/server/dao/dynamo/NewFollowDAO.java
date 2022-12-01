package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.server.dao.Database;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

public class NewFollowDAO implements Database<Follow> {
    private final DynamoDbEnhancedClient enhancedClient;

    public NewFollowDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    //TODO: implement
    @Override
    public Follow get() {
        return null;
    }

    @Override
    public void add(Follow toAdd) {

    }

    @Override
    public void update(Follow toUpdate) {

    }

    @Override
    public void delete(Follow toDelete) {

    }
}
