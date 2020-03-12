package com.example.mona

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mona.entity.Oeuvre
import kotlinx.coroutines.launch

// Class extends AndroidViewModel and requires application as a parameter.
class OeuvreViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: OeuvreRepository
    // LiveData gives us updated words when they change.
    val oeuvreList: LiveData<List<Oeuvre>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val wordsDao = OeuvreDatabase.getDatabase(application).oeuvreDAO()
        repository = OeuvreRepository(wordsDao)
        oeuvreList = repository.oeuvreList
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(oeuvre: Oeuvre) = viewModelScope.launch {
        repository.insert(oeuvre)
    }

    fun updateOeuvre(id: Int, itemRating:Float?, comment:String?, state_collected : Int?, currentPhotoPath:String?, date:String?) = viewModelScope.launch {
        repository.updateArtwork(id,itemRating,comment,state_collected,currentPhotoPath,date)
    }
}