package com.example.mona.fragment
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.mona.R
import com.example.mona.adapters.ExpandableListAdapter
import com.example.mona.adapters.ListAdapter
import com.example.mona.databinding.FragmentExpListOeuvreBinding
import com.example.mona.entity.Interval
import com.example.mona.entity.Oeuvre
import com.example.mona.viewmodels.OeuvreViewModel
import java.util.*
import kotlin.random.Random
import androidx.lifecycle.Observer

class ExpListFragment : Fragment() {
    private lateinit var listAdapter: ExpandableListAdapter
    private lateinit var expListView: ExpandableListView
    private lateinit var listDataHeader: MutableList<String>
    private lateinit var listDataChild: HashMap<String,List<Oeuvre>>


    private val oeuvreViewModel: OeuvreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentExpListOeuvreBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val expList = binding.lvExp

        listAdapter = ExpandableListAdapter(
            context,
            findNavController()
        )
        prepareListData()

        expList.setAdapter(listAdapter)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }

    private fun prepareListData() {
        listDataHeader = mutableListOf<String>()
        listDataChild = hashMapOf<String,List<Oeuvre>>()
        // Adding child data
        (listDataHeader as ArrayList<String>).add("En vedette cette semaine")
        (listDataHeader as ArrayList<String>).add("Oeuvre Alphabetique")
        (listDataHeader as ArrayList<String>).add("Oeuvre Par Quartier")
        (listDataHeader as ArrayList<String>).add("Lieu Alphabetique")
        (listDataHeader as ArrayList<String>).add("Lieu Par Quartier")

        listAdapter.submitHeaders(listDataHeader)
        // Adding child data
        //Fill the first list item of the weeks at 0
        itemsOfTheWeek()
        //Oeuvre Alphabetic
        oeuvreViewModel.oeuvreTList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
            val sortedList: List<Oeuvre> = oeuvrelist.sortedWith(compareBy(Oeuvre::title, Oeuvre::borough))
            sortedList.let { listAdapter.submitList(it,1) }
        })
        //Oeuvre by borought
        oeuvreViewModel.oeuvreTList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
            val sortedList: List<Oeuvre> = oeuvrelist.sortedWith(compareBy(Oeuvre::borough, Oeuvre::title))
            sortedList.let { listAdapter.submitList(it,2) }
        })
        //Places Alphabetic
        oeuvreViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieulist ->
            val sortedList =
                lieulist.sortedWith(compareBy(Oeuvre::title, Oeuvre::borough))
            sortedList.let { listAdapter.submitList(it,3) }
        })
        //Places by borought
        oeuvreViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieulist ->
            val sortedList =
                lieulist.sortedWith(compareBy(Oeuvre::borough, Oeuvre::title))
            sortedList.let { listAdapter.submitList(it,4) }
        })
        //listDataChild[(listDataHeader as ArrayList<String>).get(0)] = this.oeuvreFeatured // Header, Child data
        //listDataChild[(listDataHeader as ArrayList<String>).get(1)] = this.oeuvreId
        //listDataChild[(listDataHeader as ArrayList<String>).get(2)] = this.oeuvreAlphabetique
    }

    fun itemsOfTheWeek(){

        val weeklyIndex = Calendar.WEEK_OF_YEAR

        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvreList ->
            var featured_oeuvre = emptyList<Oeuvre>()

            for (num in 1..20) {
                val index = weeklyIndex + 7 * num
                featured_oeuvre = featured_oeuvre + oeuvreList.get(index)
            }

            featured_oeuvre.let{listAdapter.submitList(it,0)}
        })

    }
}