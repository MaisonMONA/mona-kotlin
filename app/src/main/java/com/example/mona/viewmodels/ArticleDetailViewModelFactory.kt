package com.example.mona.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//Factory of OeuvreDetailViewModel to accomodate the extra argument of the artoworks id

class ArticleDetailViewModelFactory(private val application: Application, private val articleId: Int) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArticleDetailViewModel(application, articleId) as T
    }
}
