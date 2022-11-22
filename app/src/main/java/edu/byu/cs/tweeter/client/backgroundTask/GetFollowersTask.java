package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {
    public GetFollowersTask(PagedTaskData<User> data, Handler messageHandler) { super(data, messageHandler); }

    @Override
    protected PagedResponse<User> getResponse() throws IOException, TweeterRemoteException {
        String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
        PagedRequest<User> req = new PagedRequest<>(getAuthToken(), targetUserAlias, getLimit(), getLastItem());
        return getServerFacade().getFollowers(req, FollowService.GET_FOLLOWERS_URL_PATH);
    }
}
