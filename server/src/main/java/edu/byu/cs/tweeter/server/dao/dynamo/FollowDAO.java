package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.FollowDatabase;
import edu.byu.cs.tweeter.server.dao.dynamo.DTO.FollowBean;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class FollowDAO extends FollowDatabase {
    private static final String TABLE_NAME = "tweeter_following";
    public static final String INDEX_NAME = "tweeter_followers_index";
    private static final String FOLLOWER_ALIAS_ATTR = "follower_alias";
    private static final String FOLLOWEE_ALIAS_ATTR = "followee_alias";

    public FollowDAO(DynamoDbEnhancedClient enhancedClient) {
        super(enhancedClient);
    }

    @Override
    public Follow get(String follower_alias, String followee_alias) throws DataAccessException {
        DynamoDbTable<FollowBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FollowBean.class));
        Key key = Key.builder().partitionValue(follower_alias).sortValue(followee_alias).build();

        FollowBean follow = table.getItem(key);

        if (follow == null) {
            throw new DataAccessException("Item not found at PartitionKey (" + FOLLOWER_ALIAS_ATTR + ":" + follower_alias + ") with SortKey (" + FOLLOWEE_ALIAS_ATTR + ":" + followee_alias + ")");
        } else {
            //TODO: create follow object to return (see TODO in getFollowers)
            //getFollower (GetUser)
            //getFollowee (GetUser)
            return new Follow();
        }
    }

    @Override
    public void add(Follow toAdd) throws DataAccessException {
        DynamoDbTable<FollowBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FollowBean.class));

        FollowBean newFollow = new FollowBean();
        newFollow.setFollower_alias(toAdd.getFollower().getAlias());
        newFollow.setFollowee_alias(toAdd.getFollowee().getAlias());

        try {
            get(newFollow.getFollower_alias(), newFollow.getFollowee_alias());
        } catch (DataAccessException e) {
            //If item does not exist, add it
            table.putItem(newFollow);
            return;
        }
        //If item does exist, throw exception
        throw new DataAccessException("User " + newFollow.getFollower_alias() + " is already following " + newFollow.getFollowee_alias());
    }

    @Override
    public void update(Follow toUpdate) {
        //TODO: figure out when you would use update for the Follow table
        //Right now, the only things I can think of are to add or delete, which of course already exist
        //Maybe if someone changes their username?
    }

    @Override
    public void delete(Follow toDelete) throws DataAccessException {
        String follower_alias = toDelete.getFollower().getAlias();
        String followee_alias = toDelete.getFollowee().getAlias();

        try {
            get(follower_alias, followee_alias);
        } catch (DataAccessException e) {
            //If item does not exist, throw exception
            throw new DataAccessException("User " + follower_alias + " is not following " + followee_alias);
        }
        //If item exists, delete it
        DynamoDbTable<FollowBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FollowBean.class));
        Key key = Key.builder()
                .partitionValue(follower_alias)
                .sortValue(followee_alias)
                .build();

        table.deleteItem(key);
    }

    private List<User> getFollowing(String follower_alias, int pageSize, String last_followee_alias) throws DataAccessException {
        DynamoDbTable<FollowBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FollowBean.class));
        Key key = Key.builder()
                .partitionValue(follower_alias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true);

        if (isNonEmptyString(last_followee_alias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FOLLOWER_ALIAS_ATTR, AttributeValue.builder().s(follower_alias).build());
            startKey.put(FOLLOWEE_ALIAS_ATTR, AttributeValue.builder().s(last_followee_alias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        List<FollowBean> beans = table.query(request).items().stream().limit(pageSize).collect(Collectors.toList());

        List<User> toReturn = new ArrayList<>();
        for (FollowBean curr : beans) {
            User followee = DynamoDAOFactory.getInstance().getUsersDAO().get(curr.getFollowee_alias(), null).makeSecureUser();
            toReturn.add(followee);
        }
        return toReturn;
    }

    private List<User> getFollowers(String followee_alias, int pageSize, String last_follower_alias) throws DataAccessException {
        DynamoDbIndex<FollowBean> index = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FollowBean.class)).index(INDEX_NAME);
        Key key = Key.builder().partitionValue(followee_alias).build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key)).limit(pageSize).scanIndexForward(true);

        if (isNonEmptyString(last_follower_alias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FOLLOWEE_ALIAS_ATTR, AttributeValue.builder().s(followee_alias).build());
            startKey.put(FOLLOWER_ALIAS_ATTR, AttributeValue.builder().s(last_follower_alias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        List<FollowBean> beans = new ArrayList<>();

        SdkIterable<Page<FollowBean>> results2 = index.query(request);
        PageIterable<FollowBean> pages = PageIterable.create(results2);
        pages.stream().limit(1).forEach(followersPage -> followersPage.items().forEach(f -> beans.add(f)));

        //TODO: change so that no DAO needs to access another DAO; store the User information in the follow table too
        List<User> toReturn = new ArrayList<>();
        for (FollowBean curr : beans) {
            User follower = DynamoDAOFactory.getInstance().getUsersDAO().get(curr.getFollower_alias(), null).makeSecureUser();
            toReturn.add(follower);
        }
        return toReturn;
    }

    @Override
    public List<String> getAllFollowers(String followee_alias) {
        DynamoDbIndex<FollowBean> index = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FollowBean.class)).index(INDEX_NAME);
        Key key = Key.builder().partitionValue(followee_alias).build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true);

//        if (isNonEmptyString(last_follower_alias)) {
//            Map<String, AttributeValue> startKey = new HashMap<>();
//            startKey.put(FOLLOWEE_ALIAS_ATTR, AttributeValue.builder().s(followee_alias).build());
//            startKey.put(FOLLOWER_ALIAS_ATTR, AttributeValue.builder().s(last_follower_alias).build());
//
//            requestBuilder.exclusiveStartKey(startKey);
//        }

        QueryEnhancedRequest request = requestBuilder.build();

        List<FollowBean> beans = new ArrayList<>();

        SdkIterable<Page<FollowBean>> results2 = index.query(request);
        PageIterable<FollowBean> pages = PageIterable.create(results2);
        pages.stream().limit(1).forEach(followersPage -> followersPage.items().forEach(f -> beans.add(f)));

        List<String> toReturn = new ArrayList<>();
        for (FollowBean curr : beans) {
            toReturn.add(curr.getFollower_alias());
        }
        return toReturn;
    }

    @Override
    public List<User> getPages(String partition_key, int pageSize, String sort_start_key, String taskType) throws DataAccessException {
        if (taskType.equals("getFollowers")) return getFollowers(partition_key, pageSize, sort_start_key);
        else { return getFollowing(partition_key, pageSize, sort_start_key); }
    }
}