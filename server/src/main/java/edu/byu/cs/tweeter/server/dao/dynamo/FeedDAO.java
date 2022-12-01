package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.Database;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

public class FeedDAO implements Database<Status> {
    private final DynamoDbEnhancedClient enhancedClient;

    public FeedDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    //TODO: implement
    @Override
    public Status get() {
        return null;
    }

    @Override
    public void add(Status toAdd) {

    }

    @Override
    public void update(Status toUpdate) {

    }

    @Override
    public void delete(Status toDelete) {

    }
}
