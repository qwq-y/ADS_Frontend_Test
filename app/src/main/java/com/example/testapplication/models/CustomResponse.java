package com.example.testapplication.models;

import android.graphics.Bitmap;

import java.io.File;
import java.util.List;

public class CustomResponse {

    private String text;

    private File image;

    private File video;

    private File zipImage;

    private List<String> encodedImages;

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

    public List<String> getEncodedImages() {
        return encodedImages;
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

    public void setEncodedImages(List<String> encodedImages) {
        this.encodedImages = encodedImages;
    }

}


