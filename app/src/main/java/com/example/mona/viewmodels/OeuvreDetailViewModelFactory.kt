package com.example.mona.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//Factory of OeuvreDetailViewModel to accomodate the extra argument of the artoworks id

class OeuvreDetailViewModelFactory(private val application: Application, private val oeuvreId: Int) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OeuvreDetailViewModel(application, oeuvreId) as T
    }
}
