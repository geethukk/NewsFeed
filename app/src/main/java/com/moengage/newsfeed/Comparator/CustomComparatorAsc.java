package com.moengage.newsfeed.Comparator;

import com.moengage.newsfeed.Model.News;

import java.util.Comparator;

public class CustomComparatorAsc implements Comparator<News> {

    @Override
    public int compare(News news1, News news2) {
        return news1.getPublishedAt().compareTo(news2.getPublishedAt());
    }
}
