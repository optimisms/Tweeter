package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.server.dao.Database;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

public abstract class DynamoDAO<T> implements Database<T> {
    protected final DynamoDbEnhancedClient enhancedClient;

    protected DynamoDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }
}
