package com.androiddevs.mvvmnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.viewModel.NewsViewModel
import com.androiddevs.mvvmnewsapp.viewModel.NewsViewModelFactory
import kotlinx.android.synthetic.main.activity_news.bottomNavigationView


import kotlinx.android.synthetic.main.activity_news.newsNavHostFragment

class NewsActivity : AppCompatActivity() {
    lateinit var newsViewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        val newsRepository= NewsRepository(ArticleDatabase(this))
        newsViewModel=ViewModelProvider(this, NewsViewModelFactory(newsRepository)).get(NewsViewModel::class.java)



        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        bottomNavigationView.setupWithNavController(navHostFragment.navController) //connecting "bottom navigation" to "Host" fragment





    }
}