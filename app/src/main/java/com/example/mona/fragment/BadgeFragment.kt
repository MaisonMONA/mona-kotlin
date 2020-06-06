package com.example.mona.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mona.R
import com.example.mona.adapters.BadgeAdapter
import com.example.mona.databinding.FragmentBadgeBinding
import com.example.mona.entity.Badge
import com.example.mona.viewmodels.OeuvreViewModel

class BadgeFragment : Fragment() {

    //view models
    private val oeuvreViewModel: OeuvreViewModel by viewModels()

    private lateinit var adapter: BadgeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBadgeBinding.inflate(inflater, container, false)
        context ?: return binding.root


        val recyclerView = binding.badgeRecyclerview

        adapter = BadgeAdapter(
            context,
            findNavController()
        )

        submitBadges()

        recyclerView.adapter = adapter

        binding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        return binding.root
    }

    fun submitBadges (){

        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvreList ->
            val collectedOeuvres = oeuvreList.filter { oeuvre -> oeuvre.state == 2 }

            val ac_count = collectedOeuvres.filter { oeuvre -> oeuvre.borough == "Ahuntsic-Cartierville"  }.size
            val cdn_count = collectedOeuvres.filter { oeuvre -> oeuvre.borough == "Côte-des-Neiges-Notre-Dame-de-Grâce"  }.size
            val hochelaga_count = collectedOeuvres.filter { oeuvre -> oeuvre.borough == "Mercier-Hochelaga-Maisonneuve"  }.size
            val lachine_count = collectedOeuvres.filter { oeuvre -> oeuvre.borough == "Lachine"  }.size
            val lasalle_count = collectedOeuvres.filter { oeuvre -> oeuvre.borough == "LaSalle"  }.size
            val outremont_count = collectedOeuvres.filter { oeuvre -> oeuvre.borough == "Outremont"  }.size
            val pmr_count = collectedOeuvres.filter { oeuvre -> oeuvre.borough == "Le Plateau-Mont-Royal"  }.size
            val rosemont_count = collectedOeuvres.filter { oeuvre -> oeuvre.borough == "Rosemont-La Petite-Patrie"  }.size
            val so_count = collectedOeuvres.filter { oeuvre -> oeuvre.borough == "Le Sud-Ouest"  }.size
            val verdun_count = collectedOeuvres.filter { oeuvre -> oeuvre.borough == "Verdun"  }.size
            val villeray_count = collectedOeuvres.filter { oeuvre -> oeuvre.borough == "Villeray-Saint-Michel-Parc-Extension"  }.size
            val vm_count = collectedOeuvres.filter { oeuvre -> oeuvre.borough == "Ville-Marie"  }.size

            val badgeList = listOf(

                Badge("Ahuntsic-Cartierville", "Bravo! Votre collection compte "+ac_count+" œuvres d'Ahuntsic-Cartierville. Êtes-vous passé devant le Cégep?", R.drawable.ac_grey, R.drawable.ac_color, ac_count, 5),

                Badge("Côte-des-Neiges-Notre-Dame-de-Grâce", "Bravo, vous avez collectionné "+cdn_count+" œuvres dans Côte-des-Neiges-Notre-Dame-de-Grâce. En avez-vous profité pour visiter l'Oratoire St-Joseph? ", R.drawable.cdn_grey, R.drawable.cdn_color, cdn_count, 10),

                Badge("Mercier-Hochelaga-Maisonneuve", ""+hochelaga_count+" œuvres collectionnées dans Mercier-Hochelaga-Maisonneuve, bravo! Apercevez-vous le Stade Olympique d'où vous êtes présentement?", R.drawable.hochelaga_grey, R.drawable.hochelaga_color, hochelaga_count, 15),

                Badge("Lachine", "Vos balades au long du Canal ont porté fruit : vous avez collectionné "+lachine_count+" œuvres à Lachine!", R.drawable.lachine_grey, R.drawable.lachine_color, lachine_count, 15),

                Badge("LaSalle", "L'exploration de LaSalle se poursuit! Vous y avez collectionné "+lasalle_count+" œuvres. Avez-vous croisé l'Hotêl de ville?", R.drawable.lasalle_grey, R.drawable.lasalle_color, lasalle_count, 5),

                Badge("Outremont", "Bravo", R.drawable.outremont_grey, R.drawable.outremont_color, outremont_count, 10),

                Badge("Le Plateau-Mont-Royal", "Le Plateau et le Mont-Royal n'ont plus de secret pour vous! Vous venez d'y collectionner votre "+pmr_count+"e œuvres", R.drawable.pmr_grey, R.drawable.pmr_color, pmr_count, 20),

                Badge("Rosemont-La Petite-Patrie", "Bravo! Votre collection compte maintenant "+ rosemont_count +" oeuvres de Rosemont-La-Petite-Patrie. Reconnaissez-vous l'édifice du Cinéma Beaubien? ", R.drawable.rosemont_grey, R.drawable.rosemont_color, rosemont_count, 25),

                Badge("Le Sud-Ouest", "Vous avez collectionné "+so_count+" œuvres dans le Sud-Ouest, félicitations! Vous pouvez en profiter pour (re-)découvrir le Marché Atwater.", R.drawable.so_grey, R.drawable.so_color, so_count, 15),

                Badge("Verdun", "Une "+ verdun_count+" œuvres collectionés à Verdun, félicitation! Reconaissez-vous l'auditorium?", R.drawable.verdun_grey, R.drawable.verdun_color, verdun_count, 5),

                Badge("Villeray-Saint-Michel-Parc-Extension", "" + villeray_count + " œuvres collectionnées dans Villeray-Saint-Michel-Parc-Extension, bravo! Combien de ruelles avez-vous visitées pour y arriver?", R.drawable.villeray_grey, R.drawable.villeray_color, villeray_count, 10),

                Badge("Ville-Marie", "Vous venez de relever un défi de taille :"+ vm_count+" œuvres collectionnées dans Ville-Marie! Le centre-ville et le Vieux Port vous paraissent-ils différents maintenant?", R.drawable.vm_grey, R.drawable.vm_color, vm_count, 30)
            )

            adapter.submitList(badgeList)


        })

    }

}