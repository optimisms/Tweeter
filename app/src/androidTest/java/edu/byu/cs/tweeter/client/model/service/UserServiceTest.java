package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;

public class UserServiceTest {
    private ServerFacade myFacade;

    @BeforeEach
    void setup() {
        myFacade = new ServerFacade();
    }

    @Test
    public void testRegisterSuccess() {
        try {
            RegisterRequest req = new RegisterRequest("@allen", "abcde", "Allen", "Allen", FakeImage.imageString);
            AuthResponse resp = myFacade.register(req, UserService.REGISTER_URL_PATH);
            Assertions.assertNotNull(resp);
            Assertions.assertTrue(resp.isSuccess());
            Assertions.assertNull(resp.getMessage());
            Assertions.assertNotNull(resp.getUser());
            Assertions.assertEquals(req.getUsername(), resp.getUser().getAlias());
            Assertions.assertEquals(req.getFirstName(), resp.getUser().getFirstName());
            Assertions.assertEquals(req.getLastName(), resp.getUser().getFirstName());
            Assertions.assertNotNull(resp.getUser().getImageUrl());
            Assertions.assertNotNull(resp.getAuthToken());
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRegisterFailure() {
        try {
            RegisterRequest req = new RegisterRequest(null, "abcde", "Allen", "Alderman", FakeImage.imageString);
            AuthResponse resp = myFacade.register(req, UserService.REGISTER_URL_PATH);
            Assertions.assertNotNull(resp);
            Assertions.assertFalse(resp.isSuccess());
            Assertions.assertNotNull(resp.getMessage());
            Assertions.assertEquals("Failed to register because of exception: [BadRequest] Missing a username", resp.getMessage());
            Assertions.assertNull(resp.getUser());
            Assertions.assertNull(resp.getAuthToken());
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFollowersSuccess() {
        try {
            PagedRequest<User> req = new PagedRequest<>(new AuthToken(), "@allen", 5, null);
            GetFollowersResponse resp = myFacade.getFollowers(req, FollowService.GET_FOLLOWERS_URL_PATH);
            Assertions.assertNotNull(resp);
            Assertions.assertTrue(resp.isSuccess());
            Assertions.assertNull(resp.getMessage());
            Assertions.assertNotNull(resp.getItems());
            Assertions.assertEquals(5, resp.getItems().size());
            Assertions.assertTrue(resp.getHasMorePages());
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFollowersFailure() {
        try {
            PagedRequest<User> req = new PagedRequest<>(new AuthToken(), null, 5, null);
            GetFollowersResponse resp = myFacade.getFollowers(req, FollowService.GET_FOLLOWERS_URL_PATH);
            Assertions.assertNotNull(resp);
            Assertions.assertFalse(resp.isSuccess());
            Assertions.assertNotNull(resp.getMessage());
            Assertions.assertEquals("Failed to register because of exception: [BadRequest] Request needs to have a followee alias", resp.getMessage());
            Assertions.assertNull(resp.getItems());
            Assertions.assertFalse(resp.getHasMorePages());
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFollowersCountSuccess() {
        try {
            int realCount = myFacade.getFollowers(new PagedRequest<>(new AuthToken(), "@allen", 50, null), FollowService.GET_FOLLOWERS_URL_PATH).getItems().size() - 1;

            CountRequest req = new CountRequest(new User("", "", "", ""));
            CountResponse resp = myFacade.getFollowersCount(req, FollowService.GET_FOLLOWERS_COUNT_URL_PATH);

            Assertions.assertNotNull(resp);
            Assertions.assertTrue(resp.isSuccess());
            Assertions.assertNull(resp.getMessage());
            Assertions.assertEquals(realCount, resp.getCount());
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFollowersCountFailure() {
        try {
            CountRequest req = new CountRequest(null);
            CountResponse resp = myFacade.getFollowersCount(req, FollowService.GET_FOLLOWERS_COUNT_URL_PATH);

            Assertions.assertNotNull(resp);
            Assertions.assertFalse(resp.isSuccess());
            Assertions.assertNotNull(resp.getMessage());
            Assertions.assertEquals("Failed to register because of exception: [BadRequest] Request needs to have a target user", resp.getMessage());
            Assertions.assertEquals(0, resp.getCount());
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
    }
}
