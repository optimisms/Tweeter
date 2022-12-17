package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.text.ParseException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.FeedDatabase;
import edu.byu.cs.tweeter.server.dao.FollowDatabase;
import edu.byu.cs.tweeter.server.dao.PagedDatabase;
import edu.byu.cs.tweeter.server.dao.factory.DAOFactory;
import edu.byu.cs.tweeter.server.net.JsonSerializer;
import edu.byu.cs.tweeter.server.net.request.WriteBatchRequest;

public class StatusService {
    DAOFactory factory;
    public StatusService(DAOFactory factory) {
        this.factory = factory;
    }

    //TODO: add storing timestamps as longs so they sort correctly and then converting them to strings to display for the statuses

    private void createDummyUsers() {
        for (int i = 0; i < 100; i++) {

        }
    }
    private void createUserAndFollow() {

    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        try {
            if (request.getStatus() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a status to post");
            } else if (request.getToken() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have an authToken");
                //TODO: uncomment this when ready to test w emulator
//            } else if (!AuthService.tokenIsValid(request.getToken())) {
//                throw new RuntimeException("[BadRequest] Request needs to have a valid authToken");
            }

            getStoryDAO().add(request.getStatus());

            String queueUrl = "https://sqs.us-west-2.amazonaws.com/475409691518/make-batch-queue";
            String messageBody = JsonSerializer.serialize(request.getStatus());
            pushToQueue(queueUrl, messageBody);

            return new PostStatusResponse();
        } catch (DataAccessException e){// | ParseException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("[BadRequest]")) {
                return new PostStatusResponse(e.getMessage());
            } else {
                return new PostStatusResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    public void makeBatch(SQSEvent.SQSMessage msg) {
        Status status = JsonSerializer.deserialize(msg.getBody(), Status.class);
        User poster = status.getUser();

        List<String> followers = getFollowDAO().getAllFollowers(poster.getAlias());

        String queueUrl = "https://sqs.us-west-2.amazonaws.com/475409691518/write-batch-queue";
        for (int i = 0; i < followers.size(); i += 25) {
            int batchSize = 25;
            if (followers.size() - i < 25) {
                batchSize = followers.size() - i;
            }
            WriteBatchRequest req = new WriteBatchRequest(followers.subList(i, i + batchSize), status);
            String messageBody = JsonSerializer.serialize(req);
            pushToQueue(queueUrl, messageBody);
        }
    }

    private void pushToQueue(String queueUrl, String messageBody) {
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(messageBody);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);

        String msgId = send_msg_result.getMessageId();
        System.out.println("Message ID: " + msgId);
    }

    public void writeBatch(SQSEvent.SQSMessage msg) {
        WriteBatchRequest req = JsonSerializer.deserialize(msg.getBody(), WriteBatchRequest.class);
        List<String> followers = req.getFollowerAliases();
        Status status = req.getStatus();

        getFeedDAO().batchWrite(followers, status);
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
    private FeedDatabase getFeedDAO() { return factory.getFeedDAO(); }
    private FollowDatabase getFollowDAO() { return factory.getFollowDAO(); }
}
