package com.example.mona.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mona.R
import com.example.mona.data.OeuvreDatabase
import com.example.mona.data.OeuvreRepository
import com.example.mona.entity.Bilingual
import com.example.mona.entity.Oeuvre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt


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

    fun getOeuvre(){
        viewModelScope.launch(Dispatchers.IO) {
            oeuvre = repository.getOeuvre(oeuvreId)
        }
    }

    fun updateTarget(oeuvreId: Int, target: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTarget(oeuvreId,target)
        }
    }


    fun isCollected(): Boolean{

        var artwork_collected = false

        //State checuk to see if artwork is collected
        if(oeuvre?.state == 2){
            artwork_collected = true
        }

        return artwork_collected
    }

    fun isTarget(): Boolean{

        var artwork_target = false

        //State checuk to see if artwork is collected
        if(oeuvre?.state == 1){
            artwork_target = true
        }

        return artwork_target
    }


    fun getArtists() : String{
        var artist_string = ""

        val array = oeuvre?.artists

        array?.let {

            for (element in array){
                //If last element of array we dont put a comma and vice versa
                if(array.last().name != element.name){
                    var temp = element.name + ", "
                    artist_string += temp
                } else {
                    var temp = element.name
                    artist_string += temp
                }
            }
        }

        return artist_string

    }

    //Parsing through dimensions
    fun getDimensions() : String{
        var dimensions: MutableList<Int> = ArrayList()
        var dimensions_string = ""
        var metric = ""

        val array = oeuvre?.dimension

        //We suppose array is not empty
        array?.let {


            //We iterate through to find out whats a number and what is the metric
            //Since data on the server is not formed properly
            for (element in array){

                var numeric = true

                try {
                    val num = parseDouble(element.toString())
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
                if (dimensions.last() != dim){
                    var temp = dim.toString() + " x "
                    dimensions_string += temp
                } else {
                    var temp = dim.toString() + " " + metric
                    dimensions_string += temp
                }
            }

        }

        return dimensions_string

    }

    fun getMaterials() : String{
        var materials_string = ""

        val array = oeuvre?.materials

        array?.let {
            for (element in array){
                //If last element of array we dont put a comma and vice versa
                if(array.last().fr != element.fr){
                    var temp = element.fr + ", "
                    materials_string += temp
                } else {
                    var temp = element.fr
                    materials_string += temp
                }
            }
        }

        return materials_string

    }

    fun getTechniques() : String{
        var techniques_string = ""

        val array = oeuvre?.techniques

        array?.let {
            for (element in array){
                //If last element of array we dont put a comma and vice versa
                if(array.last().fr != element.fr){
                    var temp = element.fr + ", "
                    techniques_string += temp
                } else {
                    var temp = element.fr
                    techniques_string += temp
                }
            }
        }

        return techniques_string

    }

    fun getCaptureDateMessage(): String{
        return "Cette oeuvre a été capturé le " + oeuvre?.date_photo
    }

    fun getComment(): String{
        return "Commentaire: "+ oeuvre?.comment
    }




}