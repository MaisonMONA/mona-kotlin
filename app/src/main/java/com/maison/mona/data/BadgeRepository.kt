package com.maison.mona.data

import androidx.lifecycle.LiveData
import com.maison.mona.entity.Badge_2
import com.maison.mona.entity.Oeuvre

class BadgeRepository(private val badgesDao:BadgeDAO){

    val badgesList: LiveData<List<Badge_2>> = badgesDao.getAll()

    var newBadgesUnlocked: MutableList<Badge_2> = mutableListOf()

    fun checkIfBadgesUnlocked(oeuvre:Oeuvre): Boolean{
        if(!newBadgesUnlocked.isEmpty()){
            newBadgesUnlocked = mutableListOf()
        }

        val badgesQuartier: LiveData<List<Badge_2>> = badgesDao.getBadgeByQuartier(oeuvre.borough!!)

        for(badge in badgesQuartier.value!!.listIterator()){
            badge.already_collected += 1

            if(badge.already_collected == badge.collected_goal){
                badge.isCollected = true;
                newBadgesUnlocked.add(badge)
            }
        }

        return !newBadgesUnlocked.isEmpty()
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