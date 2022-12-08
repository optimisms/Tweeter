package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

public abstract class GetCountTask extends AuthenticatedTask {

    public static final String COUNT_KEY = "count";

    /**
     * The user whose count is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private final User targetUser;

    private int count;

    protected GetCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, messageHandler);
        this.targetUser = targetUser;
    }

    protected User getTargetUser() {
        return targetUser;
    }

    @Override
    protected void runTask() {
        try {
            CountRequest req = new CountRequest(getAuthToken(), targetUser);
            CountResponse resp = getCountResponse(req);

            if (resp.isSuccess()) {
                count = resp.getCount();
                sendSuccessMessage();
            } else {
                sendFailedMessage(resp.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(getLogTag(), "Failed to authenticate session", ex);
            ex.printStackTrace();
            sendExceptionMessage(ex);
        }
    }

    protected abstract CountResponse getCountResponse(CountRequest req) throws IOException, TweeterRemoteException;

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putInt(COUNT_KEY, count);
    }

    protected abstract String getLogTag();
}
