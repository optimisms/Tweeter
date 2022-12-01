package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class OldFollowDAO {
    private static final String TABLE_NAME = "tweeter_following";
    public static final String INDEX_NAME = "tweeter_followers_index";
    private static final String FOLLOWER_HANDLE_ATTR = "follower_handle";
    private static final String FOLLOWER_NAME_ATTR = "follower_name";
    private static final String FOLLOWEE_HANDLE_ATTR = "followee_handle";
    private static final String FOLLOWEE_NAME_ATTR = "followee_name";

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder().region(Region.US_WEST_2).build();
    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public Relationship getRelationship(String follower_handle, String followee_handle) throws DataAccessException {
        DynamoDbTable<Relationship> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(Relationship.class));
        Key key = Key.builder().partitionValue(follower_handle).sortValue(followee_handle).build();

        Relationship relationship = table.getItem(key);

        if (relationship == null) {
            throw new DataAccessException("Item not found at PrimaryKey (" + FOLLOWER_HANDLE_ATTR + ":" + follower_handle + ") with SortKey (" + FOLLOWEE_HANDLE_ATTR + ":" + followee_handle + ")");
        } else {
            return relationship;
        }
    }

    public void addFollowerRelationship(String follower_handle, String follower_name, String followee_handle, String followee_name) {
        DynamoDbTable<Relationship> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(Relationship.class));

        Relationship newRelationship = new Relationship();
        newRelationship.setFollower_handle(follower_handle);
        newRelationship.setFollowee_handle(followee_handle);
        newRelationship.setFollower_name(follower_name);
        newRelationship.setFollowee_name(followee_name);
        table.putItem(newRelationship);
    }

    public void updateFollowerRelationship(String follower_handle, String followee_handle, String new_follower_name, String new_followee_name) {
        DynamoDbTable<Relationship> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(Relationship.class));
        Key key = Key.builder().partitionValue(follower_handle).sortValue(followee_handle).build();

        Relationship relationship = table.getItem(key);
        relationship.setFollower_name(new_follower_name);
        relationship.setFollowee_name(new_followee_name);
        table.updateItem(relationship);
    }

    public void deleteFollowerRelationship(String follower_handle, String followee_handle) {
        DynamoDbTable<Relationship> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(Relationship.class));
        Key key = Key.builder().partitionValue(follower_handle).sortValue(followee_handle).build();
        table.deleteItem(key);
    }

    public List<Relationship> getFollowing(String follower_handle, int pageSize, String last_followee_handle) {
        DynamoDbTable<Relationship> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(Relationship.class));
        Key key = Key.builder()
                .partitionValue(follower_handle)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true);

        if (isNonEmptyString(last_followee_handle)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FOLLOWER_HANDLE_ATTR, AttributeValue.builder().s(follower_handle).build());
            startKey.put(FOLLOWEE_HANDLE_ATTR, AttributeValue.builder().s(last_followee_handle).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        return table.query(request).items().stream().limit(pageSize).collect(Collectors.toList());
    }

    public List<Relationship> getFollowers(String followee_handle, int pageSize, String last_follower_handle) {
        DynamoDbIndex<Relationship> index = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(Relationship.class)).index(INDEX_NAME);
        Key key = Key.builder().partitionValue(followee_handle).build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key)).limit(pageSize).scanIndexForward(false);

        if (isNonEmptyString(last_follower_handle)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FOLLOWEE_HANDLE_ATTR, AttributeValue.builder().s(followee_handle).build());
            startKey.put(FOLLOWER_HANDLE_ATTR, AttributeValue.builder().s(last_follower_handle).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        List<Relationship> followers = new ArrayList<>();

        SdkIterable<Page<Relationship>> results2 = index.query(request);
        PageIterable<Relationship> pages = PageIterable.create(results2);
        pages.stream().limit(1).forEach(followersPage -> followersPage.items().forEach(f -> followers.add(f)));

        return followers;
    }
}
