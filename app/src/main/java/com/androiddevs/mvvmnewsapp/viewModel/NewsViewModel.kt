package com.androiddevs.mvvmnewsapp.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.NewsApplication
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(val newsRepository: NewsRepository, app: Application): AndroidViewModel(app) {
    //Using AndroidViewModel and not ViewModel, cuz it allows us to use Application Context(app) with the help of
    //getApplication() which is used in hadInternetConnection()
    val breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData() //this is the way to create an empty instance
    //of MutableLiveData, it'll contain response of getBreakingNews() wrapped under Resource's object
    var breakingNewsPage = 1 //keeping the count of page in ViewModel cuz we don't want ki on screen rotation, page=1 ho jae
    var breakingNewsResponse : NewsResponse?= null //to store the already loaded data


    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse?= null

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch { //"viewModelScope" will make
        //sure that this coroutine is alive only as long as this ViewModel is alive
//        breakingNews.postValue(Resource.Loading()) //As the network call is about to happen, we are putting our "breakingNews"
//        // variable in Loading state. An Observer of this data can show some Loading indicator on screen after seeing
//        // this state of breakingNews
//        val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
//        breakingNews.postValue(handleBreakingNewsResponse(response)) //postValue() method ensures that the value update is dispatched to the main thread.
        //When you call postValue() with a new value, the MutableLiveData queues the value change and dispatches it asynchronously to the main thread for processing.
        safeBreakingNews(countryCode)

    }

    fun searchNews(searchQuery : String) = viewModelScope.launch {
//        searchNews.postValue(Resource.Loading())
//        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
//        searchNews.postValue(handleSearchNewsResponse(response))
        safeSearchNews(searchQuery)
    }

    private fun handleBreakingNewsResponse(response : Response<NewsResponse>): Resource<NewsResponse>{ //"Response" is a
        //class of retrofit
        if (response.isSuccessful){
            response.body()?. let { resultResponse -> //"resultResponse" is a variable that stores "response.body()"
                //and it's being passed as an argument while creating an instance of Success class below
                breakingNewsPage++
                if (breakingNewsResponse==null){ //if this is the first page we want to load
                    breakingNewsResponse=resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles //articles already present on screen
                    val newArticles = resultResponse.articles //articles came after increasing breakingNewsPage
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse?: resultResponse) //return breakingNewsResponse, but if it's null
                //then return resultResponse
                }
        }
        return Resource.Error(response.message()) //"message()" gives http status message
    }

    private fun handleSearchNewsResponse(response : Response<NewsResponse>): Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?. let {  resultResponse ->
                searchNewsPage++
                if (searchNewsResponse==null){
                    searchNewsResponse=resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    fun saveArticle(article:Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }
    fun getSavedArticles() =  newsRepository.getSavedNews()
    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }
    private suspend fun safeBreakingNews(countryCode: String){ //Internet Checking Version of BreakingNews()
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }
            else{
                breakingNews.postValue(Resource.Error("No Internet connection"))
            }
        }catch (t : Throwable){ //In case API call throws an exception
            when(t){
                is IOException -> breakingNews.postValue(Resource.Error("Network Error")) //Retrofit can throw IO Exception
                else -> breakingNews.postValue(Resource.Error("Conversion Error")) //Conversion from json to java Object
            }
        }
    }
    private suspend fun safeSearchNews(searchQuery : String){ //Internet Checking Version of SearchNews()
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }
            else{
                searchNews.postValue(Resource.Error("No Internet connection"))
            }
        }catch (t : Throwable){ //In case API call throws an exception
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network Error"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private fun hasInternetConnection(): Boolean{ //A system function requires context, so we need to pass context of the
        //activity, but that'll be bad practice as ViewModel should be separated from Activity. So we'll pas Application's
        //context and not Activity context
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager //Need to cast it as connectivityManager cuz getSystemService returns an object of Object type

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){//cuz function in below API 23 is different
            val activityNetwork = connectivityManager.activeNetwork ?: return false //activityNetwork represents the currently active
            //network on the device. This could be either the Wi-Fi network or the cellular network that the device is connected to.
            val capabilities = connectivityManager.getNetworkCapabilities(activityNetwork)?: return false //"capabilities" provide
            //information about the network
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else { connectivityManager.activeNetworkInfo?.run {//these functions are not deprecated for API<23
            return when(type){
                TYPE_WIFI -> true
                TYPE_MOBILE -> true
                TYPE_ETHERNET -> true
                else -> false
            }
        }
        }
        return true
    }
}