package com.maison.mona.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.maison.mona.R
import com.maison.mona.databinding.FragmentOdjNewBinding
import com.maison.mona.databinding.FragmentOdjBinding
import com.maison.mona.entity.Oeuvre
import com.maison.mona.viewmodels.OeuvreDetailViewModel
import com.maison.mona.viewmodels.OeuvreDetailViewModelFactory
import com.maison.mona.viewmodels.OeuvreViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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

    //Photo Attributes
    private val REQUEST_TAKE_PHOTO = 1
    private lateinit var currentPhotoPath: String

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        //TODO a enlever
        //Artwork of the way is represented by the id of the artwork that
        //represents the the current day of the year
        val calendar = Calendar.getInstance()
        oeuvreId = calendar[Calendar.DAY_OF_YEAR] * (calendar[Calendar.MONTH] / 2)

        //Select a random artwork from those the user didn't collect yet

        oeuvreDetailViewModel = ViewModelProviders.of(
            this,
            OeuvreDetailViewModelFactory(requireActivity().application,
                oeuvreId
            )
        ).get(OeuvreDetailViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentOdjNewBinding>(
            inflater, R.layout.fragment_odj_new, container, false
        ).apply{
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
                        if(oeuvre.state == null){
                            oeuvreDetailViewModel.updateTarget(oeuvre.id,1)
                            Toast.makeText(requireActivity(), oeuvre.title+" ciblé", Toast.LENGTH_LONG).show()
                        }

                        if(oeuvre.state == 1){ //from target to non target
                            oeuvreDetailViewModel.updateTarget(oeuvre.id,null)
                            Toast.makeText(requireActivity(), oeuvre.title+" n'est plus ciblé", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

//        val binding = DataBindingUtil.inflate<FragmentOdjBinding>(
//            inflater, R.layout.fragment_odj, container, false
//        ).apply {
//            viewModel = oeuvreDetailViewModel
//            lifecycleOwner = viewLifecycleOwner
//
//            callback = object : Callback {
//                override fun captureOeuvre(oeuvre: Oeuvre) {
//                    dispatchTakePictureIntent(oeuvre)
//                }
//
//                override fun openMap(oeuvre: Oeuvre) {
//                    val action = HomeViewPagerFragmentDirections.odjToMap(oeuvre)
//                    findNavController().navigate(action)
//                }
//            }
//        }

        setHasOptionsMenu(true)

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        with(oeuvreDetailViewModel){
            if (!isCollected()){
                inflater.inflate(R.menu.odj_menu, menu)
            } else {
                //do not inflate menu
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.target_odj -> {
                updateData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateData() {
        with(oeuvreDetailViewModel) {
            if (isTarget()) {
                oeuvreDetailViewModel.updateTarget(oeuvreId, 0)
                Toast.makeText(requireActivity(), R.string.oeuvre_untargetted, Toast.LENGTH_SHORT).show()
            } else {
                oeuvreDetailViewModel.updateTarget(oeuvreId, 1)
                Toast.makeText(requireActivity(), R.string.oeuvre_targetted, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dispatchTakePictureIntent(oeuvre: Oeuvre) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    context?.let {
                        val photoURI: Uri = FileProvider.getUriForFile(it, "com.maison.android.fileprovider", photoFile)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)

                        onActivityResult(REQUEST_TAKE_PHOTO, Activity.RESULT_OK, takePictureIntent).let {
                            oeuvreViewModel.updatePath(oeuvre.id, currentPhotoPath)
                            Log.d("Save","Current: " + currentPhotoPath)
                            val action = HomeViewPagerFragmentDirections.odjToRating(oeuvre,currentPhotoPath)
                            findNavController().navigate(action)
                        }
                    }

                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
}
