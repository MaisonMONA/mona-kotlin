import android.content.Context
import android.graphics.Color
import android.util.Log
import com.example.mona.R
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.example.mona.entity.Oeuvre


class SecondLevelAdapter(
    mContext: Context,
    mListDataHeader: List<String>,
    mListDataChild: Map<String, List<Oeuvre>>
) :
    BaseExpandableListAdapter() {
    private val mContext: Context
    private val mListDataHeader: List<String>
    private val mListDataChild: Map<String, List<Oeuvre>>

    override fun getChild(groupPosition: Int, childPosition: Int): Oeuvre {
        return mListDataChild[mListDataHeader[groupPosition]]!![childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup?
    ): View? {
        var convertView: View? = convertView
        val childText = getChild(groupPosition, childPosition).title
        val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        convertView = layoutInflater.inflate(R.layout.recyclerview_oeuvre, parent, false)
        val txtListChild = convertView?.findViewById(R.id.titleView) as TextView
        //txtListChild.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        txtListChild.text = childText
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return try {
            mListDataChild[mListDataHeader[groupPosition]]!!.size
        } catch (e: Exception) {
            0
        }
    }


    override fun getGroup(groupPosition: Int): String {
        return this.mListDataHeader[groupPosition];
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
        var convertView: View? = convertView
        val headerTitle = mListDataHeader[groupPosition]
        val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        convertView = layoutInflater.inflate(R.layout.header_list_oeuvre, parent, false)
        val lblListHeader = convertView?.findViewById(R.id.lblListHeader) as TextView
        lblListHeader.text = headerTitle
        //lblListHeader.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
        lblListHeader.setTextColor(Color.YELLOW)
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun onGroupExpanded(groupPosition: Int) {
        super.onGroupExpanded(groupPosition)
        Log.d("Liste","Header: " + getGroup(groupPosition))
    }

    init {
        this.mContext = mContext
        this.mListDataHeader = mListDataHeader
        this.mListDataChild = mListDataChild
    }
}