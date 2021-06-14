package com.maison.mona.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maison.mona.R
import com.maison.mona.activities.LoginActivity
import com.maison.mona.activities.MyGlobals
import com.maison.mona.data.*
import com.maison.mona.databinding.FragmentMoreBinding
import com.maison.mona.entity.Oeuvre
import com.maison.mona.task.SaveOeuvre
import com.maison.mona.viewmodels.BadgeViewModel
import com.maison.mona.viewmodels.OeuvreViewModel
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoreFragment : Fragment(){

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mSwitch: Switch

    private val oeuvreViewModel: OeuvreViewModel by viewModels()
    private val badgeViewModel: BadgeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMoreBinding.inflate(inflater, container, false)
        context ?: return binding.root

        mSwitch = binding.moreOnlineSwitch

        if(SaveSharedPreference.isOnline(requireContext()))
            mSwitch.isChecked = false


        mSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener(){ compoundButton: CompoundButton, b: Boolean ->
            if(mSwitch.isChecked){
                MyGlobals(requireContext()).setOnlineMode()

                if(SaveSharedPreference.isOnline(requireContext()))
                    updateInfoOnline()
            } else{
                MyGlobals(requireContext()).setOnlineMode()

                if(SaveSharedPreference.isOnline(requireContext()))
                    updateInfoOnline()
            }
        })

        binding?.apply {
            //Affecting the username
            username.text = SaveSharedPreference.getUsername(context)

            oeuvreViewModel.collectedList.observe(viewLifecycleOwner, Observer {collected ->
                val count = collected.filter { it.state == 2 || it.state == 3 }.size

                if(count == 0){
                    moreUserArtworks.text = "Aucune œuvre collectionnée"
                } else if (count == 1){
                    moreUserArtworks.text = "1 œuvre collectionnée"
                } else {
                    val artworksString = getString(R.string.more_user_artworks_count, count)
                    moreUserArtworks.setText(artworksString)
                }
            })

            badgeViewModel.badgesList.observe(viewLifecycleOwner, Observer {collected ->
                val count = collected.filter { it.isCollected }.size

                if(count == 0){
                    moreUserBadges.text = "Aucun badge débloqué"
                } else if (count == 1){
                    moreUserBadges.text = "1 badge débloqué"
                } else {
                    val badgesString = getString(R.string.more_user_badges_count, count)
                    moreUserBadges.setText(badgesString)
                }
            })

            howItWorksButton.setOnClickListener{
                val action = HomeViewPagerFragmentDirections.homeToText("CommentCaMarche.md")
                findNavController().navigate(action)
            }

            aboutButton.setOnClickListener {
                val action = HomeViewPagerFragmentDirections.homeToText("AproposMONA.md")
                findNavController().navigate(action)
            }

            whoAreWeButton.setOnClickListener {
                val action = HomeViewPagerFragmentDirections.homeToText("QuiSommesNous.md")
                findNavController().navigate(action)
            }

            signOutButton.setOnClickListener {
                val myIntent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(myIntent)
            }
        }

        return binding.root
    }

    fun updateInfoOnline(){
        val oeuvreViewModel: OeuvreViewModel by viewModels()

        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { itemlist ->
            val sortedList = itemlist.filter{it.state == 3}
            update(sortedList);
        })
    }

    fun update(list: List<Oeuvre>){
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
                Log.d("Save", "reponse: " + response)
                val reader = JSONObject(response)
                
                if (reader.has("errors")) {
                    Log.d("Save", "Erreur Save reader");
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