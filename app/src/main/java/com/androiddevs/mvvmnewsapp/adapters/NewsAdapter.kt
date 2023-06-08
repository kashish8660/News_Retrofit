package com.androiddevs.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.models.Article
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_article_preview.view.ivArticleImage
import kotlinx.android.synthetic.main.item_article_preview.view.tvDescription
import kotlinx.android.synthetic.main.item_article_preview.view.tvPublishedAt
import kotlinx.android.synthetic.main.item_article_preview.view.tvSource
import kotlinx.android.synthetic.main.item_article_preview.view.tvTitle
import kotlin.coroutines.coroutineContext

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

   inner class ArticleViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) //"inner" keyword allows the inner class
    //to access all properties of outer class
   private var differCallback = object : DiffUtil.ItemCallback<Article>(){ //The DiffUtil class in Android is a
// utility provided by the Android Jetpack library that helps in calculating the differences between two lists(new
// list may have some more articles from API) and providing a list(<Article> suggest that the data-type of list) of
// update operations to efficiently update a RecyclerView without redrawing the entire list.
// Android suggested below 2 functions
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url== newItem.url //we could have used "id" to know the uniqueness of articles, but as we know
    //articles which are not saved in room won't have an "id"
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback) //As DiffUtil doesn't perform comparison in background,
    //so we need to mention it explicitly.

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size //"differ" object created above, can be used to access our list
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {//"apply" provides a concise way to access and modify properties
            // of an object without repeating the object reference.
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvSource.text= article.source.name
            tvDescription.text=article.description
            tvTitle.text= article.title
            tvPublishedAt.text=article.publishedAt
            setOnItemClickListener{ //"{}" defines lambda expression, used to define a function
                onItemClickListener?.let { it(article) }//"?." means run "let" function if "onItemClickListener" is not null
                //means some Article is present. "it" represents setOnItemClickListener().
                Toast.makeText(context, "Hi, event listener worked at $position", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private var onItemClickListener: ((Article)-> Unit)?=null//"Unit" is similar to "void" in java, "onItemClickListener" is
    //an instance of an a lambda function which takes "Article" as argument and returns "Unit"

    fun setOnItemClickListener(listener : (Article)-> Unit){
         onItemClickListener=listener
    }
}