package com.maison.mona.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.maison.mona.entity.Badge_2
import com.maison.mona.entity.Oeuvre

class BadgeRepository(private val badgesDao:BadgeDAO){

    val badgesList: LiveData<List<Badge_2>> = badgesDao.getAll()

    var newBadgesUnlocked: MutableList<Badge_2> = mutableListOf()

    //pour updater si le badge a deja ete collecte ou pas
    suspend fun updateCollected(id: Int, collected: Boolean?){
        badgesDao.updateCollected(id, collected)
    }

    suspend fun setGoal(id: Int, goal:Int?){
        badgesDao.setGoal(id, goal)
    }

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: BadgeRepository? = null

        fun getInstance(badgesDao: BadgeDAO) =
            instance ?: synchronized(this) {
                instance ?: BadgeRepository(badgesDao).also { instance = it }
            }
    }
}