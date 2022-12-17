package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

public abstract class FeedDatabase extends PagedDatabase<Status, Status> {
    protected FeedDatabase(DynamoDbEnhancedClient enhancedClient) {
        super(enhancedClient);
    }

    public abstract void batchWrite(List<String> followers, Status status);
}
