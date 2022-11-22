package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {
    public GetFollowersTask(PagedTaskData<User> data, Handler messageHandler) { super(data, messageHandler); }

    @Override
    protected PagedResponse<User> getResponse() throws IOException, TweeterRemoteException {
        String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
        String lastFollowerAlias = getLastItem() == null ? null : getLastItem().getAlias();
        GetFollowersRequest req = new GetFollowersRequest(getAuthToken(), targetUserAlias, getLimit(), lastFollowerAlias);
        return getServerFacade().getFollowers(req, FollowService.GET_FOLLOWERS_URL_PATH);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        return getFakeData().getPageOfUsers(getLastItem(), getLimit(), getTargetUser());
    }
}
