package com.androiddevs.mvvmnewsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null, //made it nullable cuz there will be articles that we will be showing through api but not
    //saving in our database
    val author: String?, //Making each of the column nullable cuz if API ne kuch bhi null bheja to App crash kar jaegi
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String,
    val urlToImage: String?
) : Serializable //As we need to pass this class as an argument