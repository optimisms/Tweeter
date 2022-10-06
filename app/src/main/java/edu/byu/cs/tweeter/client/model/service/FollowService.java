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
import edu.byu.cs.tweeter.client.presenter.FollowersPresenter;
import edu.byu.cs.tweeter.client.presenter.FollowingPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends Service {
    public interface FollowObserver extends FollowButtonObserver {
        void followSuccess();
    }
    public interface UnfollowObserver extends FollowButtonObserver {
        void unfollowSuccess();
    }
    public interface IsFollowerObserver extends Observer {
        void isFollowerSuccess(boolean isFollower);
    }
    public interface GetFollowingCountObserver extends Observer {
        void getFollowingCountSuccess(int count);
    }
    public interface GetFollowersCountObserver extends Observer {
        void getFollowersCountSuccess(int count);
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

    private class FollowHandler extends BackgroundTaskHandler<FollowObserver> {
        public FollowHandler(FollowObserver inObs) { super(inObs, "follow"); }

        //TODO: explore creating a super class with a template method to call enableButton()
        // Alternatively, could create an abstract super class for the two, and then an if statement
        // for if(T extends __abstract super__) { enableButtons() }

        @Override
        protected void handleSuccessMessage(FollowObserver observer, Bundle data) {
            observer.followSuccess();
            observer.enableButton();
        }
    }

    private class UnfollowHandler extends BackgroundTaskHandler<UnfollowObserver> {
        public UnfollowHandler(UnfollowObserver inObs) { super(inObs, "unfollow"); }

        @Override
        protected void handleSuccessMessage(UnfollowObserver observer, Bundle data) {
            observer.unfollowSuccess();
            observer.enableButton();
        }
    }

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
    private class GetFollowingHandler extends PagedTaskHandler<FollowingPresenter.PagedObserver, User> {
        public GetFollowingHandler(FollowingPresenter.PagedObserver inObs) { super(inObs, "get following"); }
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private class GetFollowersHandler extends PagedTaskHandler<FollowersPresenter.PagedObserver, User> {
        public GetFollowersHandler(FollowersPresenter.PagedObserver inObs) { super(inObs, "get followers"); }
    }

    /**
     * Message handler (i.e., observer) for GetFollowingCountTask
     */
    private class GetFollowingCountHandler extends BackgroundTaskHandler<GetFollowingCountObserver> {
        public GetFollowingCountHandler(GetFollowingCountObserver inObs) { super(inObs, "get following count"); }

        @Override
        protected void handleSuccessMessage(GetFollowingCountObserver observer, Bundle data) {
            int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
            observer.getFollowingCountSuccess(count);
        }
    }

    /**
     * Message handler (i.e., observer) for GetFollowingCountTask
     */
    private class GetFollowersCountHandler extends BackgroundTaskHandler<GetFollowersCountObserver> {
        public GetFollowersCountHandler(GetFollowersCountObserver inObs) { super(inObs, "get followers count:"); }

        @Override
        protected void handleSuccessMessage(GetFollowersCountObserver observer, Bundle data) {
            int count = data.getInt(GetFollowersCountTask.COUNT_KEY);
            observer.getFollowersCountSuccess(count);
        }
    }


}
