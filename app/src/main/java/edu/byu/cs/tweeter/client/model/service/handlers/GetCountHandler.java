package edu.byu.cs.tweeter.client.model.service.handlers;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.Service;

public abstract class GetCountHandler extends BackgroundTaskHandler<Service.GetCountObserver> {
    public GetCountHandler(Service.GetCountObserver observer, String goal) { super(observer, goal); }

    @Override
    protected void handleSuccessMessage(Service.GetCountObserver observer, Bundle data) {
        int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
        observer.getCountSuccess(count);
    }
}