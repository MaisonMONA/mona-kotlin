package com.maison.mona.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SectionIndexer
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.maison.mona.R
import com.maison.mona.databinding.RecyclerviewHeaderBinding
import com.maison.mona.databinding.RecyclerviewOeuvreBinding
import com.maison.mona.entity.Oeuvre
import com.maison.mona.fragment.HomeViewPagerFragmentDirections
import kotlinx.android.synthetic.main.recyclerview_oeuvre.view.*
import org.osmdroid.util.GeoPoint
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class ListAdapter internal constructor(
    context: Context?,
    private val navController: NavController
) : RecyclerView.Adapter<ListAdapter.BaseViewHolder<*>>(), SectionIndexer {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList = emptyList<Any>()
    private var mSectionPositions: MutableList<Int?> = mutableListOf()
    private var category:String = "Titres"
    private var currentLocation: GeoPoint = GeoPoint(45.5044372, -73.578502)

    companion object {
        private var TYPE_OEUVRE = 0
        private var TYPE_HEADER = 2
    }

    inner class OeuvreViewHolder(
        private val binding: RecyclerviewOeuvreBinding
    ) : BaseViewHolder<Oeuvre>(binding.root) {

        init {
            binding.setClickListener {
                val oeuvre = binding.oeuvre
                oeuvre?.let {
                    val action = HomeViewPagerFragmentDirections.homeToOeuvre(it)
                    navController.navigate(action)
                }
            }
        }

        override fun bind(item: Oeuvre) {
            binding.apply {
                oeuvre = item
                executePendingBindings()
            }
        }
    }

    inner class HeaderViewHolder(
        private val binding : RecyclerviewHeaderBinding
    ) : BaseViewHolder<String>(binding.root) {

        override fun bind(item: String) {
            val featuredView: TextView = binding.featuredMessage
            featuredView.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_OEUVRE -> {
                val itemBinding = RecyclerviewOeuvreBinding.inflate(inflater, parent, false)
                OeuvreViewHolder(itemBinding)
            }
            TYPE_HEADER ->{
                val itemBinding = RecyclerviewHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(itemBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = itemList[position]

        if(holder is OeuvreViewHolder){
            holder.bind(element as Oeuvre)

            if(holder.itemView.circleImage != null){
                setHolderText(element, holder)

                setImageIcon(element, holder)

                setLocation(element, holder)
            }
        }else if(holder is HeaderViewHolder){
            holder.bind(element as String)
        }else{
            throw java.lang.IllegalArgumentException()
        }
    }

    private fun setImageIcon(element: Oeuvre, holder: BaseViewHolder<*>){
        if(element.state == 1){
            if(element.type == "artwork")
                holder.itemView.circleImage.setImageResource(R.drawable.ic_list_oeuvre_targeted)

            if(element.type == "place")
                holder.itemView.circleImage.setImageResource(R.drawable.ic_list_lieu_targeted)
            if(element.type == "patrimoine")
                holder.itemView.circleImage.setImageResource(R.drawable.ic_list_patrimoine_targeted)

        }else if(element.state == 2 || element.state == 3){
            if(element.type == "artwork")
                holder.itemView.circleImage.setImageResource(R.drawable.ic_list_oeuvre_collected)

            if(element.type == "place")
                holder.itemView.circleImage.setImageResource(R.drawable.ic_list_lieu_collected)
            if(element.type == "patrimoine")
                holder.itemView.circleImage.setImageResource(R.drawable.ic_list_patrimoine_collected)

        }else{
            if(element.type == "artwork"){
                holder.itemView.circleImage.setImageResource(R.drawable.ic_list_oeuvre)
            }else if(element.type == "place"){
                holder.itemView.circleImage.setImageResource(R.drawable.ic_list_lieu)
            }else if(element.type == "patrimoine") {
                holder.itemView.circleImage.setImageResource(R.drawable.ic_list_patrimoine)
            }
        }
    }

    private fun setLocation(element: Oeuvre, holder: BaseViewHolder<*>){
        val format = DecimalFormat("###.##")
        var text = ""

        text += if(element.distance!! < 1){
            format.format((element.distance!! * 1000).roundToInt()).toString() + "\nm"
        }else{
            format.format(element.distance).toString() + "\nkm"
        }
        holder.itemView.distance.text = text
    }

    private fun setHolderText(element: Oeuvre, holder: BaseViewHolder<*>){
        val titleText  = holder.itemView.titleView
        val detailText = holder.itemView.boroughView

        when(this.category){
            "Titres"->{
                titleText.text = element.title
                detailText.text = element.borough
            }
            "Artistes"->{
                titleText.text   = element.title
                detailText.text = getArtistsList(element)
            }
            else ->{
                titleText.text = element.title
                detailText.text = element.borough
            }
        }
    }

    internal fun submitList(items: List<Any>,category: String,location: GeoPoint) {
        this.category = category
        this.itemList = items
        this.currentLocation = location
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is Oeuvre -> TYPE_OEUVRE
            is String -> TYPE_HEADER
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    override fun getItemCount() = itemList.size

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder (itemView) {
        abstract fun bind(item: T)
    }

    //These functions are for the fast scroller
    override fun getSectionForPosition(position: Int): Int {
        return 0
    }

    override fun getSections(): Array<out Any> {
        val sections: MutableList<String> = ArrayList(26)
        mSectionPositions = ArrayList(26)
        var i = 0
        val size: Int = this.itemList.size

        while (i < size) {
            if(itemList[i] is String) {
                val section: String =  java.lang.String.valueOf((itemList[i] as String).first()).toUpperCase(Locale.ROOT)

                if (!sections.contains(section)) {
                    sections.add(section)
                    mSectionPositions.add(i)
                }
            }

            i++
        }
        return sections.toTypedArray()
    }

    override fun getPositionForSection(sectionIndex: Int): Int {
        return mSectionPositions[sectionIndex]!!
    }

    private fun getArtistsList(item:Oeuvre):String{
        var artistsList = ""
        var counter = 1

        if(!item.artists.isNullOrEmpty()) {
            for (artist in item.artists!!) {
                if(artist.name.isNotBlank()) artistsList += artist.name
                if(counter != item.artists!!.size) artistsList += ", "

                counter++
            }
        }
        return artistsList
    }
}