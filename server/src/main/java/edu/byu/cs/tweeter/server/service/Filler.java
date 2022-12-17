package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.FollowDatabase;
import edu.byu.cs.tweeter.server.dao.UserDatabase;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;

public class Filler {
    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 10000;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final static String FOLLOW_TARGET = "@test4B";

    public static void fillDatabase() {
        // Get instance of DAOs by way of the Abstract Factory Pattern
        UserDatabase userDAO = DynamoDAOFactory.getInstance().getUsersDAO();
        FollowDatabase followDAO = DynamoDAOFactory.getInstance().getFollowDAO();

        List<String> followers = new ArrayList<>();
        List<User> users = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {
            String firstName = "Guy";
            String lastName = "" + i;
            String alias = "@guy" + UUID.randomUUID().toString().substring(0, 8);
            String imageUrl = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            User user = new User(firstName, lastName, alias, imageUrl);
            users.add(user);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            followers.add(alias);
        }

        // Call the DAOs for the database logic
        if (users.size() > 0) {
            userDAO.addUserBatch(users);
        }
        if (followers.size() > 0) {
            followDAO.addFollowersBatch(followers, FOLLOW_TARGET);
        }
    }
}