package com.maison.mona.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.maison.mona.data.BadgeDatabase
import com.maison.mona.data.BadgeRepository
import com.maison.mona.entity.Badge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BadgeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BadgeRepository

    val badgesList: LiveData<List<Badge>>

    init{
        val database = BadgeDatabase

        val badgesDao = database.getDatabase(application, viewModelScope).badgesDAO()

        repository = BadgeRepository(badgesDao)

        badgesList = repository.badgesList
    }

    fun updateCollected(id: Int, collected: Boolean?){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateCollected(id, collected)
        }
    }

    fun setGoal(id: Int, goal: Int?){
        viewModelScope.launch(Dispatchers.IO){
            repository.setGoal(id, goal)
        }
    }
}