package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedService extends Service {
    public interface GetFeedObserver extends Observer {
        void getFeedSuccess(List<Status> statuses, boolean morePages);
    }

    public void getFeed(AuthToken authToken, User user, int pageSize, Status lastStatus, GetFeedObserver observer) {
        GetFeedTask getFeedTask = new GetFeedTask(authToken, user, pageSize, lastStatus, new GetFeedHandler(observer));
        executeTask(getFeedTask);
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    private class GetFeedHandler extends BackgroundTaskHandler<GetFeedObserver> {
        public GetFeedHandler(GetFeedObserver inObs) { super(inObs); }

        @Override
        protected void handleSuccessMessage(GetFeedObserver observer, Bundle data) {
            List<Status> statuses = (List<Status>) data.getSerializable(GetFeedTask.ITEMS_KEY);
            boolean hasMorePages = data.getBoolean(GetFeedTask.MORE_PAGES_KEY);
            observer.getFeedSuccess(statuses, hasMorePages);
        }

        @Override
        protected void handleFailureMessage(GetFeedObserver observer, String message) {
            observer.taskFailed("Failed to get feed: " + message);
        }

        @Override
        protected void handleExceptionMessage(GetFeedObserver observer, String message) {
            observer.taskFailed("Failed to get feed because of exception: " + message);
        }
    }
}
