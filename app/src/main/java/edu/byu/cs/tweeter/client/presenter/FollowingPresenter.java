package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter {
    public interface View {
        void displayMessage(String message);
        void clearMessage();
        void setLoadingFooter();
        void addFollowees(List<User> followees);
        void startUserActivity(User user);
    }

    private static final int PAGE_SIZE = 10;

    private View mView;
    private User lastFollowee;
    private boolean hasMorePages;
    private boolean isLoading = false;

    public FollowingPresenter(View inView) { mView = inView; }

    public boolean isLoading() { return isLoading; }
    public boolean hasMorePages() { return hasMorePages; }

    public void loadMoreItems(User user) {
        isLoading = true;
        mView.setLoadingFooter();

        new FollowService().loadMoreFollowing(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastFollowee, new GetFollowingObserver());
    }
    public void initiateGetUser(String username) {
        new UserService().getUser(Cache.getInstance().getCurrUserAuthToken(), username, new FollowingPresenter.GetUserObserver());
    }

    private class GetFollowingObserver implements FollowService.GetFollowingObserver {
        @Override
        public void getFollowingSuccess(List<User> followees, boolean morePages) {
            isLoading = false;
            mView.setLoadingFooter();

            lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;
            hasMorePages = morePages;
            mView.addFollowees(followees);
        }

        @Override
        public void getFollowingFailed(String message) {
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
        public void getUserFailed(String message) {
            mView.clearMessage();
            mView.displayMessage(message);
        }
    }
}
