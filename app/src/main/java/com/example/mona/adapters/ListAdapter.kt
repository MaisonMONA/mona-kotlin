package com.example.mona.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SectionIndexer
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mona.R
import com.example.mona.databinding.RecyclerviewHeaderBinding
import com.example.mona.databinding.RecyclerviewOeuvreBinding
import com.example.mona.entity.Oeuvre
import com.example.mona.fragment.HomeViewPagerFragmentDirections
import kotlinx.android.synthetic.main.recyclerview_oeuvre.view.*
import java.text.DecimalFormat
import java.text.Normalizer
import java.util.*
import kotlin.collections.ArrayList

class ListAdapter internal constructor(
    context: Context?,
    navController: NavController
) : RecyclerView.Adapter<ListAdapter.BaseViewHolder<*>>(), SectionIndexer {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var masterList = mutableMapOf<String,List<Any>>()
    private var itemList = emptyList<Any>()
    private val navController = navController
    //private var rootList = emptyList<String>();
    var mSectionPositions: MutableList<Int?> = mutableListOf()
    private var category:String = "Titres";
    private var currentLocation: Location = Location("")

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
                //Set the texts
                var titleText  = holder.itemView.titleView;
                var detailText = holder.itemView.boroughView;
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
                //select the color for the articles
                if(element.type == "artwork"){
                    holder.itemView.circleImage.backgroundTintList = ColorStateList.valueOf( holder.itemView.context.resources.getColor(R.color.artwork))
                }else if(element.type == "place"){
                    holder.itemView.circleImage.backgroundTintList = ColorStateList.valueOf( holder.itemView.context.resources.getColor(R.color.lieu))
                }
                //Set the image icon
                if(element.state == 1){
                    holder.itemView.circleImage.setImageResource(R.drawable.targeted)
                }else if(element.state == 2){
                    holder.itemView.circleImage.setImageResource(R.drawable.ic_collected)
                }
                //Set the location if we have the permission to do so
                if(this.currentLocation.provider != ""){
                    val distance = distance(
                        this.currentLocation.latitude,
                        this.currentLocation.longitude,
                        element.location!!.lat,
                        element.location!!.lng
                    )
                    var format = DecimalFormat("###.##")
                    var text = ""
                    if(distance < 1){
                        text +=  format.format(Math.round(distance * 1000)).toString() + "\nm"
                    }else{
                        text +=  format.format(distance).toString() + "\nkm"
                    }
                    holder.itemView.distance.text = text
                }

            }
        }else if(holder is HeaderViewHolder){
            holder.bind(element as String)
        }else{
            throw java.lang.IllegalArgumentException()
        }
    }

    internal fun submitList(items: List<Any>,category: String,location: Location) {
        this.category = category;
        this.itemList = items
        this.currentLocation = location
        notifyDataSetChanged()
    }
    /*
    internal fun submitMasterList(items: MutableMap<String,List<Any>>) {
        this.masterList = items
        notifyDataSetChanged()
    }

    internal fun submitRootList(items: List<String>) {
        this.rootList = items
    }

    internal fun submitSubList(child :String, items: List<Any>) {
        this.masterList[child] = items
    }
    */
    override fun getItemViewType(position: Int): Int {
        val comparable = itemList[position]
        return when (comparable) {
            is Oeuvre -> TYPE_OEUVRE
            is String -> TYPE_HEADER
            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }

    override fun getItemCount() = itemList.size


    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder (itemView) {
        abstract fun bind(item: T)
    }

    fun getEmojiByUnicode(unicode: Int): String? {
        return String(Character.toChars(unicode))
    }

    //These functions are for the fast scroller
    override fun getSectionForPosition(position: Int): Int {
        return 0
    }

    override fun getSections(): Array<out Any>? {
        val sections: MutableList<String> = ArrayList(26)
        mSectionPositions = ArrayList(26)
        var i = 0
        val size: Int = this.itemList.size
        while (i < size) {
            if(itemList.get(i) is String) {
                val section: String =  java.lang.String.valueOf((itemList.get(i) as String).first()).toUpperCase(Locale.ROOT)
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
        return mSectionPositions.get(sectionIndex)!!
    }

    fun getArtistsList(item:Oeuvre):String{
        var artistsList = ""
        var counter = 1;
        if(!item.artists.isNullOrEmpty()) {
            for (artist in item.artists!!) {
                if(!artist.name.isBlank()) artistsList += artist.name
                if(counter != item.artists!!.size) artistsList += ", "
                counter++
            }
        }
        return artistsList
    }


    fun distance(
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double
    ): Double {
        var p = 0.017453292519943295;    // Math.PI / 180
        var a = 0.5 - Math.cos((toLat - fromLat) * p)/2 +
                Math.cos(fromLat * p) * Math.cos(toLat * p) *
                (1 - Math.cos((toLon - fromLon) * p))/2;

        return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km
    }
}