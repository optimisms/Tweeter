package edu.byu.cs.tweeter.client.model.service.handlers;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.Service.NoDataReturnedObserver;


public class NoDataReturnedHandler extends BackgroundTaskHandler<NoDataReturnedObserver> {
    public NoDataReturnedHandler(NoDataReturnedObserver observer, String goal) { super(observer, goal); }

    @Override
    protected void handleSuccessMessage(NoDataReturnedObserver observer, Bundle data) {
        observer.taskSuccess();
        afterSuccessDo(observer);
    }

    protected void afterSuccessDo(NoDataReturnedObserver observer) {}
}