package edu.byu.cs.tweeter.client.model.service.handlers;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.AuthenticateTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthHandler extends BackgroundTaskHandler<Service.AuthObserver> {
    public AuthHandler(Service.AuthObserver observer, String goal) { super(observer, goal); }

    @Override
    protected void handleSuccessMessage(Service.AuthObserver observer, Bundle data) {
        User authUser = (User) data.getSerializable(AuthenticateTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(AuthenticateTask.AUTH_TOKEN_KEY);

        Cache.getInstance().setCurrUser(authUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        observer.authSuccess(authUser, authToken);
    }
}
