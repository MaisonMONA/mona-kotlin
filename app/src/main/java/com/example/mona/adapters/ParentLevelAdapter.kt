package com.example.mona.adapters

import CustomExpListView
import SecondLevelAdapter
import com.example.mona.R
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.navigation.NavController
import com.example.mona.databinding.RecyclerviewOeuvreBinding
import com.example.mona.entity.Oeuvre

import java.util.*


class ParentLevelAdapter(

    private val mContext: Context,
    private val mListDataHeader: List<String>,
    private var mListDataSubHeaders: List<List<String>>,
    private var mListData_SecondLevel_Map: MutableMap<String,List<String>>,
    private var mListData_ThirdLevel_Map: MutableMap<String, List<Oeuvre>>

) : BaseExpandableListAdapter() {

    override fun getChild(groupPosition: Int, childPosition: Int): String {
        return mListDataSubHeaders[groupPosition][childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup?
    ): View {
        val parentNode = getGroup(groupPosition)
        val sub:String = mListDataSubHeaders[groupPosition][childPosition]
        val numberChildren = mListData_ThirdLevel_Map[sub]?.size ?: 0
        val size = convertView?.minimumHeight ?: 0
        val sizeChildren = 50 * numberChildren
        val secondLevelExpListView = CustomExpListView(mContext,sizeChildren)

        Log.d("Liste", parentNode)
        secondLevelExpListView.setAdapter(
            mListData_SecondLevel_Map.get(parentNode)?.let {
                SecondLevelAdapter(
                    mContext,
                    it,
                    mListData_ThirdLevel_Map
                )
            })
        secondLevelExpListView.setGroupIndicator(null)
        return secondLevelExpListView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        /*
        if(mListDataHeader.isEmpty() || mListDataSubHeaders[groupPosition].isNullOrEmpty()){
            return 0
        }else{
            Log.d("Liste","Parent Size Children Count: " + mListDataSubHeaders[groupPosition].size.toString())
            return mListDataSubHeaders[groupPosition].size
        }*/
        return 1;
    }

    override fun getGroup(groupPosition: Int): String {
        return mListDataHeader[groupPosition]
    }

    override fun getGroupCount(): Int {
        return mListDataHeader.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup?
    ): View? {
        Log.d("Liste","Get group parent: " + groupPosition.toString())
        var convertView = convertView
        val headerTitle = getGroup(groupPosition)
        val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        convertView = layoutInflater.inflate(R.layout.top_header_list_oeuvre, null)
        val lblListHeader = convertView?.findViewById(R.id.topListHeader) as TextView
        //lblListHeader.setTypeface(null, Typeface.BOLD)
        lblListHeader.setTextColor(Color.CYAN)
        lblListHeader.text = headerTitle
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    internal fun submitList(items: List<Oeuvre>, sub: String) {
        this.mListData_ThirdLevel_Map[sub] = items
        Log.d("Liste","Items sub: " + sub)
        Log.d("Liste","Item Count: " + (this.mListData_ThirdLevel_Map[sub]?.size ?: 0))
        notifyDataSetChanged()
    }

}