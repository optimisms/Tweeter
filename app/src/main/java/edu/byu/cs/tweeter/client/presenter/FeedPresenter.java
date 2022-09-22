package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter {
    public interface View {
        void displayMessage(String message);
        void clearMessage();
        void startUserActivity(User user);
    }

    private View mView;

    public FeedPresenter(View inView) { mView = inView; }

    public void initiateGetUser(String username) {
        new UserService().getUser(Cache.getInstance().getCurrUserAuthToken(), username, new GetUserObserver());
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
