package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter {
    public interface View {
        void logoutUser();
        void displayLogoutMessage();
        void clearLogoutMessage();
        void displayLogoutErrorMessage(String message);
        void displayPostingMessage();
        void clearPostingMessage();
        void displayPostingErrorMessage(String message);
        void updateFolloweeCount(int count);
        void updateFollowerCount(int count);

        void displayErrorMessage(String message);
    }

    private static final String LOG_TAG = "MainActivity";

    private View mView;

    public MainPresenter(View inView) { mView = inView; }

    public void initiateLogout() { new UserService().logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver()); }
    public void initiatePostStatus(String post) {
        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            new StatusService().postStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, new PostStatusObserver());
        } catch (ParseException ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            mView.displayPostingErrorMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }
    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }
    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }
    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public void initiateGetCounts(User user) {
        new FollowService().getCounts(Cache.getInstance().getCurrUserAuthToken(), user, new GetFollowersCountObserver(), new GetFollowingCountObserver());
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
    private class PostStatusObserver implements StatusService.PostStatusObserver {
        @Override
        public void postStatusSuccess() {
            mView.clearPostingMessage();
            mView.displayPostingMessage();
        }

        @Override
        public void postStatusFailed(String message) {
            mView.clearPostingMessage();
            mView.displayLogoutErrorMessage(message);
        }
    }
    private class GetFollowingCountObserver implements FollowService.GetFollowingCountObserver {
        @Override
        public void getFollowingCountSuccess(int count) { mView.updateFolloweeCount(count); }

        @Override
        public void getFollowingCountFailed(String message) { mView.displayErrorMessage(message); }
    }
    private class GetFollowersCountObserver implements FollowService.GetFollowersCountObserver {
        @Override
        public void getFollowersCountSuccess(int count) { mView.updateFollowerCount(count); }

        @Override
        public void getFollowersCountFailed(String message) { mView.displayErrorMessage(message); }
    }

}
