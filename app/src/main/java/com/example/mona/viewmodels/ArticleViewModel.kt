package com.example.mona.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mona.data.ArticleDatabase
import com.example.mona.data.ArticleRepository
import com.example.mona.data.LieuDatabase
import com.example.mona.data.LieuRepository
import com.example.mona.entity.Article
import com.example.mona.entity.Lieu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Class extends AndroidViewModel and requires application as a parameter.
// You can also use a ViewModel to share data between fragments.
// https://developer.android.com/topic/libraries/architecture/viewmodel
class ArticleViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: ArticleRepository
    // LiveData gives us updated words when they change.
    val articleList: LiveData<List<Article>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val articleDAO = ArticleDatabase.getDatabase(
            application,
            viewModelScope
        ).articleDAO()
        repository = ArticleRepository(articleDAO)
        articleList = repository.articleList
    }

    fun updateRating(id: Int, rating: Float?, comment: String?, state: Int?, date: String?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRating(id, rating, comment, state, date)
        }
    }

    fun updatePath(id: Int, path: String?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePath(id, path)
        }
    }



}
