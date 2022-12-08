package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.PagedDatabase;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;

public class StatusService {
    //TODO: update to include authToken checking

    //TODO: update to include updating feed
    public PostStatusResponse postStatus(PostStatusRequest request) {
        try {
            if(request.getStatus() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a status to post");
            }

            getStoryDAO().add(request.getStatus());
            getFeedDAO().add(request.getStatus());
            return new PostStatusResponse();
        } catch (DataAccessException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("[BadRequest]")) {
                return new PostStatusResponse(e.getMessage());
            } else {
                return new PostStatusResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    public GetFeedResponse getFeed(PagedRequest<Status> request) {
        try {
            if(request.getTargetUserAlias() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
            } else if(request.getLimit() <= 0) {
                throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
            }

            String lastStatusTimestamp = request.getLastItem() == null ? null : request.getLastItem().getDate();
            List<Status> feed = getFeedDAO().getPages(request.getTargetUserAlias(), request.getLimit(), lastStatusTimestamp, "getFeed");

            return new GetFeedResponse(feed, feed.size() == request.getLimit());
        } catch (DataAccessException | RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("[BadRequest]")) {
                return new GetFeedResponse(e.getMessage());
            } else {
                return new GetFeedResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    public GetStoryResponse getStory(PagedRequest<Status> request) {
        try {
            if(request.getTargetUserAlias() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a followee alias");
            } else if(request.getLimit() <= 0) {
                throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
            }

            String lastStatusTimestamp = request.getLastItem() == null ? null : request.getLastItem().getDate();
            List<Status> story = getStoryDAO().getPages(request.getTargetUserAlias(), request.getLimit(), lastStatusTimestamp, "getStory");

            return new GetStoryResponse(story, story.size() == request.getLimit());
        } catch (DataAccessException | RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("[BadRequest]")) {
                return new GetStoryResponse(e.getMessage());
            } else {
                return new GetStoryResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    private PagedDatabase<Status, Status> getStoryDAO() { return DynamoDAOFactory.getInstance().getStoryDAO(); }
    private PagedDatabase<Status, Status> getFeedDAO() { return DynamoDAOFactory.getInstance().getFeedDAO(); }
}
