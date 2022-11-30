package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.handlers.NoDataReturnedHandler;
import edu.byu.cs.tweeter.client.presenter.MainPresenter.PostStatusObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class StatusService extends Service {
    public static final String POST_STATUS_URL_PATH = "post_status/";

    public void postStatus(AuthToken authToken, Status newStatus, PostStatusObserver observer) {
        PostStatusTask statusTask = new PostStatusTask(authToken, newStatus, new PostStatusHandler(observer));
        executeTask(statusTask);
    }

    private class PostStatusHandler extends NoDataReturnedHandler {
        public PostStatusHandler(PostStatusObserver inObs) { super(inObs, "post status"); }
    }
}
