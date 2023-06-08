package com.androiddevs.mvvmnewsapp.util

class Constants {
    companion object{ //it's like "static" object of java
        const val API_KEY="40c47b823378461598fe1f837f47b16d" //"const" keyword indicate that value is
    // known at compile time. Any changes tried to change it's value(even in compile time) will generate error.
        const val BASE_URL="https://newsapi.org"
        const val SEARCH_NEWS_TIME_DELAY = 500L
        const val QUERY_PAGE_SIZE = 20 //cuz 1 page of API gives 20 news
    }
}