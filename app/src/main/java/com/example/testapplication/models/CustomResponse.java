package com.example.testapplication.models;

import android.graphics.Bitmap;

public class CustomResponse {

    private String text;
    private Bitmap image;

    public CustomResponse() {
    }

    public CustomResponse(String text) {
        this.text = text;
    }

    public CustomResponse(Bitmap image) {
        this.image = image;
    }

    public CustomResponse(String text, Bitmap image) {
        this.text = text;
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "CustomResponse{" +
                "text='" + text + '\'' +
                ", image=" + image +
                '}';
    }
}
