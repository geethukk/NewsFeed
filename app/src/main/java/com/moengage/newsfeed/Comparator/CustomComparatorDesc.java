package com.moengage.newsfeed.Comparator;

import com.moengage.newsfeed.Model.News;

import java.util.Comparator;

public class CustomComparatorDesc implements Comparator<News> {

    @Override
    public int compare(News news1, News news2) {
        return news2.getPublishedAt().compareTo(news1.getPublishedAt());
    }
}
