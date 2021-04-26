package com.maison.mona.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.maison.mona.data.BadgeDatabase
import com.maison.mona.data.BadgeRepository
import com.maison.mona.entity.Badge_2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BadgeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BadgeRepository

    val badgesList: LiveData<List<Badge_2>>

    init{
        var database = BadgeDatabase

        val badgesDao = database.getDatabase(application, viewModelScope).badgesDAO()

        repository = BadgeRepository(badgesDao)

        badgesList = repository.badgesList
    }

    fun updateCollected(id: Int, collected: Boolean?){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateCollected(id, collected)
        }
    }

//    private fun isNetworkConnected(): Boolean {
//        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
//        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
//        return isConnected
//    }
}