package com.maison.mona.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maison.mona.data.OeuvreDatabase
import com.maison.mona.data.OeuvreRepository
import com.maison.mona.entity.Oeuvre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Double.parseDouble

//View Model for the single artwork displayed on the UI to bind properly
class OeuvreDetailViewModel(application: Application, private var oeuvreId: Int): AndroidViewModel(application) {
    private var isArt = false;
    private var isPlaces = false;
    private var isHeritages = false;

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
            oeuvre = repository.getArticleById(oeuvreId)
        }
    }


    fun updateTarget(oeuvreId: Int, target: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTarget(oeuvreId,target)
        }
    }


    fun isCollected(): Boolean = oeuvre?.state == 2


    fun isTarget(): Boolean = oeuvre?.state == 1


    fun getArtists() : String {
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

        isArt = true;
        isPlaces = false;
        isHeritages = false;
        return artistString
    }

    fun getDate() : String ? {
        val dateAt = oeuvre?.produced_at
        val dateEnd = oeuvre?.produced_end

        return if (dateAt == dateEnd || dateEnd == null) {
            dateAt
        } else
            "$dateAt, $dateEnd"
    }

    //Function used for sous-usages for Patrimoine or category for Artworks
    fun getSousUsageOrCategory() : String{

        val category = oeuvre?.category?.fr

        if(category != null){
            return category
        }
        var sousUsagesString = ""
        val array = oeuvre?.sousUsages


        if (array != null) {
            for (element in array){
                sousUsagesString += "\n" + element

            }
        }
        isArt = false;
        isPlaces = false;
        isHeritages = true;

        return sousUsagesString
    }

    // Borough and Territory for Patrimoine and Subcategory for Artworks
    fun getBoroughTerritorySubcategory() : String{

        var subcategory = oeuvre?.subcategory?.fr

        if(subcategory !== null){
            return subcategory
        }
        var borough = oeuvre?.borough

        if(borough == null){
            return ""

        }

        if(borough == ""){
            return oeuvre?.territory+""
        }
        return borough +", "+oeuvre?.territory
    }

    //Parsing through dimensions
    fun getDimensionsOrStatus() : String {
        val dimensions: MutableList<Int> = ArrayList()
        var dimensionsString = ""
        var metric = ""
        val array = oeuvre?.dimension
        val statusPatrimoine = oeuvre?.status // if it's Patrimoine we store the status

        if (statusPatrimoine !== null) {
            return statusPatrimoine // if it's Patrimoine we return status instead of dimensions
        }

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

                if (numeric) {
                    dimensions.add(parseDouble(element.toString()).toInt())
                } else {
                    metric += element.toString()
                }
            }

            // Form final string of dimensions well formed
            for ((i, dim) in dimensions.withIndex()) {
                dimensionsString += "$dim "

                if ((i + 1) == dimensions.size) {
                    dimensionsString += metric
                } else {
                    dimensionsString += "× "
                }
            }
        }

        return dimensionsString
    }

    fun getMaterialsOrAdresses() : String {

        val adresses = oeuvre?.addresses
        var adressesString = ""

        if(adresses != null) {
            for (element in adresses) {
                adressesString += element
            }
            return adressesString + "\n"
        }

        var materialsString = ""
        val array = oeuvre?.materials

        array?.let {
            for ((i, element) in array.withIndex()) {
                materialsString += element.fr

                // Adding comma unless it's the last element
                if ((i + 1) != array.size) {
                    materialsString += ", "
                }
            }
        }

        return materialsString
    }

    //description and synthesis
    fun getTechniques() : String {
        val description = oeuvre?.description
        val synthesis = oeuvre?.synthesis


        if (description !== null && synthesis !== null){
            return description + "\n" + synthesis
        }

        if (description !== null) {
            return description
        }

        var techniquesString = ""
        val array = oeuvre?.techniques

        array?.let {
            for ((i, element) in array.withIndex()) {
                techniquesString += element.fr

                // Adding comma unless it's the last element
                if ((i + 1) != array.size) {
                    techniquesString += ", "
                }
            }
        }

        return techniquesString
    }


    fun getCaptureDateMessage(): String{
        return "Date de création: " + oeuvre?.date_photo
    }


    fun isOeuvreSent(): Boolean = oeuvre?.isSent ?: false


    fun getTypeOfContent(): String{
        if (isHeritages) {
            isHeritages = false
            return "heritages"
        } else if (isArt) {
            isArt = false
            return "art"
        } else {
            isPlaces = false
            return "places"
        }
    }

}