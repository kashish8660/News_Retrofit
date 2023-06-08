package com.androiddevs.mvvmnewsapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepository: NewsRepository): ViewModel() {

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
        breakingNews.postValue(Resource.Loading()) //As the network call is about to happen, we are putting our "breakingNews"
        // variable in Loading state. An Observer of this data can show some Loading indicator on screen after seeing
        // this state of breakingNews
        val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response)) //postValue() method ensures that the value update is dispatched to the main thread.
        //When you call postValue() with a new value, the MutableLiveData queues the value change and dispatches it asynchronously to the main thread for processing.
    }

    fun searchNews(searchQuery : String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
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

    private fun hasInternetConnection(): Boolean{
        return true
    }
}