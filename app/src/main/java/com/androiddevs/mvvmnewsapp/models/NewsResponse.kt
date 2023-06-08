package com.androiddevs.mvvmnewsapp.models

import com.androiddevs.mvvmnewsapp.models.Article

data class NewsResponse(
    val articles: MutableList<Article>, //Making it Mutable, cuz we'd need to add articles during pagination
    val status: String,
    val totalResults: Int //This field gives us total number of results present including all the pages(1 page has upto 20 news)
)