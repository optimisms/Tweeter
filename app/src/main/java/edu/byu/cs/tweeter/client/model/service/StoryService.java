package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter.GetStoryObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryService extends Service {
    public void getStory(AuthToken authToken, User user, int pageSize, Status lastStatus, GetStoryObserver observer) {
        GetStoryTask getStoryTask = new GetStoryTask(authToken, user, pageSize, lastStatus, new GetStoryHandler(observer));
        executeTask(getStoryTask);
    }


    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private class GetStoryHandler extends BackgroundTaskHandler<GetStoryObserver> {
        public GetStoryHandler(GetStoryObserver inObs) { super(inObs); }

        @Override
        protected void handleSuccessMessage(GetStoryObserver observer, Bundle data) {
            List<Status> statuses = (List<Status>) data.getSerializable(GetStoryTask.ITEMS_KEY);
            boolean hasMorePages = data.getBoolean(GetStoryTask.MORE_PAGES_KEY);
            observer.pagedTaskSuccess(statuses, hasMorePages);
        }

        @Override
        protected void handleFailureMessage(GetStoryObserver observer, String message) {
            observer.taskFailed("Failed to get story: " + message);
        }

        @Override
        protected void handleExceptionMessage(GetStoryObserver observer, String message) {
            observer.taskFailed("Failed to get story because of exception: " + message);
        }
    }
}
