package com.moengage.newsfeed.Model;

import com.google.gson.annotations.SerializedName;

public class News {
    @SerializedName("url")
    private String mUrl;

    @SerializedName("urlToImage")
    private String mUrlToImage;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("author")
    private String mAuthor;

    @SerializedName("publishedAt")
    private String mPublishedAt;

    @SerializedName("description")
    private String mDescription;


    public String getUrlToImage() {
        return mUrlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.mUrlToImage = urlToImage;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getPublishedAt() {
        return mPublishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.mPublishedAt = publishedAt;
    }
}
