package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.PagedTaskData;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.handlers.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.handlers.FollowButtonHandler;
import edu.byu.cs.tweeter.client.model.service.handlers.GetCountHandler;
import edu.byu.cs.tweeter.client.model.service.handlers.PagedTaskHandler;
import edu.byu.cs.tweeter.client.presenter.MainPresenter.FollowObserver;
import edu.byu.cs.tweeter.client.presenter.MainPresenter.GetFollowersCountObserver;
import edu.byu.cs.tweeter.client.presenter.MainPresenter.GetFollowingCountObserver;
import edu.byu.cs.tweeter.client.presenter.MainPresenter.UnfollowObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends Service {
    public static final String FOLLOW_URL_PATH = "follow/";
    public static final String UNFOLLOW_URL_PATH = "follow/unfollow/";
    public static final String IS_FOLLOWER_URL_PATH = "follow/is_follower/";
    public static final String GET_FOLLOWERS_URL_PATH = "get/followers/";
    public static final String GET_FOLLOWERS_COUNT_URL_PATH = "get/followers/count/";
    public static final String GET_FOLLOWING_URL_PATH = "get/following/";
    public static final String GET_FOLLOWING_COUNT_URL_PATH = "get/following/count/";

    public interface IsFollowerObserver extends Observer {
        void isFollowerSuccess(boolean isFollower);
    }

    public void loadMoreFollowing(PagedTaskData<User> data) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(data, new GetFollowingHandler(data.getObserver()));
        executeTask(getFollowingTask);
    }
    public void loadMoreFollowers(PagedTaskData<User> data) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(data, new GetFollowersHandler(data.getObserver()));
        executeTask(getFollowersTask);
    }
    public void getCounts(AuthToken authToken, User user, GetFollowersCountObserver followersObserver, GetFollowingCountObserver followingObserver) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(authToken, user, new GetFollowersCountHandler(followersObserver));
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(authToken, user, new GetFollowingCountHandler(followingObserver));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<BackgroundTask> taskList = new ArrayList<>(Arrays.asList(followersCountTask, followingCountTask));

        executeTasks(executor, taskList);
    }
    public void follow(AuthToken authToken, User user, FollowObserver observer) {
        FollowTask followTask = new FollowTask(authToken, user, new FollowHandler(observer));
        executeTask(followTask);
    }
    public void unfollow(AuthToken authToken, User user, UnfollowObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(authToken, user, new UnfollowHandler(observer));
        executeTask(unfollowTask);
    }
    public void isFollower(AuthToken authToken, User user, User selectedUser, IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken, user, selectedUser, new IsFollowerHandler(observer));
        executeTask(isFollowerTask);
    }

    /**
     * Message handler (i.e., observer) for FollowTask.
     */
    private class FollowHandler extends FollowButtonHandler {
        public FollowHandler(FollowObserver inObs) { super(inObs, "follow"); }
    }

    /**
     * Message handler (i.e., observer) for UnfollowTask.
     */
    private class UnfollowHandler extends FollowButtonHandler {
        public UnfollowHandler(UnfollowObserver inObs) { super(inObs, "unfollow"); }
    }

    /**
     * Message handler (i.e., observer) for IsFollowerTask.
     */
    private class IsFollowerHandler extends BackgroundTaskHandler<IsFollowerObserver> {
        public IsFollowerHandler(IsFollowerObserver inObs) { super(inObs, "determine following relationship"); }

        @Override
        protected void handleSuccessMessage(IsFollowerObserver observer, Bundle data) {
            boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            observer.isFollowerSuccess(isFollower);
        }
    }

    /**
     * Message handler (i.e., observer) for GetFollowingTask.
     */
    private class GetFollowingHandler extends PagedTaskHandler<Service.PagedObserver<User>, User> {
        public GetFollowingHandler(Service.PagedObserver<User> inObs) { super(inObs, "get following"); }
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private class GetFollowersHandler extends PagedTaskHandler<Service.PagedObserver<User>, User> {
        public GetFollowersHandler(Service.PagedObserver<User> inObs) { super(inObs, "get followers"); }
    }

    /**
     * Message handler (i.e., observer) for GetFollowingCountTask
     */
    private class GetFollowingCountHandler extends GetCountHandler {
        public GetFollowingCountHandler(GetFollowingCountObserver inObs) { super(inObs, "get following count"); }
    }

    /**
     * Message handler (i.e., observer) for GetFollowingCountTask
     */
    private class GetFollowersCountHandler extends GetCountHandler {
        public GetFollowersCountHandler(GetFollowersCountObserver inObs) { super(inObs, "get followers count:"); }
    }


}
