package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.androiddevs.mvvmnewsapp.util.Resource
import com.androiddevs.mvvmnewsapp.viewModel.NewsViewModel
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_breaking_news.rvBreakingNews

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    val TAG = "BreakingNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as NewsActivity).newsViewModel //casting BreakingNews Fragment as NewsActivity so that
        //it has access to it's newsViewModel
        setupRecyclerView()
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer {response -> //"response" = "breakingNews", Need to pass
            //"viewLifecycleOwner" cuz we are inside a fragment
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)//response.data = newsResponse
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE +2 //adding 1 cuz in Integer/Integer the result
                        //is rounded off, so we need 1 extra page. Adding another 1 cuz last page of API has No articles
                        isLastPage = viewModel.breakingNewsPage ==totalPages
                        if(isLastPage){
                            rvBreakingNews.setPadding(0,0,0,0) //setting padding to 0 as we don't want
                            //any gap between bottomNavigation and the last news of last page. BottomPadding(50dp) is initially defined under "fragment_breaking_news"
                            //file
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {message ->
                        Log.e(TAG, "An error occurred Braking News API: $message") }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
        newsAdapter.setOnItemClickListener {
            Log.e(TAG, "It went inside onClickListener")
            val bundle = Bundle().apply {//bundle is used to pass data between Fragments/Activities, Here bundle
                //represents the item(Article) that was clicked
                putSerializable("article", it) //"article" is the name of Argument we created in Navigation graph under "ArticleFragment"
            }
            findNavController().navigate(R.id.action_breakingNewsFragment_to_acticleFragment,bundle) //Like an Intent, this line
            //will take us to the the ArticleFragment, now we can receive this bundle in ArticleFragment
        }
    }
    private fun hideProgressBar(){
        paginationProgressBar.visibility=View.INVISIBLE
        isLoading= false
    }
    private fun showProgressBar(){
        paginationProgressBar.visibility= View.VISIBLE
        isLoading=true
    }

    var isLoading = false
    var isLastPage = false //last page info sent by API
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){ //To check if user is still scrolling
                isScrolling= true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition= layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount= layoutManager.childCount //no of items currently being displayed
            val totalItemCount = layoutManager.itemCount //total no of items currently available in recylerview
            //below are some boolean checks, used to determine if we should paginate or not
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate){
                viewModel.getBreakingNews("us")
                isScrolling=false
            }

        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter= newsAdapter //passing the adapter into the recycler view
            layoutManager=LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}