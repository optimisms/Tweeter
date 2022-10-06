package edu.byu.cs.tweeter.client.model.service.handlers;


import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.Service;

public class FollowButtonHandler extends BackgroundTaskHandler<Service.FollowButtonObserver> {
    public FollowButtonHandler(Service.FollowButtonObserver observer, String goal) {super(observer, goal);}

    @Override
    protected void handleSuccessMessage(Service.FollowButtonObserver observer, Bundle data) {
        observer.taskSuccess();
        observer.enableButton();
    }
}