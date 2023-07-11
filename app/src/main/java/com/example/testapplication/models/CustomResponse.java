package com.example.testapplication.models;

import android.graphics.Bitmap;

import java.io.File;

public class CustomResponse {

    private String text;

    private File image;

    private File video;

    public CustomResponse() {
    }

    public String getText() {
        return text;
    }

    public File getImage() {
        return image;
    }

    public File getVideo() {
        return video;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public void setVideo(File video) {
        this.video = video;
    }

    @Override
    public String toString() {
        return "CustomResponse{" +
                "text='" + text + '\'' +
                ", image=" + image +
                ", video=" + video +
                '}';
    }
}


