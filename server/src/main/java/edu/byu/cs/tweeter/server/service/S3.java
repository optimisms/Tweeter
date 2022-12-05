package edu.byu.cs.tweeter.server.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class S3 {
    public static String putImage(String imageName, String imageString) {
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion("us-west-2")
                .build();

        //Bucket name
        String targetBucket = "hterry-cs-340-tweeter-profile-pics";

        //Convert image to a byte array
        byte[] imageByteArray = Base64.getDecoder().decode(imageString);

        //Set metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageByteArray.length);
        //TODO: maybe change this to just image/* ?
        metadata.setContentType("image/jpg");

        //Create put request and upload image
        AmazonS3 client = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
        PutObjectRequest req = new PutObjectRequest(targetBucket, imageName, new ByteArrayInputStream(imageByteArray), metadata);
        client.putObject(req);

        //Get image url
        return s3.getUrl(targetBucket, imageName).toString();
    }
}


