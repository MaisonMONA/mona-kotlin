package com.maison.mona.fragment


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.maison.mona.R
import com.maison.mona.databinding.FragmentOeuvreItemBinding
import com.maison.mona.entity.Oeuvre
import com.maison.mona.fragment.MapFragment
import com.maison.mona.viewmodels.OeuvreDetailViewModel
import com.maison.mona.viewmodels.OeuvreDetailViewModelFactory
import com.maison.mona.viewmodels.OeuvreViewModel
import kotlinx.android.synthetic.main.fragment_oeuvre_item.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class OeuvreDetailFragment : Fragment() {

    // View Models
    private val oeuvreViewModel: OeuvreViewModel by viewModels()
    private lateinit var oeuvreDetailViewModel: OeuvreDetailViewModel
    private val safeArgs : OeuvreDetailFragmentArgs by navArgs()

    // Photo Attributes
    private val REQUEST_TAKE_PHOTO = 1
    private lateinit var currentPhotoPath: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Obtain the artwork associated with `safeArgs.itemSelected.id`
        oeuvreDetailViewModel = ViewModelProvider(
            this,
            OeuvreDetailViewModelFactory(requireActivity().application, safeArgs.itemSelected.id)
        ).get(OeuvreDetailViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentOeuvreItemBinding>(inflater, R.layout.fragment_oeuvre_item, container, false).apply {
            viewModel = oeuvreDetailViewModel
            lifecycleOwner = viewLifecycleOwner
            callback = getProperCallback(fab)

            // If fetching the data failed, set a tiny timeout and try again
            while (oeuvreDetailViewModel.oeuvre?.title == null) {
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel = oeuvreDetailViewModel
                    callback = getProperCallback(fab)
                }, 30L)
            }

            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }

        return binding.root
    }


    private fun getProperCallback(fab: FloatingActionButton): Callback{
        return object : Callback {
            override fun updateTarget(oeuvre: Oeuvre) {
                oeuvre.let {
                    //Set state depending on current state of artwork

                    if (oeuvre.state == null) {  // from non target to target
                        // Change color of the target button icon to black
                        fab.drawable.mutate().setTint(ContextCompat.getColor(requireContext(), R.color.black))

                        findNavController().popBackStack(R.id.fragmentViewPager_dest, false)

                        oeuvreDetailViewModel.updateTarget(oeuvre.id, 1)

                        Toast.makeText(requireActivity(), "${oeuvre.title} ciblé", Toast.LENGTH_LONG).show()
                    }

                    if (oeuvre.state == 1) {  // From target to non target
                        // Change color of the target button icon to white
                        fab.drawable.mutate().setTint(ContextCompat.getColor(requireContext(), R.color.white))

                        // Line below is commented: do not hide artwork page after untargeting!
                        // findNavController().popBackStack(R.id.fragmentViewPager_dest, false)

                        oeuvreDetailViewModel.updateTarget(oeuvre.id, null)

                        Toast.makeText(requireActivity(), "${oeuvre.title} n'est plus ciblé", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun captureOeuvre(oeuvre: Oeuvre) { //TODO fait le crash de l'appareil quand les details sont pas loaded
                dispatchTakePictureIntent(oeuvre)
            }

            override fun openMap(oeuvre: Oeuvre) {
                val action = OeuvreDetailFragmentDirections.openOeuvreMap(oeuvre)
                findNavController().navigate(action)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Check if there is empty parameters in the oeuvre. Remove the textView if its empty
        Log.d("Save","Photo path: " +  oeuvreDetailViewModel.oeuvre?.photo_path)

        appbar.getLayoutTransition().setAnimateParentHierarchy(false);

        val arrayParameters = arrayOf(
            oeuvreDetailViewModel.oeuvre?.title,
            oeuvreDetailViewModel.getArtists(),
            oeuvreDetailViewModel.getDate(),
            oeuvreDetailViewModel.getDimensionsOrStatus(),
            oeuvreDetailViewModel.getSousUsageOrCategory(),
            oeuvreDetailViewModel.getBoroughTerritorySubcategory(),
            oeuvreDetailViewModel.getMaterialsOrAdresses(),
            oeuvreDetailViewModel.getTechniques()
        )

        val arrayViews = arrayOf(
            view.findViewById(R.id.oeuvre_name),
            view.findViewById(R.id.oeuvre_artist),
            view.findViewById(R.id.oeuvre_date),
            view.findViewById(R.id.oeuvre_dimensions),
            view.findViewById(R.id.oeuvre_category),
            view.findViewById(R.id.oeuvre_subcategory),
            view.findViewById(R.id.oeuvre_materials),
            view.findViewById<TextView>(R.id.oeuvre_techniques)
        )

        for ((i, param) in arrayParameters.withIndex()) {
            if (param != null && param != "") {
                arrayViews[i].visibility = View.VISIBLE
            }
        }
    }

    private fun dispatchTakePictureIntent(oeuvre: Oeuvre) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        takePictureIntent.resolveActivity(requireActivity().packageManager)

        // Create the File where the photo should go
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            println("================== ERROR WHEN CREATING IMGFILE")
            null  // Error occurred while creating the File
        }

        // Continue only if the File was successfully created
        if (photoFile != null) {
            context?.let {
                val photoURI: Uri = FileProvider.getUriForFile(it, "com.maison.android.fileprovider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)

                onActivityResult(REQUEST_TAKE_PHOTO, Activity.RESULT_OK, takePictureIntent).let {
                    oeuvreViewModel.updatePath(oeuvre.id, currentPhotoPath)
                    Log.d("Save", "Current: $currentPhotoPath")
                    val action = OeuvreDetailFragmentDirections.itemToRating(oeuvre,currentPhotoPath)
                    findNavController().navigate(action)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File(storageDir, "JPEG_${timeStamp}_.jpg").apply {
            currentPhotoPath = absolutePath
        }
    }

    interface Callback {
        fun updateTarget(oeuvre:Oeuvre)
        fun openMap(oeuvre:Oeuvre)
        fun captureOeuvre(oeuvre: Oeuvre)

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}
