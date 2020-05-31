package com.example.mona.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//Factory of LieuDetailViewModel to accomodate the extra argument of the artoworks id

class LieuDetailViewModelFactory(private val application: Application, private val lieuId: Int) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LieuDetailViewModel(application, lieuId) as T
    }
}