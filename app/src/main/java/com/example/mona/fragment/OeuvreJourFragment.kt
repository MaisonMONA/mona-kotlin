package com.example.mona.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.FileProvider
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class OeuvreJourFragment : Fragment() {

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

        //Artwork of the way is represented by the id of the artwork that
        //represents the the current day of the year
        val calendar = Calendar.getInstance()
        oeuvreId = calendar[Calendar.DAY_OF_YEAR]

        oeuvreDetailViewModel = ViewModelProviders.of(
            this,
            OeuvreDetailViewModelFactory(requireActivity().application,
                oeuvreId
            )
        ).get(OeuvreDetailViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentOdjBinding>(
            inflater, R.layout.fragment_odj, container, false
        ).apply {
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
            }
        }

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
        super.onCreateOptionsMenu(menu, inflater)
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
                        val photoURI: Uri = FileProvider.getUriForFile(it, "com.example.android.fileprovider", photoFile)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)

                        onActivityResult(REQUEST_TAKE_PHOTO, Activity.RESULT_OK, takePictureIntent).let {

                            oeuvreViewModel.updatePath(oeuvre.id, currentPhotoPath)

                            val action = HomeViewPagerFragmentDirections.odjToRating(oeuvre)
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
        val storageDir: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    interface Callback {
        fun openMap(oeuvre:Oeuvre)

        fun captureOeuvre(oeuvre: Oeuvre)
    }
}
