package com.example.centralchat.news;

public class NewsList {
    private String newsBody, newsDate, newsImage;


    public NewsList(String newsBody, String newsDate, String newsImage) {
        this.newsBody = newsBody;
        this.newsDate = newsDate;
        this.newsImage = newsImage;
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
