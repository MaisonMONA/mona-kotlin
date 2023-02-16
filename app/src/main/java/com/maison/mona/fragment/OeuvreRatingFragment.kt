package com.maison.mona.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
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
import com.maison.mona.R
import com.maison.mona.data.SaveSharedPreference
import com.maison.mona.entity.Badge
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

    private var newBadge = mutableListOf<Badge>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_item_rating, container, false)
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

            val state = if (SaveSharedPreference.isOnline(requireContext())) {
                2
            } else {
                3
            }

            val date = getDate().toString()

            if (oeuvreIdServeur != null) {
                oeuvreViewModel.updateRating(oeuvreId, rating, comment, state, date)
            }

            Toast.makeText(requireActivity(), "Oeuvre #$oeuvreId ajoutée", Toast.LENGTH_LONG).show()

            // Save the informations in the database
            if (SaveSharedPreference.isOnline(requireContext())) {  // Must be online
                val sendOeuvre = activity?.let { it1 -> SaveOeuvre(it1) }

                sendOeuvre?.execute(
                    oeuvreIdServeur.toString(),
                    rating.toInt().toString(),
                    comment,
                    imagePath,
                    safeArgs.oeuvre.type
                )

                val response = sendOeuvre?.get()

                if (response != "" && response != null) {
                    val reader = JSONObject(response)

                    if (reader.has("errors")) {
                        Log.d("Save", "Erreur Save reader")
                        val errors = reader.getJSONObject("errors")
                        Log.d("Save", errors.toString())
                    }
                }
            }

            oeuvreViewModel.collectedList.observe(viewLifecycleOwner) { collected ->
                badgeViewModel.badgesList.observe(viewLifecycleOwner) { badgeList ->
                    for (badge in badgeList) {
                        if (!badge.isCollected) {
                            val args = badge.optional_args!!

                            if (args.contains("borough")) {
                                if (collected.count { args.contains(it.borough.toString()) } == badge.goal) {
                                    addBadge(badge)
                                } else if (args.contains("Rivière-des-Prairies") &&
                                           collected.count { it.borough?.contains("Rivière-des-Prairies") == true } == badge.goal) {
                                    // DO NOT remove the `== true` part, it ensures the variable is not null!!
                                    addBadge(badge)
                                }
                            } else if (collected.size == badge.goal && args.length < 3) {
                                addBadge(badge)
                            } else if (args.contains("category")) {
                                if (collected.filter { args.contains(it.category?.en.toString())}.size == badge.goal || collected.filter { args.contains(it.category?.fr.toString())}.size == badge.goal){
                                    addBadge(badge)
                                }
                            } else if (args.contains("collection")) {
                                val collection = "Université de Montréal"
                                if (safeArgs.oeuvre.collection == collection && collected.filter { it.collection == collection }.size == badge.goal) {
                                    addBadge(badge)
                                }
                            }
                        }
                    }


                }
            }

            val mHandler = Handler()
            mHandler.postDelayed({
                Log.d("BADGES", newBadge.toString())
                if(newBadge.isEmpty()){
                    findNavController().popBackStack(R.id.fragmentViewPager_dest,false)
                } else {
                    val popup = PopUpManagerFragment()
                    popup.onAttach(requireContext())
                    popup.createPopUpsBadges(view, findNavController(), newBadge)

                    this.childFragmentManager.executePendingTransactions()

                }
            }, 500L)
        }
    }

    fun addBadge(badge: Badge){
        newBadge.add(badge)
        badgeViewModel.updateCollected(badge.id, true)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(): String? {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd")
        val date = Date()
        return dateFormat.format(date)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}
