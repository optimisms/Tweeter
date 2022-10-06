package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> {

    public FollowersPresenter(PagedView<User> inView) { super(inView); }

    public void loadMoreFollowers(User user) {
        loadMoreItems();

        new FollowService().loadMoreFollowers(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new GetFollowersObserver());
    }
    public void initiateGetUser(String username) {
        new UserService().getUser(Cache.getInstance().getCurrUserAuthToken(), username, new GetUserObserver());
    }

    public class GetFollowersObserver implements Service.PagedObserver<User> {
        @Override
        public void pagedTaskSuccess(List<User> followers, boolean morePages) {
            isLoading = false;
            mView.setLoadingFooter();

            lastItem = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
            hasMorePages = morePages;
            mView.addItems(followers);
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
