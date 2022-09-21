package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.complete.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.complete.GetFollowingTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {
    public interface GetFollowingObserver {
        void getFollowingSuccess(List<User> followings, boolean morePages);
        void getFollowingFailed(String message);
    }

    public interface GetFollowersObserver {
        void getFollowersSuccess(List<User> followers, boolean morePages);
        void getFollowersFailed(String message);
    }

    public void loadMoreFollowing(AuthToken authToken, User user, int pageSize, User lastFollowee, GetFollowingObserver observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(authToken, user, pageSize, lastFollowee, new GetFollowingHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowingTask);
    }

    public void loadMoreFollowers(AuthToken authToken, User user, int pageSize, User lastFollower, GetFollowersObserver observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(authToken, user, pageSize, lastFollower, new GetFollowersHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }

    /**
     * Message handler (i.e., observer) for GetFollowingTask.
     */
    private class GetFollowingHandler extends Handler {
        private GetFollowingObserver observer;

        public GetFollowingHandler(GetFollowingObserver inObs) { observer = inObs; }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingTask.SUCCESS_KEY);
            if (success) {
                List<User> followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.FOLLOWEES_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
                observer.getFollowingSuccess(followees, hasMorePages);
            } else if (msg.getData().containsKey(GetFollowingTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingTask.MESSAGE_KEY);
                observer.getFollowingFailed("Failed to get following: " + message);
            } else if (msg.getData().containsKey(GetFollowingTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingTask.EXCEPTION_KEY);
                observer.getFollowingFailed("Failed to get following because of exception: " + ex.getMessage());
            }
        }
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private class GetFollowersHandler extends Handler {
        private GetFollowersObserver observer;

        public GetFollowersHandler(GetFollowersObserver inObs) { observer = inObs; }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersTask.SUCCESS_KEY);
            if (success) {
                List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.FOLLOWERS_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
                observer.getFollowersSuccess(followers, hasMorePages);
            } else if (msg.getData().containsKey(GetFollowersTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersTask.MESSAGE_KEY);
                observer.getFollowersFailed("Failed to get followers: " + message);
            } else if (msg.getData().containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersTask.EXCEPTION_KEY);
                observer.getFollowersFailed("Failed to get followers because of exception: " + ex.getMessage());
            }
        }
    }
}
