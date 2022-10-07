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
import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.AbstractPresenters.Presenter;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter {
    //TODO: try to refactor some of the observers implementations out
    public interface MainView extends View {
        void logoutUser();
        void updateCounts();
        void updateFolloweeCount(int count);
        void updateFollowerCount(int count);
        void enableFollowButton();
        void updateFollowButtonState(boolean state);
        String getFollowMessage();
        String getUnfollowMessage();
    }

    //TODO: get with a TA to ask how to remove mView from child class without ruining everything

    private static final String LOG_TAG = "MainActivity";

    public MainPresenter(MainView inView) { super(inView); }

    public void initiateLogout() { new UserService().logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver()); }
    public void initiatePostStatus(String post) {
        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            new StatusService().postStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, new PostStatusObserver());
        } catch (ParseException ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            ((MainView) mView).displayMessage("Failed to post the status because of exception: " + ex.getMessage());
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

    public void initiateGetCounts(User user) { new FollowService().getCounts(Cache.getInstance().getCurrUserAuthToken(),
            user, new GetFollowersCountObserver(), new GetFollowingCountObserver()); }
    public void initiateFollow(User user) { new FollowService().follow(Cache.getInstance().getCurrUserAuthToken(), user, new FollowObserver()); }
    public void initiateUnfollow(User user) { new FollowService().unfollow(Cache.getInstance().getCurrUserAuthToken(), user, new UnfollowObserver()); }
    public void initiateIsFollower(User user) {
        new FollowService().isFollower(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(), user, new IsFollowerObserver());
    }

    private class LogoutObserver implements Service.NoDataReturnedObserver {
        @Override
        public void taskSuccess() {
            //Clear user data (cached data).
            Cache.getInstance().clearCache();
            ((MainView) mView).clearMessage();
            mView.displayMessage("Logging Out...");
            ((MainView) mView).logoutUser();
        }

        @Override
        public void taskFailed(String message) {
            mView.clearMessage();
            mView.displayMessage(message);
        }
    }
    public class PostStatusObserver implements Service.NoDataReturnedObserver {
        @Override
        public void taskSuccess() {
            mView.clearMessage();
            mView.displayMessage("Posting Status...");
        }

        @Override
        public void taskFailed(String message) {
            mView.clearMessage();
            mView.displayMessage(message);
        }
    }
    public class FollowObserver implements Service.FollowButtonObserver {
        @Override
        public void taskSuccess() {
            ((MainView) mView).updateCounts();
            ((MainView) mView).updateFollowButtonState(true);
            mView.displayMessage(((MainView) mView).getFollowMessage());
        }

        @Override
        public void taskFailed(String message) { mView.displayMessage(message); }

        @Override
        public void enableButton() { ((MainView) mView).enableFollowButton(); }
    }
    public class UnfollowObserver implements Service.FollowButtonObserver {
        @Override
        public void taskSuccess() {
            ((MainView) mView).updateCounts();
            ((MainView) mView).updateFollowButtonState(false);
            mView.displayMessage(((MainView) mView).getUnfollowMessage());
        }

        @Override
        public void taskFailed(String message) { mView.displayMessage(message); }

        @Override
        public void enableButton() { ((MainView) mView).enableFollowButton(); }
    }
    private class IsFollowerObserver implements FollowService.IsFollowerObserver {
        @Override
        public void isFollowerSuccess(boolean isFollower) {
            ((MainView) mView).updateFollowButtonState(isFollower);
        }

        @Override
        public void taskFailed(String message) {
            mView.displayMessage(message);
        }
    }
    public class GetFollowingCountObserver implements Service.GetCountObserver {
        @Override
        public void getCountSuccess(int count) { ((MainView) mView).updateFolloweeCount(count); }

        @Override
        public void taskFailed(String message) { mView.displayMessage(message); }
    }
    public class GetFollowersCountObserver implements Service.GetCountObserver {
        @Override
        public void getCountSuccess(int count) { ((MainView) mView).updateFollowerCount(count); }

        @Override
        public void taskFailed(String message) { mView.displayMessage(message); }
    }

}
