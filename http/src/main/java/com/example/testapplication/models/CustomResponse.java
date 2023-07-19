package com.example.testapplication.models;

import android.graphics.Bitmap;

import java.io.File;
import java.util.List;
import java.util.Map;

public class CustomResponse {

    private Map<String, String> images;
    private Map<String, String> links;
    private String message;
    private String status;
    private File video;

    public CustomResponse() {}

    public Map<String, String> getImages() {
        return images;
    }

    public void setImages(Map<String, String> images) {
        this.images = images;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public File getVideo() {
        return video;
    }

    public void setVideo(File video) {
        this.video = video;
    }
}


