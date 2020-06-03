package com.example.mona.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mona.R
import com.example.mona.databinding.FragmentOeuvreItemBinding
import com.example.mona.entity.Oeuvre
import com.example.mona.viewmodels.OeuvreDetailViewModel
import com.example.mona.viewmodels.OeuvreDetailViewModelFactory
import com.example.mona.viewmodels.OeuvreViewModel
import kotlinx.android.synthetic.main.fragment_oeuvre_item.*
import okhttp3.internal.notify
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class OeuvreDetailFragment () : Fragment() {

    //View Models
    private val oeuvreViewModel: OeuvreViewModel by viewModels()
    private lateinit var oeuvreDetailViewModel: OeuvreDetailViewModel
    private val safeArgs : OeuvreDetailFragmentArgs by navArgs()

    //Photo Attributes
    private val REQUEST_TAKE_PHOTO = 1
    private lateinit var currentPhotoPath: String


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        oeuvreDetailViewModel = ViewModelProviders.of(this, OeuvreDetailViewModelFactory(requireActivity().application, safeArgs.itemSelected.id)
        ).get(OeuvreDetailViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentOeuvreItemBinding>(
            inflater, R.layout.fragment_oeuvre_item, container, false
        ).apply {
            viewModel = oeuvreDetailViewModel
            lifecycleOwner = viewLifecycleOwner

            callback = object : Callback {
                override fun updateTarget(oeuvre: Oeuvre) {
                    oeuvre?.let {
                        //Set state depending on current state of artwork
                        //from non target to target
                        if(oeuvre.state == null){

                            fab.drawable.mutate().setTint(ContextCompat.getColor(requireContext(), R.color.black))

                            findNavController().popBackStack(R.id.fragmentViewPager_dest,false)

                            oeuvreDetailViewModel.updateTarget(oeuvre.id,1)

                            Toast.makeText(requireActivity(), oeuvre.title+" ciblé", Toast.LENGTH_LONG).show()
                        }
                        //from target to non target
                        if(oeuvre.state == 1){ //from target to non target

                            fab.drawable.mutate().setTint(ContextCompat.getColor(requireContext(), R.color.white))

                            findNavController().popBackStack(R.id.fragmentViewPager_dest,false)

                            oeuvreDetailViewModel.updateTarget(oeuvre.id,null)
                            
                            Toast.makeText(requireActivity(), oeuvre.title+" n'est plus ciblé", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun captureOeuvre(oeuvre: Oeuvre) {
                    dispatchTakePictureIntent(oeuvre)
                }

                override fun openMap(oeuvre: Oeuvre) {
                    val action = OeuvreDetailFragmentDirections.openOeuvreMap(oeuvre)
                    findNavController().navigate(action)
                }

            }

            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }



        return binding.root
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

                            val action = OeuvreDetailFragmentDirections.itemToRating(oeuvre)
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
        fun updateTarget(oeuvre:Oeuvre)

        fun openMap(oeuvre:Oeuvre)

        fun captureOeuvre(oeuvre: Oeuvre)
    }



}
