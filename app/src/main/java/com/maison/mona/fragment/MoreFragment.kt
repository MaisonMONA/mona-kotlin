package com.maison.mona.fragment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maison.mona.R
import com.maison.mona.activities.BadgeActivity
import com.maison.mona.activities.LoginActivity
import com.maison.mona.activities.OnBoardingActivity
import com.maison.mona.activities.Tutoriel
import com.maison.mona.data.OeuvreDatabase
import com.maison.mona.data.OeuvreRepository
import com.maison.mona.data.SaveSharedPreference
import com.maison.mona.databinding.FragmentMoreBinding
import com.maison.mona.entity.Oeuvre
import com.maison.mona.task.SaveOeuvre
import com.maison.mona.viewmodels.BadgeViewModel
import com.maison.mona.viewmodels.OeuvreViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class MoreFragment : Fragment(){

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mSwitch: Switch

    private val oeuvreViewModel: OeuvreViewModel by viewModels()
    private val badgeViewModel: BadgeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMoreBinding.inflate(inflater, container, false)
        context ?: return binding.root

        //mSwitch = binding.moreOnlineSwitch

        /*if(SaveSharedPreference.isOnline(requireContext()))
            mSwitch.isChecked = false


        mSwitch.setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
            updateInfoOnline()
        }*/

        binding.apply {
            //Affecting the username
            username.text = SaveSharedPreference.getUsername(context)

           /* oeuvreViewModel.collectedList.observe(viewLifecycleOwner, { collected ->

                when (val count = collected.filter { it.state == 2 || it.state == 3 }.size) {
                    0 -> {
                        moreUserArtworks.text = "Aucune œuvre collectionnée"
                    }
                    1 -> {
                        moreUserArtworks.text = "1 œuvre collectionnée"
                    }
                    else -> {
                        val artworksString = getString(R.string.more_user_artworks_count, count)
                        moreUserArtworks.text = artworksString
                    }
                }
            })

            badgeViewModel.badgesList.observe(viewLifecycleOwner, { collected ->

                when (val count = collected.filter { it.isCollected }.size) {
                    0 -> {
                        moreUserBadges.text = "Aucun badge débloqué"
                    }
                    1 -> {
                        moreUserBadges.text = "1 badge débloqué"
                    }
                    else -> {
                        val badgesString = getString(R.string.more_user_badges_count, count)
                        moreUserBadges.text = badgesString
                    }
                }
            })*/

            /*howItWorksButton.setOnClickListener{
                val action = HomeViewPagerFragmentDirections.homeToText("CommentCaMarche.md")
                findNavController().navigate(action)
            }*/

            howItWorksButton.setOnClickListener{
                val intent = Intent(context, Tutoriel::class.java)
                startActivity(intent)
            }

            aboutButton.setOnClickListener {
                val action = HomeViewPagerFragmentDirections.homeToText("AproposMONA.md")
                findNavController().navigate(action)
            }

            whoAreWeButton.setOnClickListener {
                val action = HomeViewPagerFragmentDirections.homeToText("QuiSommesNous.md")
                findNavController().navigate(action)
            }

            reglages.setOnClickListener {

                val packageName = "com.maison.mona"
                try {
                    //Open the specific App Info page:
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    //e.printStackTrace();

                    //Open the generic Apps page:
                    val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                    startActivity(intent)
                }
            }
        }

        return binding.root
    }

    private fun updateInfoOnline(){
        val oeuvreViewModel: OeuvreViewModel by viewModels()

        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, { itemlist ->
            val sortedList = itemlist.filter{it.state == 3}
            update(sortedList)
        })
    }

    private fun update(list: List<Oeuvre>){
        val repository: OeuvreRepository
        val oeuvreDao = OeuvreDatabase.getDatabase(
            requireContext(),
            lifecycleScope
        ).oeuvreDAO()
        repository = OeuvreRepository.getInstance(oeuvreDao)

        for(item in list){
            val sendOeuvre = SaveOeuvre(requireContext())
            sendOeuvre.execute(
                item.idServer.toString(),
                item.rating!!.toInt().toString(),
                item.comment,
                item.photo_path,
                item.type
            )
            val response = sendOeuvre.get()

            if (response != "" && response != null) {
                Log.d("Save", "reponse: $response")
                val reader = JSONObject(response)
                
                if (reader.has("errors")) {
                    Log.d("Save", "Erreur Save reader")
                    val errors = reader.getJSONObject("errors")
                    Log.d("Save", errors.toString())
                }else{
                    lifecycleScope.launch(Dispatchers.IO) {
                        repository.updateTarget(item.id,2)
                    }
                }
            }
        }
    }
}