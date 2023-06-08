package com.androiddevs.mvvmnewsapp.ui.fragments
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.viewModel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.fab
import kotlinx.android.synthetic.main.fragment_article.webView


class ArticleFragment: Fragment(R.layout.fragment_article) {
    lateinit var viewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs() //Similar to creating an Intent to receive value
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        val article = args.article
        webView.apply {
            webViewClient= WebViewClient()
            loadUrl(article.url)
        }
        fab.setOnClickListener{
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article Saved Successfully", Snackbar.LENGTH_SHORT).show()
        }
    }
}