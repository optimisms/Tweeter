package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;

public class MainPresenter {
    public interface View {
        void logoutUser();
        void displayLogoutMessage();
        void clearLogoutMessage();
        void displayLogoutErrorMessage(String message);
    }

    private View mView;

    public MainPresenter(View inView) { mView = inView; }

    public void initiateLogout() {
        new UserService().logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver());
    }

    private class LogoutObserver implements UserService.LogoutObserver {
        @Override
        public void logoutSuccess() {
            //Clear user data (cached data).
            Cache.getInstance().clearCache();
            mView.clearLogoutMessage();
            mView.displayLogoutMessage();
            mView.logoutUser();
        }

        @Override
        public void logoutFailed(String message) {
            mView.clearLogoutMessage();
            mView.displayLogoutErrorMessage(message);
        }
    }
}
