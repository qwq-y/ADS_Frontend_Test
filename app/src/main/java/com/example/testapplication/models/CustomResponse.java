package com.example.testapplication.models;

import android.graphics.Bitmap;

import java.io.File;

public class CustomResponse {

    private String text;

    private File image;

    private File video;

    private File zipImage;

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

    public File getZipImage() {
        return zipImage;
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

    public void setZipImage(File zipImage) {
        this.zipImage = zipImage;
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

