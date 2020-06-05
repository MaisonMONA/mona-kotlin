package com.example.mona.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mona.data.LieuDatabase
import com.example.mona.data.LieuRepository
import com.example.mona.entity.Lieu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



//View Model for the single artwork displayed on the UI to bind properly
class LieuDetailViewModel(application: Application, private var lieuId: Int): AndroidViewModel(application){

    private val repository: LieuRepository
    var lieu: Lieu? = null
    init {
        // Gets reference to OeuvreDao from OeuvreDatabase to construct
        // the correct OeuvreRepository.
        val lieuDao = LieuDatabase.getDatabase(
            application,
            viewModelScope
        ).lieuDAO()
        repository = LieuRepository.getInstance(lieuDao)
        getLieu()
    }

    fun getLieu(){
        viewModelScope.launch(Dispatchers.IO) {
            lieu = repository.getLieu(lieuId)
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
        if(lieu?.state == 2){
            place_collected = true
        }

        return place_collected
    }

    fun isTarget(): Boolean{

        var place_target = false

        //State checuk to see if artwork is collected
        if(lieu?.state == 1){
            place_target = true
        }

        return place_target
    }


    fun getCaptureDateMessage(): String{
        return "Cette oeuvre a été capturée le " + lieu?.date_photo
    }

    fun getComment(): String{
        return "Commentaire: "+ lieu?.comment
    }




}