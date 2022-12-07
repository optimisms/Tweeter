package edu.byu.cs.tweeter.server.dao.dynamo.DTO;

import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class StoryBean {
    public String post;
    public String sender_firstName;
    public String sender_lastName;
    public String sender_alias;
    public String sender_imageUrl;
    public String timestamp;
    public List<String> urls;
    public List<String> mentions;

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    @DynamoDbPartitionKey
    public String getSender_alias() {
        return sender_alias;
    }

    public void setSender_alias(String sender_alias) {
        this.sender_alias = sender_alias;
    }

    public String getSender_firstName() {
        return sender_firstName;
    }

    public void setSender_firstName(String sender_firstName) {
        this.sender_firstName = sender_firstName;
    }

    public String getSender_lastName() {
        return sender_lastName;
    }

    public void setSender_lastName(String sender_lastName) {
        this.sender_lastName = sender_lastName;
    }

    public String getSender_imageUrl() {
        return sender_imageUrl;
    }

    public void setSender_imageUrl(String sender_imageUrl) {
        this.sender_imageUrl = sender_imageUrl;
    }

    @DynamoDbSortKey
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }
}
