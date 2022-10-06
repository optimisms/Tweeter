package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.StoryService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter {
    public interface View {
        void displayMessage(String message);
        void clearMessage();
        void setLoadingFooter();
        void addStatuses(List<Status> statuses);
        void startUserActivity(User user);
    }

    private static final int PAGE_SIZE = 10;

    private View mView;
    private Status lastStatus;
    private boolean hasMorePages;
    private boolean isLoading = false;

    public StoryPresenter(View inView) { mView = inView; }

    public boolean isLoading() { return isLoading; }
    public boolean hasMorePages() { return hasMorePages; }

    public void initiateGetStory(User user) {
        isLoading = true;
        mView.setLoadingFooter();

        new StoryService().getStory(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastStatus, new GetStoryObserver());
    }
    public void initiateGetUser(String username) {
        new UserService().getUser(Cache.getInstance().getCurrUserAuthToken(), username, new GetUserObserver());
    }

    public class GetStoryObserver implements Service.PagedObserver<Status> {
        @Override
        public void pagedTaskSuccess(List<Status> items, boolean morePages) {
            isLoading = false;
            mView.setLoadingFooter();

            lastStatus = (items.size() > 0) ? items.get(items.size() - 1) : null;
            hasMorePages = morePages;
            mView.addStatuses(items);
        }

        @Override
        public void taskFailed(String message) {
            isLoading = false;
            mView.setLoadingFooter();

            mView.clearMessage();
            mView.displayMessage(message);
        }
    }
    private class GetUserObserver implements UserService.GetUserObserver {
        @Override
        public void getUserSuccess(User user) {
            mView.displayMessage("Getting user's profile...");
            mView.startUserActivity(user);
        }

        @Override
        public void taskFailed(String message) {
            mView.clearMessage();
            mView.displayMessage(message);
        }
    }
}
