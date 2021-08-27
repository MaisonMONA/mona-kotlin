package com.maison.mona.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maison.mona.data.OeuvreDatabase
import com.maison.mona.data.OeuvreRepository
import com.maison.mona.entity.Oeuvre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Double.parseDouble

//View Model for the single artwork displayed on the UI to bind properly
class OeuvreDetailViewModel(application: Application, private var oeuvreId: Int): AndroidViewModel(application){

    private val repository: OeuvreRepository
    var oeuvre: Oeuvre? = null
    init {
        // Gets reference to OeuvreDao from OeuvreDatabase to construct
        // the correct OeuvreRepository.
        val oeuvreDao = OeuvreDatabase.getDatabase(
            application,
            viewModelScope
        ).oeuvreDAO()
        repository = OeuvreRepository.getInstance(oeuvreDao)
        getOeuvre()
    }

    private fun getOeuvre(){
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("OEUVRES", "oeuvre dans detailviewmodel avant")
            oeuvre = repository.getArticleById(oeuvreId)
            Log.d("OEUVRES", "oeuvre dans oeuvredetailviewmodel : " + oeuvre?.title.toString())
        }
    }

    fun updateTarget(oeuvreId: Int, target: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTarget(oeuvreId,target)
        }
    }

    fun isCollected(): Boolean{
        var artworkCollected = false

        //State check to see if artwork is collected
        if(oeuvre?.state == 2){
            artworkCollected = true
        }

        return artworkCollected
    }

    fun isTarget(): Boolean{
        var artworkTarget = false

        //State checuk to see if artwork is collected
        if(oeuvre?.state == 1){
            artworkTarget = true
        }

        return artworkTarget
    }

    fun getArtists() : String{
        var artistString = ""

        val array = oeuvre?.artists

        array?.let {
            for (element in array){
                //If last element of array we dont put a comma and vice versa
                artistString += if(array.last().name != element.name){
                    val temp = element.name + ", "
                    temp
                } else {
                    val temp = element.name
                    temp
                }
            }
        }

        return artistString
    }

    //Parsing through dimensions
    fun getDimensions() : String{
        val dimensions: MutableList<Int> = ArrayList()
        var dimensionsString = ""
        var metric = ""
        val array = oeuvre?.dimension

        //We suppose array is not empty
        array?.let {
            //We iterate through to find out whats a number and what is the metric
            //Since data on the server is not formed properly
            for (element in array){
                var numeric = true

                try {
                    parseDouble(element.toString())
                } catch (e: NumberFormatException) {
                    numeric = false
                }

                if (numeric){
                    val num = parseDouble(element.toString()).toInt()
                    dimensions.add(num)
                }else{
                    metric += element.toString()
                }
            }

            //Form final string of dimensions well formed
            for (dim in dimensions){
                dimensionsString += if (dimensions.last() != dim){
                    val temp = "$dim x "
                    temp
                } else {
                    val temp = "$dim $metric"
                    temp
                }
            }
        }

        return dimensionsString
    }

    fun getMaterials() : String{
        var materialsString = ""
        val array = oeuvre?.materials

        array?.let {
            for (element in array){
                //If last element of array we dont put a comma and vice versa
                materialsString += if(array.last().fr != element.fr){
                    val temp = element.fr + ", "
                    temp
                } else {
                    val temp = element.fr
                    temp
                }
            }
        }

        return materialsString
    }

    fun getTechniques() : String{
        var techniquesString = ""
        val array = oeuvre?.techniques

        array?.let {
            for (element in array){
                //If last element of array we dont put a comma and vice versa
                techniquesString += if(array.last().fr != element.fr){
                    val temp = element.fr + ", "
                    temp
                } else {
                    val temp = element.fr
                    temp
                }
            }
        }

        return techniquesString
    }

    fun getCaptureDateMessage(): String{
        return "Cette oeuvre a été collectionnée le " + oeuvre?.date_photo
    }

    fun isOeuvreSent(): Boolean? {
        return if (oeuvre != null) oeuvre?.isSent else false
    }

}