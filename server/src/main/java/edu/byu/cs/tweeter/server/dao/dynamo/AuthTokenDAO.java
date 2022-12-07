package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.dynamo.DTO.AuthTokenBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class AuthTokenDAO extends DynamoDAO<AuthToken> {
    private static final String TABLE_NAME = "tweeter_auth_tokens";
    private static final String AUTH_TOKEN_ATTR = "auth_token";

    public AuthTokenDAO(DynamoDbEnhancedClient enhancedClient) {
        super (enhancedClient);
    }

    @Override
    public AuthToken get(String auth_token, String sort) throws DataAccessException {
        //IGNORE sort

        DynamoDbTable<AuthTokenBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(AuthTokenBean.class));
        Key key = Key.builder().partitionValue(auth_token).build();

        AuthTokenBean tokenBean = table.getItem(key);

        if (tokenBean == null) {
            throw new DataAccessException("Item not found at PartitionKey (" + AUTH_TOKEN_ATTR + ":" + auth_token + ")");
        } else {
            return new AuthToken(tokenBean.getAuth_token(), tokenBean.getDatetime());
        }
    }

    @Override
    public void add(AuthToken toAdd) throws DataAccessException {
        DynamoDbTable<AuthTokenBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(AuthTokenBean.class));

        try {
            get(toAdd.getToken(), null);
        } catch (DataAccessException e) {
            //If item does not exist, add it
            AuthTokenBean newToken = new AuthTokenBean();
            newToken.setAuth_token(toAdd.getToken());
            newToken.setDatetime(toAdd.getDatetime());

            table.putItem(newToken);
            return;
        }
        //If item does exist, throw exception
        throw new DataAccessException("This authToken already exists.");
    }

    @Override
    public void update(AuthToken toUpdate) throws DataAccessException {
        DynamoDbTable<AuthTokenBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(AuthTokenBean.class));
        Key key = Key.builder().partitionValue(toUpdate.getToken()).build();

        AuthTokenBean token = table.getItem(key);
        if (token == null) {
            //If item does not exist, throw exception
            throw new DataAccessException("Item not found at PartitionKey (" + AUTH_TOKEN_ATTR + ":" + toUpdate.getToken() + ")");
        }

        //If item does exist, update it
        token.setAuth_token(toUpdate.getToken());
        token.setDatetime(toUpdate.getDatetime());

        table.putItem(token);
    }

    @Override
    public void delete(AuthToken toDelete) throws DataAccessException {
        String token = toDelete.getToken();

        try {
            get(token, null);
        } catch (DataAccessException e) {
            //If item does not exist, throw exception
            throw new DataAccessException("Item not found at PartitionKey (" + AUTH_TOKEN_ATTR + ":" + token + ")");
        }
        //If item exists, delete it
        DynamoDbTable<AuthTokenBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(AuthTokenBean.class));
        Key key = Key.builder()
                .partitionValue(token)
                .build();

        table.deleteItem(key);
    }
}
