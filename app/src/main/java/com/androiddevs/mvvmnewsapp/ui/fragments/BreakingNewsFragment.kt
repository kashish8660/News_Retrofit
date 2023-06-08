package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
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
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer {response -> //"response" = "breakingNews"
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse -> newsAdapter.differ.submitList(newsResponse.articles)
                    }
                    //response.data = newsResponse
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
    }
    private fun showProgressBar(){
        paginationProgressBar.visibility= View.VISIBLE
    }
    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter= newsAdapter //passing the adapter into the recycler view
            layoutManager=LinearLayoutManager(activity)
        }
    }
}