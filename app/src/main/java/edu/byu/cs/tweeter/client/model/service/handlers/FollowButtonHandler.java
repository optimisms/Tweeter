package edu.byu.cs.tweeter.client.model.service.handlers;


import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.Service.FollowButtonObserver;

public class FollowButtonHandler extends NoDataReturnedHandler {
    public FollowButtonHandler(FollowButtonObserver observer, String goal) {super(observer, goal);}

    @Override
    protected void afterSuccessDo(Service.NoDataReturnedObserver observer) {
        ((FollowButtonObserver) observer).enableButton();
    }
}