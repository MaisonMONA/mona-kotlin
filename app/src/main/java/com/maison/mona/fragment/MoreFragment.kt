package com.maison.mona.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maison.mona.R
import com.maison.mona.activities.LoginActivity
import com.maison.mona.activities.MyGlobals
import com.maison.mona.activities.OnboardingActivity
import com.maison.mona.data.OeuvreDatabase
import com.maison.mona.data.OeuvreRepository
import com.maison.mona.data.SaveSharedPreference
import com.maison.mona.databinding.FragmentMoreBinding
import com.maison.mona.entity.Oeuvre
import com.maison.mona.task.SaveOeuvre
import com.maison.mona.viewmodels.OeuvreViewModel
import kotlinx.android.synthetic.main.fragment_more.*
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoreFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMoreBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding?.apply {
            var online = MyGlobals(requireContext())
            //Affecting the username
            username.text = SaveSharedPreference.getUsername(context)
            setOnlineMessage(offlineButton, SaveSharedPreference.isOnline(requireContext()))
            badgeButton.setOnClickListener {
                val action = HomeViewPagerFragmentDirections.homeToBadge()
                findNavController().navigate(action)

            }
            howItWorksButton.setOnClickListener{
                //val action = HomeViewPagerFragmentDirections.homeToText("CommentCaMarche.md")
                //findNavController().navigate(action)
                val intent = Intent(context, OnboardingActivity::class.java)
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

            offlineButton.setOnClickListener {
                online.setOnlineMode()
                setOnlineMessage(offlineButton,SaveSharedPreference.isOnline(requireContext()))
                if(SaveSharedPreference.isOnline(requireContext())){
                    updateInfoOnline()
                }

            }

            signOutButton.setOnClickListener {
                val myIntent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(myIntent)
            }
        }

        return binding.root
    }

    fun setOnlineMessage(button: TextView, status: Boolean){
        var offlineMessage = R.string.go_offline
        if(!status) offlineMessage = R.string.go_online
        button.setText(offlineMessage)
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