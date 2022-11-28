package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {
    private static final String LOG_TAG = "FollowTask";
    /**
     * The user that is being followed.
     */
    private final User followee;

    public FollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        try {
            FollowRequest req = new FollowRequest(Cache.getInstance().getCurrUser(), followee);
            FollowResponse resp = getServerFacade().follow(req, FollowService.FOLLOW_URL_PATH);

            if (resp.isSuccess()) {
                sendSuccessMessage();
            } else {
                sendFailedMessage(resp.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to follow user", ex);
            ex.printStackTrace();
            sendExceptionMessage(ex);
        }
    }

}
