package com.example.mona.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mona.OeuvreViewModel
import com.example.mona.R
import com.example.mona.entity.Oeuvre
import kotlinx.android.synthetic.main.fragment_item.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ItemFragment () : Fragment() {

    val safeArgs : ItemFragmentArgs by navArgs()
    private val REQUEST_TAKE_PHOTO = 1
    private lateinit var currentPhotoPath: String

    private val oeuvreViewModel: OeuvreViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_item, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val oeuvre  = safeArgs.itemSelected

        //We setup the artowk item in accordance
        val title_and_year = oeuvre?.title + ", " + oeuvre?.produced_at?.substring(0,4)

        view.itemTitleYear.text = title_and_year

        var artistString : String? = ""

        for(artistIndex in 0 until oeuvre?.artists!!.size){
            artistString += oeuvre.artists!!.get(artistIndex).name
        }
        view.itemArtist.text = artistString
/*
        var dimensionString : String? = ""
        for (dimensionIndex in 0 until oeuvre.dimension!!.size){
            dimensionString += oeuvre.dimension!![dimensionIndex]
            dimensionString += " "
        }

        view.itemDimensions.text = dimensionString
*/
        view.itemCategory.text = oeuvre.category?.fr
        view.itemSubcategory.text = oeuvre.subcategory?.fr

        //the artwork state is collected
        //display displayed artwork
        if(oeuvre.state == 2){
            val button_layout = view.findViewById<LinearLayout>(R.id.itemButtons)
            button_layout.layoutParams.height = 0

            val item_comment = view.findViewById<TextView>(R.id.itemComment)
            item_comment.text = oeuvre.comment
            item_comment.visibility = View.VISIBLE

            val item_rating = view.findViewById<RatingBar>(R.id.itemRating)
            item_rating.rating = oeuvre.rating!!.toFloat()
            item_rating.visibility = View.VISIBLE

            val itemDate = view.findViewById<TextView>(R.id.itemDate)
            itemDate.text = "Prise le "+oeuvre.date_photo


            var item_picture = view.findViewById<ImageView>(R.id.itemImage)
            var picture = Drawable.createFromPath(oeuvre.photo_path)
            item_picture.setImageDrawable(picture)
            item_picture.rotation = 90.0f

            //Reproportion the layout weight
            //val upperHalfView = view.findViewById<LinearLayout>(R.id.upper_half)
            //(upperHalfView.layoutParams as LinearLayout.LayoutParams).weight = 0.7f


        }else{

            //Button map
            view.findViewById<ImageButton>(R.id.button_map)?.setOnClickListener {
                val action = ItemFragmentDirections.openItemMap(oeuvre)
                findNavController().navigate(action)
            }

            view.findViewById<ImageButton>(R.id.button_cam)?.setOnClickListener {
                dispatchTakePictureIntent(oeuvre)
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

                            val action = ItemFragmentDirections.itemToRating(oeuvre)
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



}
