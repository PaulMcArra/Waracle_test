package com.waracle.androidtest;


import android.graphics.Bitmap;


public class Cake {
    private String name;
    private String description;
    private String image;
    private Bitmap bmImage;

    public Cake(String name, String description, String image, Bitmap bitmap) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.bmImage = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getBmImage() {
        return this.bmImage;
    }


    public void setBmImage(Bitmap bmImage) {
        this.bmImage = bmImage;
    }
}
