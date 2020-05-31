package com.example.mona.fragment

import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_item_rating.view.*
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

        view.done_rating_button.setOnClickListener {

            val ratingBar = view.findViewById<RatingBar>(R.id.rating)
            val rating  = ratingBar.rating

            val itemComment = view.findViewById<TextView>(R.id.comment)
            val comment = itemComment.text.toString()

            val state = 2

            val date = getDate().toString()


            oeuvreViewModel.updateRating(oeuvreId, rating, comment, state, date)

            Toast.makeText(requireActivity(), "Oeuvre #"+oeuvreId+" ajoutée", Toast.LENGTH_LONG).show()

            //Pop everything from the stack that is not the Home Pager
            findNavController().popBackStack(R.id.fragmentViewPager_dest,false)
        }
    }

    private fun getDate(): String? {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd")
        val date = Date()
        return dateFormat.format(date)
    }

}
