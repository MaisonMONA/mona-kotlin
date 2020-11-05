package com.example.mona.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mona.viewmodels.OeuvreViewModel
import com.example.mona.R
import com.example.mona.data.SaveSharedPreference
import com.example.mona.task.SaveOeuvre
import kotlinx.android.synthetic.main.fragment_item_rating.view.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class OeuvreRatingFragment : Fragment() {

    val safeArgs : OeuvreRatingFragmentArgs by navArgs()
    private val oeuvreViewModel: OeuvreViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_item_rating, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val oeuvreId = safeArgs.oeuvre.id
        val oeuvreIdServeur = safeArgs.oeuvre.idServer
        val imagePath = safeArgs.imagePath

        view.done_rating_button.setOnClickListener {

            val ratingBar = view.findViewById<RatingBar>(R.id.rating)
            val rating  = ratingBar.rating

            val itemComment = view.findViewById<TextView>(R.id.comment)
            val comment = itemComment.text.toString()

            val state = 2

            val date = getDate().toString()

            if (oeuvreIdServeur != null) {
                oeuvreViewModel.updateRating(oeuvreId, rating, comment, state, date)
            }
            Toast.makeText(requireActivity(), "Oeuvre #"+oeuvreId+" ajoutée", Toast.LENGTH_LONG).show()
            //Save the informations in the database
            if(SaveSharedPreference.isOnline(requireContext())) {//Must be online

                    Log.d("Save", "Commence Save")
                    val sendOeuvre = activity?.let { it1 -> SaveOeuvre(it1) }

                    if (sendOeuvre != null) {
                        Log.d("Save", "obtien contexte")
                        Log.d("Save", "Path: " + imagePath)
                        sendOeuvre.execute(
                            oeuvreIdServeur.toString(),
                            rating.toInt().toString(),
                            comment,
                            imagePath,
                            safeArgs.oeuvre.type
                        )
                    }
                    var response = sendOeuvre?.get()
                    if (response != "" && response != null) {
                        Log.d("Save", "reponse: " + response)
                        val reader = JSONObject(response)
                        if (reader.has("errors")) {
                            Log.d("Save", "Erreur Save reader");
                            val errors = reader.getJSONObject("errors")
                            Log.d("Save", errors.toString())
                        }
                    }
                }

            //Pop everything from the stack that is not the Home Pager
            //findNavController().popBackStack(R.id.fragmentViewPager_dest,false)
        }
    }

    private fun getDate(): String? {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd")
        val date = Date()
        return dateFormat.format(date)
    }
}
