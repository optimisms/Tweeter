package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class StatusService extends Service {
    public interface PostStatusObserver extends Observer {
        void postStatusSuccess();
    }

    public void postStatus(AuthToken authToken, Status newStatus, PostStatusObserver observer) {
        PostStatusTask statusTask = new PostStatusTask(authToken, newStatus, new PostStatusHandler(observer));
        executeTask(statusTask);
    }

    private class PostStatusHandler extends BackgroundTaskHandler<PostStatusObserver> {
        public PostStatusHandler(PostStatusObserver inObs) { super(inObs); }

        @Override
        protected void handleSuccessMessage(PostStatusObserver observer, Bundle data) {
            observer.postStatusSuccess();
        }

        @Override
        protected void handleFailureMessage(PostStatusObserver observer, String message) {
            observer.taskFailed( "Failed to post status: " + message);
        }

        @Override
        protected void handleExceptionMessage(PostStatusObserver observer, String message) {
            observer.taskFailed("Failed to post status because of exception: " + message);
        }
    }
}
