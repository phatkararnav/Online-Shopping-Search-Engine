package com.example.admin.onlineshoppingsearchengine;

import android.support.annotation.NonNull;
import android.view.View;

import java.io.Serializable;

public class Item implements Comparable<Item>, Serializable {

    public String title;
    public double price;
    public String imageUrl;
    public String website;
    public String itemUrl;

    Item(String title, double price, String imageUrl, String website, String itemUrl) {
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
        this.website = website;
        this.itemUrl = itemUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public String getPrice() {
        return String.format(java.util.Locale.US, "%.2f", this.price);
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getWebsite() {
        return this.website;
    }

    public String getItemUrl() {
        return this.itemUrl;
    }

    @Override
    public int compareTo(@NonNull Item other) {
        return Double.compare(this.price, other.price);
    }

    @Override
    public String toString() {
        return "Title: " + this.getTitle() +
               "\nPrice: " + this.getPrice() +
               "\nImage Url: " + this.getImageUrl() +
               "\nWebsite: " + this.getWebsite() +
               "\nLink: " + this.getItemUrl() + "\n\n";
    }
}
