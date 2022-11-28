package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthenticatedTask {
    private static final String LOG_TAG = "PostStatusTask";

    /**
     * The new status being sent. Contains all properties of the status,
     * including the identity of the user sending the status.
     */
    private final Status status;

    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(authToken, messageHandler);
        this.status = status;
    }

    @Override
    protected void runTask() {
        try {
            PostStatusRequest req = new PostStatusRequest(status);
            PostStatusResponse resp = getServerFacade().postStatus(req, StatusService.POST_STATUS_URL_PATH);

            if (resp.isSuccess()) {
                sendSuccessMessage();
            } else {
                sendFailedMessage(resp.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {

            Log.e(LOG_TAG, "Failed to logout of session", ex);
            sendExceptionMessage(ex);
        }
    }
}
