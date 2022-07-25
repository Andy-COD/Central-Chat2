package com.example.centralchat.news;

public class NewsList {
    private String newsBody, newsDate, newsImage, newsLink;


    public NewsList(String newsBody, String newsDate, String newsImage, String newsLink) {
        this.newsBody = newsBody;
        this.newsDate = newsDate;
        this.newsImage = newsImage;
        this.newsLink = newsLink;
    }

    public String getNewsLink() {
        return newsLink;
    }

    public String getNewsBody() {
        return newsBody;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public String getNewsImage() {
        return newsImage;
    }
}
