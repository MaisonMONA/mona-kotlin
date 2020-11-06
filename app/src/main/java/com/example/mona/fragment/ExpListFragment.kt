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
import com.example.mona.adapters.ParentLevelAdapter
import kotlin.collections.HashMap

class ExpListFragment : Fragment() {
    private lateinit var listAdapter: ExpandableListAdapter
    //private lateinit var listAdapter: ParentLevelAdapter
    private lateinit var expListView: ExpandableListView
    private var listDataHeader: MutableList<String> = mutableListOf()
    private var listDataSubHeader: MutableList<String> = mutableListOf()
    //private lateinit var listDataHeader: MutableMap<String,List<String>>
    private lateinit var listDataChild: HashMap<String,List<Oeuvre>>

    private lateinit var mListData_SecondLevel_Map: MutableMap<String, List<String>>
    private lateinit var mListData_ThirdLevel_Map: MutableMap<String, List<Oeuvre>>

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

        //TODO implement multi leveled expandable lists
        /*listDataHeader = listOf("1","2") as MutableList<String>
        var listDataSubHeaders = listOf(listOf("1.1","1.2","1.3"),listOf("2.1","2.2","2.3"))
        var listDataOeuvre = mutableMapOf(
            "1.1" to listOf("test1","test2"),
            "1.2" to listOf("test3","test4"),
            "1.3" to listOf("test3","test4"),
            "2.1" to listOf("test5","test6"),
            "2.2" to listOf("test7","test8"),
            "2.3" to listOf("test9","test10")
        )
        listAdapter = ParentLevelAdapter(
            requireContext(),
            listDataHeader,
            listDataSubHeaders,
            listDataOeuvre
        )
         */
        //Have to figure out a few quirks
        /*
        listDataHeader = mutableListOf()
        listDataSubHeader = mutableListOf()
        // Adding the headers
        (listDataHeader as ArrayList<String>).add("1- Oeuvres")
        (listDataHeader as ArrayList<String>).add("2- Lieux")

        //Adding the sub headers
        var listDataSubHeaders = listOf(
            listOf("1.1- Alphabétique","1.2- Quartier"),
            listOf("2.1- Alphabétique","2.2- Quartier"))

        mListData_SecondLevel_Map = mutableMapOf()
        for(i in listDataHeader.indices){
            val key: String = listDataHeader[i]
            val content: List<String> = listDataSubHeaders[i]
            mListData_SecondLevel_Map[key] = content
        }

        mListData_ThirdLevel_Map = mutableMapOf()
        for(i in listDataSubHeaders.indices){
            for(j in listDataSubHeaders[i].indices){
                val key: String = listDataSubHeaders[i][j]
                val content: List<Oeuvre> = listOf()
                mListData_ThirdLevel_Map[key] = content
            }
        }
        Log.d("Liste","Liste Data Header: " + listDataHeader.toString())
        Log.d("Liste","Liste Sub Header: " + listDataSubHeaders.toString())
        Log.d("Liste","Liste SecondLevel: " + mListData_SecondLevel_Map.toString())
        listAdapter = ParentLevelAdapter(
            requireContext(),
            listDataHeader,
            listDataSubHeaders,
            mListData_SecondLevel_Map,
            mListData_ThirdLevel_Map
            )

        prepareListData(listDataHeader,listDataSubHeaders)
        */
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
/*
    private fun prepareListData(listDataHeaders : List<String>, listDataSubHeaders: List<List<String>> ) {
        listDataChild = hashMapOf()

        // Adding child data
        //Fill the first list item of the weeks at 0
        //itemsOfTheWeek()
        //Oeuvre Alphabetic
        oeuvreViewModel.oeuvreTList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
            val sortedList: List<Oeuvre> = oeuvrelist.sortedWith(compareBy(Oeuvre::title, Oeuvre::borough))
            sortedList.let { listAdapter.submitList(it,"1.1- Alphabétique") }
        })
        //Oeuvre by borought
        oeuvreViewModel.oeuvreTList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
            val sortedList: List<Oeuvre> = oeuvrelist.sortedWith(compareBy(Oeuvre::borough, Oeuvre::title))
            sortedList.let { listAdapter.submitList(it,"1.2- Quartier") }
        })
        //Places Alphabetic
        oeuvreViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieulist ->
            val sortedList =
                lieulist.sortedWith(compareBy(Oeuvre::title, Oeuvre::borough))
            sortedList.let { listAdapter.submitList(it,"2.1- Alphabétique") }
        })
        //Places by borought
        oeuvreViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieulist ->
            val sortedList =
                lieulist.sortedWith(compareBy(Oeuvre::borough, Oeuvre::title))
            sortedList.let { listAdapter.submitList(it,"2.2- Quartier") }
        })
        /*
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
         */
    }
    */
    fun prepareListData() {
        listDataHeader = mutableListOf<String>()
        listDataChild = hashMapOf<String,List<Oeuvre>>()
        // Adding child data
        (listDataHeader as ArrayList<String>).add("En vedette cette semaine")
        (listDataHeader as ArrayList<String>).add("Oeuvre Alphabetique")
        (listDataHeader as ArrayList<String>).add("Oeuvre Par Quartier")
        (listDataHeader as ArrayList<String>).add("Lieu Alphabetique")
        (listDataHeader as ArrayList<String>).add("Lieu Par Quartier")
        (listDataHeader as ArrayList<String>).add("Collectés")

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
        oeuvreViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieulist ->
            val sortedList =
                lieulist.sortedWith(compareBy(Oeuvre::borough, Oeuvre::title))
            sortedList.let { listAdapter.submitList(it,4) }
        })
        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { itemlist ->
            val sortedList =
                itemlist.filter{it.state == 2}
            sortedList.let { listAdapter.submitList(it,5) }
        })
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