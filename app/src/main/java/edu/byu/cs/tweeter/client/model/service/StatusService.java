package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class StatusService extends Service {
    public interface PostStatusObserver extends Observer {
        void postStatusSuccess();
    }

    public void postStatus(AuthToken authToken, Status newStatus, PostStatusObserver observer) {
        //TODO: make sure this commented line is safe to delete
        //Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
        PostStatusTask statusTask = new PostStatusTask(authToken, newStatus, new PostStatusHandler(observer));
        executeTask(statusTask);
    }

    private class PostStatusHandler extends Handler {
        PostStatusObserver mObserver;

        public PostStatusHandler(PostStatusObserver inObs) { mObserver = inObs; }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(PostStatusTask.SUCCESS_KEY);
            if (success) {
                //TODO: make sure this commented line is safe to delete
                //postingToast.cancel();
                //Toast.makeText(MainActivity.this, "Successfully Posted!", Toast.LENGTH_LONG).show();
                mObserver.postStatusSuccess();
            } else if (msg.getData().containsKey(PostStatusTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(PostStatusTask.MESSAGE_KEY);
                mObserver.taskFailed( "Failed to post status: " + message);
            } else if (msg.getData().containsKey(PostStatusTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(PostStatusTask.EXCEPTION_KEY);
                mObserver.taskFailed("Failed to post status because of exception: " + ex.getMessage());
            }
        }
    }
}
