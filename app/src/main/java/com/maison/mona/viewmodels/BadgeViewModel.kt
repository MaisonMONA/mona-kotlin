package com.maison.mona.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.maison.mona.data.BadgeDatabase
import com.maison.mona.data.BadgeRepository
import com.maison.mona.entity.Badge_2

class BadgeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BadgeRepository

    val badgesList: LiveData<List<Badge_2>>

    init{
        var database = BadgeDatabase

        val badgesDao = database.getDatabase(application, viewModelScope).badgesDAO()
        repository = BadgeRepository(badgesDao)
        badgesList = repository.badgesList
    }
}