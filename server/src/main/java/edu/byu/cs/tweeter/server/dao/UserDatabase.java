package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoDAO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

public abstract class UserDatabase extends DynamoDAO<User> {
    protected UserDatabase(DynamoDbEnhancedClient enhancedClient) {
        super(enhancedClient);
    }

    public abstract void addUserBatch(List<User> users);
}
