package edu.byu.cs.tweeter.server.dao.dynamo.DTO;

import edu.byu.cs.tweeter.server.dao.dynamo.NewFollowDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class FollowBean {
    public String follower_handle;
    public String followee_handle;


    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames= NewFollowDAO.INDEX_NAME)
    public String getFollower_handle() {
        return follower_handle;
    }

    public void setFollower_handle(String follower_handle) {
        this.follower_handle=follower_handle;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames=NewFollowDAO.INDEX_NAME)
    public String getFollowee_handle() {
        return followee_handle;
    }

    public void setFollowee_handle(String followee_handle) {
        this.followee_handle=followee_handle;
    }

    @Override
    public String toString() { return String.format("Relationship[%s, %s]", follower_handle, followee_handle); }
}