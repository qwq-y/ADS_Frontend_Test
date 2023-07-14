package com.example.testapplication.models;

import android.graphics.Bitmap;

import java.io.File;
import java.util.List;
import java.util.Map;

public class CustomResponse {

    private String status;

    private List<String> images;

    List<Map<String, Object>> messages;

    private File video;

    public CustomResponse() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Map<String, Object>> getMessages() {
        return messages;
    }

    public void setMessages(List<Map<String, Object>> messages) {
        this.messages = messages;
    }

    public File getVideo() {
        return video;
    }

    public void setVideo(File video) {
        this.video = video;
    }
}


