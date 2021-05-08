package com.maison.mona.fragment

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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.maison.mona.R
import com.maison.mona.data.SaveSharedPreference
import com.maison.mona.entity.Badge_2
import com.maison.mona.task.SaveOeuvre
import com.maison.mona.viewmodels.BadgeViewModel
import com.maison.mona.viewmodels.OeuvreViewModel
import kotlinx.android.synthetic.main.fragment_item_rating.view.*
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class OeuvreRatingFragment : Fragment() {

    val safeArgs : OeuvreRatingFragmentArgs by navArgs()
    private val oeuvreViewModel: OeuvreViewModel by viewModels()
    private val badgeViewModel: BadgeViewModel by viewModels()

    private var newBadge = mutableListOf<Badge_2>()

    /*private val repository: BadgeRepository
    init{
        val badgeDAO = BadgeDatabase.getDatabase(
            context.applicationContext,
            viewModelScope
        ).badgesDAO()
        repository = BadgeRepository.getInstance(badgeDAO)
    }*/


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

            var state: Int?

            if(SaveSharedPreference.isOnline(requireContext())){
                state = 2
            }else{
                state = 3
            }

            val date = getDate().toString()

            if (oeuvreIdServeur != null) {
                oeuvreViewModel.updateRating(oeuvreId, rating, comment, state, date)
            }
            Toast.makeText(requireActivity(), "Oeuvre #" + oeuvreId + " ajoutée", Toast.LENGTH_LONG).show()
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

            oeuvreViewModel.collectedList.observe(viewLifecycleOwner, Observer {collected ->

                badgeViewModel.badgesList.observe(viewLifecycleOwner, Observer { badgeList ->
                    Log.d("SAVE", "OeuvreRating : " + badgeList.toString())

                    for(badge in badgeList){
                        if(!badge.isCollected){
                            if(badge.optional_args!!.contains("borough")) {
                                var borough = badge.optional_args.substringAfter(":'").substringBefore("'}")
                                if (safeArgs.oeuvre.borough == borough && collected.filter { it.borough == borough }.size == badge.goal) {
                                    newBadge.add(badge)
                                    badgeViewModel.updateCollected(badge.id, true)

                                    var popup = PopUpManagerFragment()
                                    popup.onAttach(requireContext())
                                    popup.onButtonShowPopupWindowClick(view, findNavController(), badge)

                                    getFragmentManager()?.executePendingTransactions()
                                }
                            } else if(collected.size == badge.goal){
                                newBadge.add(badge)
                                badgeViewModel.updateCollected(badge.id, true)
                                var popup = PopUpManagerFragment()
                                popup.onAttach(requireContext())
                                popup.onButtonShowPopupWindowClick(view, findNavController(), badge)

                                getFragmentManager()?.executePendingTransactions()
                            }
                        }
                    }
                    if(newBadge.isEmpty()){
                        findNavController().popBackStack(R.id.fragmentViewPager_dest,false)
                    }
                })
            })
        }
    }

    private fun getDate(): String? {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd")
        val date = Date()
        return dateFormat.format(date)
    }
}
