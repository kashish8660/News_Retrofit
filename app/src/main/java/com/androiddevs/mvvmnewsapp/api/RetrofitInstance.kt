package com.androiddevs.mvvmnewsapp.api

import com.androiddevs.mvvmnewsapp.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {
    companion object{
        private val retrofit by lazy {//"lazy" is used when some variable's value(retrofit in this case) is calculated or
        // initialized only when it is first accessed and then cached for subsequent access.
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client= OkHttpClient.Builder() //creating a client which will help us in debugging our api by logging
                .addInterceptor(logging)
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) //client is created above
                .build()
        }
        val api by lazy{
            retrofit.create(NewsApi::class.java) //passing an anonymous class which implements NewsApi interface
        }
    }
}