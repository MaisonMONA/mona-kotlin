package com.maison.mona.util

import androidx.lifecycle.MutableLiveData
import com.maison.mona.entity.Badge

class FakeBadgeRepository {
    private val badgesList: MutableList<Badge> = mutableListOf()
    private val observableBadgesList = MutableLiveData<List<Badge>>(badgesList)

    private fun refreshLiveData(){
        observableBadgesList.postValue(badgesList)
    }

    fun insertAll(listBadge: List<Badge>){
        badgesList.addAll(listBadge)
    }

    fun updateCollected(id: Int, collected: Boolean){
        badgesList[id].isCollected = collected
    }

    fun setGoal(id: Int, goal:Int?){
        badgesList[id].goal = goal
    }
}