package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.Database;
import edu.byu.cs.tweeter.server.dao.dynamo.DTO.UserBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class UsersDAO implements Database<User> {
    private static final String TABLE_NAME = "tweeter_users";
    private static final String USER_ALIAS_ATTR = "user_alias";

    private final DynamoDbEnhancedClient enhancedClient;

    public UsersDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    @Override
    public User get(String user_alias, String sort) throws DataAccessException {
        //IGNORE sort

        DynamoDbTable<UserBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserBean.class));
        Key key = Key.builder().partitionValue(user_alias).build();

        UserBean user = table.getItem(key);

        if (user == null) {
            throw new DataAccessException("Item not found at PartitionKey (" + USER_ALIAS_ATTR + ":" + user_alias + ")");
        } else {
            return new User(user.getFirst_name(), user.getLast_name(), user.getUser_alias(), user.getImage_url(), user.getFollower_count(), user.getFollowing_count());
        }
    }

    @Override
    public void add(User toAdd) throws DataAccessException {
        DynamoDbTable<UserBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserBean.class));

        UserBean newUser = new UserBean();
        newUser.setUser_alias(toAdd.getAlias());
        try {
            get(newUser.getUser_alias(), null);
        } catch (DataAccessException e) {
            //If item does not exist, add it
            newUser.setFirst_name(toAdd.getFirstName());
            newUser.setLast_name(toAdd.getLastName());
            newUser.setImage_url(toAdd.getImageUrl());
            newUser.setFollower_count(toAdd.getFollowerCount());
            newUser.setFollowing_count(toAdd.getFollowingCount());
            newUser.setPassword(toAdd.getHashedPassword());
            newUser.setSalt(toAdd.getHashSalt());

            table.putItem(newUser);
            return;
        }
        //If item does exist, throw exception
        throw new DataAccessException("The username " + newUser.getUser_alias() + " is not available");
    }

    @Override
    public void update(User toUpdate) throws DataAccessException {
        DynamoDbTable<UserBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserBean.class));
        Key key = Key.builder().partitionValue(toUpdate.getAlias()).build();

        UserBean user = table.getItem(key);
        if (user == null) {
            //If item does not exist, throw exception
            throw new DataAccessException("Item not found at PartitionKey (" + USER_ALIAS_ATTR + ":" + toUpdate.getAlias() + ")");
        }

        //If item does exist, update it
        user.setFollower_count(toUpdate.getFollowerCount());
        user.setFollowing_count(toUpdate.getFollowingCount());

        table.putItem(user);
    }

    @Override
    public void delete(User toDelete) {
        //This would be necessary if we had a way to delete your account
    }
}
