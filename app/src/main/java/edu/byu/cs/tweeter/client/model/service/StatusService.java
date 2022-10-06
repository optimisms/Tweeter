package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.handlers.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.presenter.MainPresenter.PostStatusObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class StatusService extends Service {
    public void postStatus(AuthToken authToken, Status newStatus, PostStatusObserver observer) {
        PostStatusTask statusTask = new PostStatusTask(authToken, newStatus, new PostStatusHandler(observer));
        executeTask(statusTask);
    }

    private class PostStatusHandler extends BackgroundTaskHandler<PostStatusObserver> {
        public PostStatusHandler(PostStatusObserver inObs) { super(inObs, "post status"); }

        @Override
        protected void handleSuccessMessage(PostStatusObserver observer, Bundle data) {
            observer.taskSuccess();
        }
    }
}
