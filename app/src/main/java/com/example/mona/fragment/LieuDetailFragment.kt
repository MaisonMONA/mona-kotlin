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
import com.example.mona.databinding.FragmentLieuItemBinding
import com.example.mona.entity.Lieu
import com.example.mona.entity.Oeuvre
import com.example.mona.viewmodels.LieuDetailViewModel
import com.example.mona.viewmodels.LieuDetailViewModelFactory
import com.example.mona.viewmodels.LieuViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LieuDetailFragment () : Fragment() {

    //View Models
    private val lieuViewModel: LieuViewModel by viewModels()
    private lateinit var lieuDetailViewModel: LieuDetailViewModel
    private val safeArgs : LieuDetailFragmentArgs by navArgs()

    //Photo Attributes
    private val REQUEST_TAKE_PHOTO = 1
    private lateinit var currentPhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lieuDetailViewModel = ViewModelProviders.of(this, LieuDetailViewModelFactory(requireActivity().application, safeArgs.itemSelected.id)
        ).get(LieuDetailViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentLieuItemBinding>(
            inflater, R.layout.fragment_lieu_item, container, false
        ).apply {
            viewModel = lieuDetailViewModel
            lifecycleOwner = viewLifecycleOwner

            callback = object : Callback{
                override fun updateTarget(lieu: Lieu) {
                    lieu?.let {
                        if (lieu.state == null){

                            fab.drawable.mutate().setTint(ContextCompat.getColor(requireContext(), R.color.black))

                            findNavController().popBackStack(R.id.fragmentViewPager_dest,false)

                            lieuDetailViewModel.updateTarget(lieu.id,1)

                            Toast.makeText(requireActivity(), "Oeuvre "+lieu.title+" ciblé", Toast.LENGTH_LONG).show()

                        }

                        if(lieu.state == 1){
                            fab.drawable.mutate().setTint(ContextCompat.getColor(requireContext(), R.color.white))

                            findNavController().popBackStack(R.id.fragmentViewPager_dest,false)

                            lieuDetailViewModel.updateTarget(lieu.id, null)

                            Toast.makeText(requireActivity(), "Oeuvre "+lieu.title+" n'est plus ciblé", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun captureLieu(lieu: Lieu) {
                    dispatchTakePictureIntent(lieu)
                }

                override fun openMap(lieu: Lieu) {
                    val action = LieuDetailFragmentDirections.openLieuMap(lieu)
                    findNavController().navigate(action)
                }
            }

            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

        }
        return binding.root
    }

    private fun dispatchTakePictureIntent(lieu: Lieu) {
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

                            lieuViewModel.updatePath(lieu.id, currentPhotoPath)

                            val action = LieuDetailFragmentDirections.itemToRating(lieu)
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
        fun updateTarget(lieu:Lieu)

        fun openMap(lieu:Lieu)

        fun captureLieu(lieu: Lieu)
    }

}