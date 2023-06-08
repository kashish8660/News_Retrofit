package com.androiddevs.mvvmnewsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androiddevs.mvvmnewsapp.models.Article

@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao

    companion object{
        @Volatile //to make the changes done in "instance" variable visible to all threads
        private var instance:ArticleDatabase?= null //mean either "instance" value will be null or of
    // "ArticleDatabase" type
        private val LOCK= Any() //"Any"(root class of all classes) is similar to Object class in java. Here
    // ww are initialising "LOCK" with an instance of "Any" class

    operator fun invoke(context: Context) = instance ?: synchronized(LOCK){ //"?:" means run the further code if "instance" is null
        //but if instance is not null then return the instance. We are overloading "invoke" operator with the help of "operator" keyword
    instance ?: createDatabase(context).also { instance= it} //"it" indicates instance will be assigned to whatever is
        //returned by createDatabase(). "also" says create the object through "createDatabase" and also assign it to "instance"
    }
    private fun createDatabase(context: Context) =
        Room.databaseBuilder(context.applicationContext,ArticleDatabase::class.java, "article_db.db")
            .build()
    }
}