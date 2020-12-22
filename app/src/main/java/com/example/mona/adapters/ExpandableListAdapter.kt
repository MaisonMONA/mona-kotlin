package com.example.mona.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mona.R
import com.example.mona.databinding.ActivityLoginBinding.bind
import com.example.mona.databinding.RecyclerviewOeuvreBinding
import com.example.mona.entity.Oeuvre
import com.example.mona.fragment.HomeViewPagerFragmentDirections
import com.example.mona.viewmodels.OeuvreViewModel
import java.util.*
import com.example.mona.databinding.RecyclerviewHeaderBinding
import kotlinx.android.synthetic.main.recyclerview_oeuvre.view.*
import kotlin.math.sign


class ExpandableListAdapter(
    private val _context: Context?, // header titles
    navController: NavController

) : BaseExpandableListAdapter() {
    private val navController = navController
    private val inflater: LayoutInflater = LayoutInflater.from(_context)
    private var _listDataHeader: List<String> = listOf<String>()
    // child data in format of header title, child title
    private var _listDataChild: HashMap<String,List<Oeuvre>> = hashMapOf<String,List<Oeuvre>>()


    override fun getChild(groupPosition: Int, childPosititon: Int): Oeuvre {
        return _listDataChild[_listDataHeader.get(groupPosition)]!![childPosititon]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View? {
        var convertView = convertView
        val element = getChild(groupPosition,childPosition)
        //if(convertView == null){
            val itemBinding = RecyclerviewOeuvreBinding.inflate(inflater, parent, false)
        itemBinding.oeuvre = element
            itemBinding.setClickListener {
                Log.d("Liste", "$groupPosition $childPosition")
                Log.d("Liste",element.toString())
                val oeuvre = itemBinding.oeuvre
                oeuvre?.let {
                    val action = HomeViewPagerFragmentDirections.homeToOeuvre(it)
                    navController.navigate(action)
                }
            }
            convertView = itemBinding.root
        //}

        convertView.titleView.text = element.title
        if(convertView.circleImage != null) {
            //select the color for the articles
            if (element.type == "artwork") {
                convertView.circleImage.backgroundTintList =
                    ColorStateList.valueOf(convertView.context.resources.getColor(R.color.artwork))
            } else if (element.type == "place") {
                convertView.circleImage.backgroundTintList =
                    ColorStateList.valueOf(convertView.context.resources.getColor(R.color.lieu))
            }
            if(element.state == 1){
                convertView.circleImage.setImageResource(R.drawable.ic_target_black)
            }else if(element.state == 2 || element.state == 3){
                convertView.circleImage.setImageResource(R.drawable.ic_collected)
            }
        }
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        if(_listDataHeader.isEmpty() || _listDataChild[_listDataHeader[groupPosition]].isNullOrEmpty()){
            return 0
        }else{
            return _listDataChild[_listDataHeader[groupPosition]]!!.size
        }
    }

    override fun getGroup(groupPosition: Int): Any {
        return _listDataHeader[groupPosition]
    }

    override fun getGroupCount(): Int {
        return _listDataHeader.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView
        val headerTitle = getGroup(groupPosition) as String
        if (convertView == null) {
            val infalInflater = _context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.header_list_oeuvre, null)
        }
        //select the color for the Headers
        var color: Int = R.color.artwork
        when(groupPosition){
            0   -> {
                color = R.color.red
            }
            1,2 -> {
                color = R.color.artwork
            }
            3,4 -> {
                color = R.color.lieu
            }
            5 -> {
                color = R.color.grey
            }
        }
        //set image
        var image: Int = R.drawable.circle
        when(groupPosition){
            0   -> image = R.drawable.ic_featured
            1,3 -> image = R.drawable.ic_alphabetical
            2,4 -> image = R.drawable.ic_borough
            5   -> image = R.drawable.ic_collected
        }
        if (convertView != null) {
            convertView.backgroundTintList =
                ColorStateList.valueOf(convertView.context.resources.getColor(color))
            convertView.findViewById<ImageView>(R.id.iconHeader).setImageResource(image)
        }
        val lblListHeader = convertView!!.findViewById<View>(R.id.lblListHeader) as TextView
        lblListHeader.setTypeface(null, Typeface.BOLD)
        lblListHeader.text = headerTitle
        val numberContent = convertView.findViewById<View>(R.id.numberContent) as TextView
        val numberChildren = getChildrenCount(groupPosition).toString()
        numberContent.setText(numberChildren)
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun onGroupExpanded(groupPosition: Int) {
        super.onGroupExpanded(groupPosition)
        Log.d("Liste","Header: " + _listDataHeader[groupPosition] + " " + _listDataChild[_listDataHeader[groupPosition]].toString())
    }

    internal fun submitHeaders(headers: List<String>){
        this._listDataHeader = headers
        notifyDataSetChanged()
    }

    internal fun submitList(items: List<Oeuvre>, type: Int) {
        //Log.d("Liste","Nombre: " + items.size.toString())
        //Initially, sort the list alphabetically
        //https://stackoverflow.com/questions/37259159/sort-collection-by-multiple-fields-in-kotlin
        //val sortedList = oeuvres.sortedWith(compareBy(Oeuvre::title, Oeuvre::borough))
        _listDataChild[_listDataHeader.get(type)] = items
        notifyDataSetChanged()
    }
}