package com.example.mona.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mona.viewmodels.OeuvreViewModel
import com.example.mona.R
import com.example.mona.entity.Oeuvre
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.mona.databinding.FragmentOdjBinding
import com.example.mona.viewmodels.OeuvreDetailViewModel
import com.example.mona.viewmodels.OeuvreDetailViewModelFactory
import kotlinx.android.synthetic.main.fragment_oeuvre_jour.view.*
import java.util.*

class OeuvreJourFragment : Fragment() {

    //View Models
    private val oeuvreViewModel : OeuvreViewModel by viewModels()
    private lateinit var oeuvreDetailViewModel: OeuvreDetailViewModel

    //Photo Attributes
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        //Artwork of the way is represented by the id of the artwork that
        //represents the the current day of the year
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar[Calendar.DAY_OF_YEAR]

        oeuvreDetailViewModel = ViewModelProviders.of(this, OeuvreDetailViewModelFactory(requireActivity().application, dayOfYear)
        ).get(OeuvreDetailViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentOdjBinding>(
            inflater, R.layout.fragment_odj, container, false
        ).apply {
            viewModel = oeuvreDetailViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root

    }

}
