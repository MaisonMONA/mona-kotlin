package com.maison.mona.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.maison.mona.R
import com.maison.mona.databinding.BadgeRecyclerviewBadgeBinding
import com.maison.mona.databinding.BadgeRecyclerviewHeaderBinding
import com.maison.mona.entity.Badge_2
import com.maison.mona.fragment.Badge2DetailFragment

class Badge_2Adapter internal constructor (
    context: Context?
): RecyclerView.Adapter<Badge_2Adapter.BaseViewHolder<*>>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList = emptyList<Any>()
    private var badgeList = emptyList<Badge_2>()
//    private val navController = TODO()

    private var mToolbar: Toolbar? = null
    private var mActivity: Activity? = null
    private var mFragmentSupportManager: FragmentManager? = null

    private val onClickCategoryLayer: View.OnClickListener = View.OnClickListener { mActivity?.finish() }

    private val onClickBadgeLayer: View.OnClickListener = View.OnClickListener {
        val test: List<String> = listOf("Quartiers", "Oeuvres", "Autres")
        submitList(test)
        mToolbar?.setOnClickListener(onClickCategoryLayer)
    }

    private val onClickDetailLayer: View.OnClickListener = View.OnClickListener{
        val ft2 = mFragmentSupportManager?.beginTransaction()
        mFragmentSupportManager?.popBackStack()
        ft2?.commit()
        inBadge = false
        mToolbar?.setOnClickListener(onClickBadgeLayer)
    }

    private var inBadge = false

    companion object{
        private var TYPE_HEADER = 0
        private var TYPE_BADGE = 1
    }

    inner class Badge2ViewHolder(
        private val binding: BadgeRecyclerviewBadgeBinding
    ) : BaseViewHolder<Badge_2>(binding.root){
        init{
            binding.setClickListener {
                if(!inBadge){
                    val badge = binding.badge2
                    val ft = mFragmentSupportManager?.beginTransaction()
                    ft?.add(R.id.fragment_container, Badge2DetailFragment(badge))
                    ft?.addToBackStack(null)
                    ft?.commit()
                    inBadge = true
                    mToolbar?.setOnClickListener(onClickDetailLayer)
                }
            }
        }

        override fun bind(item: Badge_2) {
            binding.apply{
                badge2 = item
                executePendingBindings()
            }

            if(binding.badge2!!.isCollected){
                binding.imageView.setImageResource(R.drawable.verdun_color)
            } else{
                binding.imageView.setImageResource(R.drawable.verdun_grey)
            }
        }
    }

    inner class BadgeHeaderViewHolder(
        private val binding: BadgeRecyclerviewHeaderBinding
    ) : BaseViewHolder<String>(binding.root){

        init {
            binding.setClickListener {
                mToolbar?.setOnClickListener(onClickBadgeLayer)

                when(binding.messageHeader.text){
                    "Quartiers" -> {
                        submitList(badgeList.filter { it.optional_args!!.contains("borough") })
                    }
                    "Oeuvres" -> {
                        submitList(badgeList.filter { it.optional_args!!.length < 5 })
                    }
                    "Autres" -> {
                        submitList(badgeList.filter{ it.optional_args!!.length >= 5 && !it.optional_args!!.contains("borough")})
                    }
                }
            }
        }

        override fun bind(item: String) {
            val featuredView: TextView = binding.messageHeader
            featuredView.text = item
        }
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView){
        abstract fun bind(item: T)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when(viewType) {
            TYPE_HEADER -> {
                val itemBinding = BadgeRecyclerviewHeaderBinding.inflate(inflater, parent, false)
                BadgeHeaderViewHolder(itemBinding)
            }
            TYPE_BADGE -> {
                val itemBinding = BadgeRecyclerviewBadgeBinding.inflate(inflater, parent, false)
                Badge2ViewHolder(itemBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = itemList[position]
        if(holder is BadgeHeaderViewHolder){
            holder.bind(element as String)
        } else if (holder is Badge2ViewHolder){
            holder.bind(element as Badge_2)
        } else{
            throw IllegalArgumentException()
        }
    }

    override fun getItemCount() = itemList.size

    internal fun submitList(items: List<Any>){
        this.itemList = items
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val comparable = itemList[position]
        return when (comparable) {
            is Badge_2 -> TYPE_BADGE
            is String -> TYPE_HEADER
            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }

    internal fun giveAdapter(tool: Toolbar?, badges: List<Badge_2>, activity: Activity, fragmentM: FragmentManager){
        mToolbar = tool
        badgeList = badges
        mActivity = activity
        mFragmentSupportManager = fragmentM
    }
}