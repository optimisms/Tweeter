package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

public abstract class FollowDatabase extends PagedDatabase<Follow, User> {
    protected FollowDatabase(DynamoDbEnhancedClient enhancedClient) {
        super(enhancedClient);
    }

    public abstract List<String> getAllFollowers(String followee_alias);
    public abstract void addFollowersBatch(List<String> followers, String followTarget);
}
