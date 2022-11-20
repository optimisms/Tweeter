package edu.byu.cs.tweeter.model.net.request;

//TODO: write class
public class RegisterRequest extends Request {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String image;

    private RegisterRequest() {}

    public RegisterRequest(String username, String password, String firstName, String lastName, String image) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
    }

    /**
     * Returns the username of the user to be registered by this request.
     *
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password of the user to be registered by this request.
     *
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the firstName of the user to be registered by this request.
     *
     * @return the firstName.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the firstName.
     *
     * @param firstName the first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the lastName of the user to be registered by this request.
     *
     * @return the lastName.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the lastName.
     *
     * @param lastName the last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the profile image of the user to be registered by this request.
     *
     * @return the image.
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the image.
     *
     * @param image the image.
     */
    public void setImage(String image) {
        this.image = image;
    }
}
