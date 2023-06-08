package com.androiddevs.mvvmnewsapp.db

import androidx.room.TypeConverter
import com.androiddevs.mvvmnewsapp.models.Source

class Converters {
    @TypeConverter
    fun fromSource(source: Source) : String{
        return source.name //this value will be stored in table
    }
    @TypeConverter
    fun toSource(name : String): Source {
        return Source(name, name) //this value will be seen to user while retrieving String present under "Source" column
    }
}