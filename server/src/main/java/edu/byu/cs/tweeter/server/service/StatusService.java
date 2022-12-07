package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.Database;
import edu.byu.cs.tweeter.server.dao.dynamo.StatusDAO;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;

public class StatusService {
    //TODO: update to include authToken checking

    //TODO: update to include updating feed
    public PostStatusResponse postStatus(PostStatusRequest request) {
        try {
            if(request.getStatus() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a status to post");
            }

            getNewStatusDAO().add(request.getStatus());
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

    //TODO: migrate below to DynamoDB
    public GetFeedResponse getFeed(PagedRequest<Status> request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getStatusDAO().getFeed(request);
    }

    public GetStoryResponse getStory(PagedRequest<Status> request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a followee alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getStatusDAO().getStory(request);
    }

    StatusDAO getStatusDAO() {
        return new StatusDAO();
    }
    private Database<Status> getNewStatusDAO() { return DynamoDAOFactory.getInstance().getStoryDAO();
    }
}
