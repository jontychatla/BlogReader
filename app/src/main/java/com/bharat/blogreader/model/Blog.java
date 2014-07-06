package com.bharat.blogreader.model;

/**
 * Created by bharatkc on 7/5/14.
 */
public class Blog {
    private String author;
    private String title;
    private String thumbnail;

    public Blog(String title, String author, String thumbnail) {
        this.title = title;
        this.author = author;
        this.thumbnail = thumbnail;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
