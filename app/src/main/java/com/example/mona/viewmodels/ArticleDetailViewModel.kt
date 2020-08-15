package com.example.mona.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mona.data.ArticleDatabase
import com.example.mona.data.ArticleRepository
import com.example.mona.data.LieuDatabase
import com.example.mona.data.LieuRepository
import com.example.mona.entity.Article
import com.example.mona.entity.Lieu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



//View Model for the single artwork displayed on the UI to bind properly
class ArticleDetailViewModel(application: Application, private var articleId: Int): AndroidViewModel(application){

    private val repository: ArticleRepository
    var article: Article? = null
    init {
        Log.d("LIEU","DETAIL VIEW")
        // Gets reference to OeuvreDao from OeuvreDatabase to construct
        // the correct OeuvreRepository.
        val articleDao = ArticleDatabase.getDatabase(
            application,
            viewModelScope
        ).articleDAO()
        repository = ArticleRepository.getInstance(articleDao)
        getArticle()
    }

    fun getArticle(){
        viewModelScope.launch(Dispatchers.IO) {
            article = repository.getArticle(articleId)
        }
    }

    fun updateTarget(oeuvreId: Int, target: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTarget(oeuvreId,target)
        }
    }


    fun isCollected(): Boolean{

        var place_collected = false

        //State checuk to see if artwork is collected
        if(article?.state == 2){
            place_collected = true
        }

        return place_collected
    }

    fun isTarget(): Boolean{

        var place_target = false

        //State checuk to see if artwork is collected
        if(article?.state == 1){
            place_target = true
        }

        return place_target
    }


    fun getCaptureDateMessage(): String{
        return "Cette oeuvre a été capturée le " + article?.date_photo
    }

    fun getComment(): String{
        return "Commentaire: "+ article?.comment
    }




}