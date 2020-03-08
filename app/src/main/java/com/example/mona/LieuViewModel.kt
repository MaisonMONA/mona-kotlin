package com.example.mona

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mona.entity.Lieu
import com.example.mona.entity.Oeuvre
import kotlinx.coroutines.launch

// Class extends AndroidViewModel and requires application as a parameter.
// You can also use a ViewModel to share data between fragments.
// https://developer.android.com/topic/libraries/architecture/viewmodel
class LieuViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: LieuRepository
    // LiveData gives us updated words when they change.
    val lieuList: LiveData<List<Lieu>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val lieuDAO = LieuDatabase.getDatabase(application,viewModelScope).lieuDAO()
        repository = LieuRepository(lieuDAO)
        lieuList = repository.lieuList
    }

    fun insertAll(lieuList: List<Lieu>) = viewModelScope.launch {
        repository.insertAll(lieuList)
    }


}

//to bind view models in different fragments visit
//fragment ktx at https://developer.android.com/kotlin/ktx#fragment
//share data between fragments at https://developer.android.com/topic/libraries/architecture/viewmodel.html