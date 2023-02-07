package com.maison.mona.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maison.mona.R
import com.maison.mona.data.OeuvreDatabase
import com.maison.mona.databinding.FragmentOdjBinding
import com.maison.mona.entity.Oeuvre
import com.maison.mona.viewmodels.OeuvreDetailViewModel
import com.maison.mona.viewmodels.OeuvreDetailViewModelFactory
import com.maison.mona.viewmodels.OeuvreViewModel
import kotlinx.android.synthetic.main.fragment_collection.*
import kotlinx.android.synthetic.main.fragment_odj.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.measureTimeMillis

//a clean
class OeuvreJourFragment : Fragment() {

    //callback des fonctions pour l'oeuvre
    interface Callback {
        fun openMap(oeuvre:Oeuvre)
        fun captureOeuvre(oeuvre: Oeuvre)
        fun updateOeuvre(oeuvre: Oeuvre)
    }

    //View Models
    private val oeuvreViewModel : OeuvreViewModel by viewModels()
    private lateinit var oeuvreDetailViewModel: OeuvreDetailViewModel
    private var oeuvreId: Int = 0

    private var odjTop: ConstraintLayout? = null
    private var odjBottom: LinearLayout? = null
    private var odjCardview: CardView? = null

    //Photo Attributes
    private val REQUEST_TAKE_PHOTO = 1
    private lateinit var currentPhotoPath: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //Artwork of the day is represented by the id of the artwork that
        //represents the current day of the year
        val calendar = Calendar.getInstance()

        // Building the seed for Random as an Int {DAY OF YEAR}{YEAR} so it is unique
        val random = kotlin.random.Random(calendar[Calendar.DAY_OF_YEAR] * 10000 + calendar[Calendar.YEAR])
        oeuvreId = random.nextInt(from=1, until=OeuvreDatabase.getNbOeuvres())


        // Obtain the artwork associated with `oeuvreId`
        oeuvreDetailViewModel = ViewModelProviders.of(
            this,
            OeuvreDetailViewModelFactory(requireActivity().application, oeuvreId)
        ).get(OeuvreDetailViewModel::class.java)


        val binding = DataBindingUtil.inflate<FragmentOdjBinding>(inflater, R.layout.fragment_odj, container, false).apply {
            viewModel = oeuvreDetailViewModel
            lifecycleOwner = viewLifecycleOwner

            callback = object : Callback {
                override fun captureOeuvre(oeuvre: Oeuvre) {
                    dispatchTakePictureIntent(oeuvre)
                }

                override fun openMap(oeuvre: Oeuvre) {
                    val action = HomeViewPagerFragmentDirections.odjToMap(oeuvre)
                    findNavController().navigate(action)
                }

                override fun updateOeuvre(oeuvre: Oeuvre) {
                    oeuvre.let {
                        if (oeuvre.state == null) {
                            oeuvreDetailViewModel.updateTarget(oeuvre.id,1)
                            Toast.makeText(requireActivity(), "${oeuvre.title} ciblé", Toast.LENGTH_LONG).show()
                        }

                        if (oeuvre.state == 1) { //from target to non target
                            oeuvreDetailViewModel.updateTarget(oeuvre.id,null)
                            Toast.makeText(requireActivity(), "${oeuvre.title} n'est plus ciblé", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        odjCardview = binding.odjCardview
        odjBottom = binding.odjConstraintLayoutAnim2
        odjTop = binding.odjConstraintLayoutAnim1

        val transition = AutoTransition()
        transition.duration = 1000

        // Apply transition to display artwork of the day
        odjBottom?.setOnClickListener {
            if (odjTop?.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(odjCardview, transition)
                odjTop?.visibility = View.VISIBLE
            }
        }

        Handler().postDelayed({
            odjBottom?.callOnClick()
        }, 1500L)

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun dispatchTakePictureIntent(oeuvre: Oeuvre) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        takePictureIntent.resolveActivity(requireActivity().packageManager)

        // Create the File where the photo should go
        val photoFile: File? = try {
            val imgFile = createImageFile()
            imgFile
        } catch (ex: IOException) {
            // Error occurred while creating the File
            null
        }

        // Continue only if the file was successfully created
        if (photoFile != null) {
            context?.let {
                val photoURI: Uri = FileProvider.getUriForFile(it, "com.maison.android.fileprovider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)

                onActivityResult(REQUEST_TAKE_PHOTO, Activity.RESULT_OK, takePictureIntent).let {
                    oeuvreViewModel.updatePath(oeuvre.id, currentPhotoPath)
                    Log.d("Save", "Current: $currentPhotoPath")
                    val action = HomeViewPagerFragmentDirections.odjToRating(oeuvre,currentPhotoPath)
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        odj_cardview.getLayoutTransition().setAnimateParentHierarchy(false);
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", // file name
            ".jpg",               // file extension
            storageDir            // directory
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
}
