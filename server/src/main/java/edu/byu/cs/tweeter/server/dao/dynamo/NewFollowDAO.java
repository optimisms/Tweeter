package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.Database;
import edu.byu.cs.tweeter.server.dao.dynamo.DTO.FollowBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class NewFollowDAO implements Database<Follow> {
    private static final String TABLE_NAME = "tweeter_following";
    public static final String INDEX_NAME = "tweeter_followers_index";
    private static final String FOLLOWER_HANDLE_ATTR = "follower_handle";
    private static final String FOLLOWEE_HANDLE_ATTR = "followee_handle";

    private final DynamoDbEnhancedClient enhancedClient;

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public NewFollowDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    //TODO: implement

    @Override
    public Follow get(String partition) {
        return null;
    }

    @Override
    public Follow get(String follower_handle, String followee_handle) throws DataAccessException {
        DynamoDbTable<FollowBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FollowBean.class));
        Key key = Key.builder().partitionValue(follower_handle).sortValue(followee_handle).build();

        FollowBean follow = table.getItem(key);

        if (follow == null) {
            throw new DataAccessException("Item not found at PartitionKey (" + FOLLOWER_HANDLE_ATTR + ":" + follower_handle + ") with SortKey (" + FOLLOWEE_HANDLE_ATTR + ":" + followee_handle + ")");
        } else {
            //TODO: create follow object to return
            //getFollower (GetUser)
            //getFollowee (GetUser)
            return new Follow();
        }
    }

    //TODO: add paginated get functions

//    public List<FollowBean> getFollowing(String follower_handle, int pageSize, String last_followee_handle) {
//        DynamoDbTable<FollowBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FollowBean.class));
//        Key key = Key.builder()
//                .partitionValue(follower_handle)
//                .build();
//
//        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
//                .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true);
//
//        if (isNonEmptyString(last_followee_handle)) {
//            Map<String, AttributeValue> startKey = new HashMap<>();
//            startKey.put(FOLLOWER_HANDLE_ATTR, AttributeValue.builder().s(follower_handle).build());
//            startKey.put(FOLLOWEE_HANDLE_ATTR, AttributeValue.builder().s(last_followee_handle).build());
//
//            requestBuilder.exclusiveStartKey(startKey);
//        }
//
//        QueryEnhancedRequest request = requestBuilder.build();
//
//        return table.query(request).items().stream().limit(pageSize).collect(Collectors.toList());
//    }
//
//    public List<FollowBean> getFollowers(String followee_handle, int pageSize, String last_follower_handle) {
//        DynamoDbIndex<FollowBean> index = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FollowBean.class)).index(INDEX_NAME);
//        Key key = Key.builder().partitionValue(followee_handle).build();
//
//        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
//                .queryConditional(QueryConditional.keyEqualTo(key)).limit(pageSize).scanIndexForward(false);
//
//        if (isNonEmptyString(last_follower_handle)) {
//            Map<String, AttributeValue> startKey = new HashMap<>();
//            startKey.put(FOLLOWEE_HANDLE_ATTR, AttributeValue.builder().s(followee_handle).build());
//            startKey.put(FOLLOWER_HANDLE_ATTR, AttributeValue.builder().s(last_follower_handle).build());
//
//            requestBuilder.exclusiveStartKey(startKey);
//        }
//
//        QueryEnhancedRequest request = requestBuilder.build();
//
//        List<FollowBean> followers = new ArrayList<>();
//
//        SdkIterable<Page<FollowBean>> results2 = index.query(request);
//        PageIterable<FollowBean> pages = PageIterable.create(results2);
//        pages.stream().limit(1).forEach(followersPage -> followersPage.items().forEach(f -> followers.add(f)));
//
//        return followers;
//    }

    @Override
    public void add(Follow toAdd) throws DataAccessException {
        DynamoDbTable<FollowBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FollowBean.class));

        FollowBean newFollow = new FollowBean();
        newFollow.setFollower_alias(toAdd.getFollower().getAlias());
        newFollow.setFollowee_alias(toAdd.getFollowee().getAlias());

        try {
            get(newFollow.follower_alias, newFollow.followee_alias);
        } catch (DataAccessException e) {
            //If item does not exist, add it
            table.putItem(newFollow);
            return;
        }
        //If item does exist, throw exception
        throw new DataAccessException("User " + newFollow.follower_alias + " is already following " + newFollow.followee_alias);
    }

    @Override
    public void update(Follow toUpdate) {
        //TODO: figure out when you would use update for the Follow table
        //Right now, the only things I can think of are to add or delete, which of course already exist
        //Maybe if someone changes their username?

//        DynamoDbTable<FollowBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FollowBean.class));
//        Key key = Key.builder().partitionValue(follower_handle).sortValue(followee_handle).build();
//
//        FollowBean follow = table.getItem(key);
//        follow.setFollower_name(new_follower_name);
//        follow.setFollowee_name(new_followee_name);
//        table.updateItem(follow);
    }

    @Override
    public void delete(Follow toDelete) throws DataAccessException {
        DynamoDbTable<FollowBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FollowBean.class));
        Key key = Key.builder()
                .partitionValue(toDelete.getFollower().getAlias())
                .sortValue(toDelete.getFollowee().getAlias())
                .build();

        try {
            get(toDelete.getFollower().getAlias(), toDelete.getFollowee().getAlias());
        } catch (DataAccessException e) {
            //If item does not exist, throw exception
            throw new DataAccessException("User " + toDelete.getFollower().getAlias() + " is not following " + toDelete.getFollowee().getAlias());
        }
        //If item exists, delete it
        table.deleteItem(key);
    }
}
