package edu.byu.cs.tweeter.server.service;

import java.text.ParseException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.PagedDatabase;
import edu.byu.cs.tweeter.server.dao.factory.DAOFactory;

public class StatusService {
    DAOFactory factory;
    public StatusService(DAOFactory factory) {
        this.factory = factory;
    }

    //TODO: add storing timestamps as longs so they sort correctly and then converting them to strings to display for the statuses

    public PostStatusResponse postStatus(PostStatusRequest request) {
        try {
            if (request.getStatus() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a status to post");
            } else if (request.getToken() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have an authToken");
            } else if (!AuthService.tokenIsValid(request.getToken())) {
                throw new RuntimeException("[BadRequest] Request needs to have a valid authToken");
            }

            getStoryDAO().add(request.getStatus());

            //TODO: update to include updating feeds for real
            getFeedDAO().add(request.getStatus());
            return new PostStatusResponse();
        } catch (DataAccessException | ParseException e) {
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
            } else if (request.getAuthToken() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have an authToken");
            } else if (!AuthService.tokenIsValid(request.getAuthToken())) {
                throw new RuntimeException("[BadRequest] Request needs to have a valid authToken");
            }

            String lastStatusTimestamp = request.getLastItem() == null ? null : request.getLastItem().getDate();
            List<Status> feed = getFeedDAO().getPages(request.getTargetUserAlias(), request.getLimit(), lastStatusTimestamp, "getFeed");

            return new GetFeedResponse(feed, feed.size() == request.getLimit());
        } catch (DataAccessException | RuntimeException | ParseException e) {
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
            } else if (request.getAuthToken() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have an authToken");
            } else if (!AuthService.tokenIsValid(request.getAuthToken())) {
                throw new RuntimeException("[BadRequest] Request needs to have a valid authToken");
            }

            String lastStatusTimestamp = request.getLastItem() == null ? null : request.getLastItem().getDate();
            List<Status> story = getStoryDAO().getPages(request.getTargetUserAlias(), request.getLimit(), lastStatusTimestamp, "getStory");

            return new GetStoryResponse(story, story.size() == request.getLimit());
        } catch (DataAccessException | RuntimeException | ParseException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("[BadRequest]")) {
                return new GetStoryResponse(e.getMessage());
            } else {
                return new GetStoryResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    private PagedDatabase<Status, Status> getStoryDAO() { return factory.getStoryDAO(); }
    private PagedDatabase<Status, Status> getFeedDAO() { return factory.getFeedDAO(); }
}
