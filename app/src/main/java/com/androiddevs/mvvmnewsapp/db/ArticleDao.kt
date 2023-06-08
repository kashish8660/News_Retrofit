package com.androiddevs.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androiddevs.mvvmnewsapp.models.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) //If we trying to add some already existing article, then
    // old one will be replaced
   fun upsert(article: Article):Long //upsert= insert or update

    @Query("select * from articles")
    fun getAllArticles():LiveData<List<Article>> //not using "suspend"(Worker-Thread) here cuz it returns LiveData which is used
    // to update UI(Main-Thread)

    @Delete
    fun deleteArticle(article: Article)
}