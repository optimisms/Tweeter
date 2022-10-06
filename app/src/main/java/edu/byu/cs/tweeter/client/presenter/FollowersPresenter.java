package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter {
    public interface View {
        void displayMessage(String message);
        void clearMessage();
        void setLoadingFooter();
        void addFollowers(List<User> followers);
        void startUserActivity(User user);
    }

    private static final int PAGE_SIZE = 10;

    private View mView;
    private User lastFollower;
    private boolean hasMorePages;
    private boolean isLoading = false;

    public FollowersPresenter(View inView) { mView = inView; }

    public boolean isLoading() { return isLoading; }
    public boolean hasMorePages() { return hasMorePages; }

    public void loadMoreFollowers(User user) {
        isLoading = true;
        mView.setLoadingFooter();

        new FollowService().loadMoreFollowers(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastFollower, new GetFollowersObserver());
    }
    public void initiateGetUser(String username) {
        new UserService().getUser(Cache.getInstance().getCurrUserAuthToken(), username, new GetUserObserver());
    }

    public class GetFollowersObserver implements Service.PagedObserver<User> {
        @Override
        public void pagedTaskSuccess(List<User> followers, boolean morePages) {
            isLoading = false;
            mView.setLoadingFooter();

            lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
            hasMorePages = morePages;
            mView.addFollowers(followers);
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
