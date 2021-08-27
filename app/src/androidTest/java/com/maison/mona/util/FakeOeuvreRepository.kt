package com.maison.mona.util

import androidx.lifecycle.MutableLiveData
import com.maison.mona.entity.Oeuvre

class FakeOeuvreRepository {
    private val oeuvreList: MutableList<Oeuvre> = mutableListOf()
    private val observableOeuvreList = MutableLiveData<List<Oeuvre>>(oeuvreList)

    private fun refreshLiveData(){
        observableOeuvreList.postValue(oeuvreList)
    }

    fun insertAll(listOeuvre: List<Oeuvre>){
        oeuvreList.addAll(listOeuvre)
    }
    fun insert(oeuvre: Oeuvre){
        oeuvreList.add(oeuvre)
    }


}