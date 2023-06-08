package com.androiddevs.mvvmnewsapp.api
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/top-headlines") //this is the request url after base url
    suspend fun getBreakingNews(
        @Query("country") //this is the "Query Parameter" , which can be given in url too in Postman
        countryCode:String="us", //initial value of "country"
        @Query("page")
        pageNumber: Int=1,
        @Query("apiKey")
        apiKey:String= API_KEY
    ) : Response<NewsResponse>//"Response" is a pre-defined class of Retrofit.

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery:String,
        @Query("page")
        pageNumber: Int=1,
        @Query("apiKey")
        apiKey:String= API_KEY
    ) : Response<NewsResponse>
}