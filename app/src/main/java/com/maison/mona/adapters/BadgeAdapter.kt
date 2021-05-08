package com.maison.mona.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.maison.mona.databinding.RecyclerviewBadgeBinding
import com.maison.mona.entity.Badge
import com.maison.mona.fragment.BadgeFragmentDirections
import java.lang.IllegalArgumentException

//a delete ?
class BadgeAdapter internal constructor(
    context: Context?,
    navController: NavController
) : RecyclerView.Adapter<BadgeAdapter.BaseViewHolder<*>>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var badgeList = emptyList<Any>()
    private val navController = navController

    companion object{
        private var TYPE_BADGE = 0
        private var TYPE_HEADER = 1
    }

    inner class BadgeViewHolder(
        private val binding: RecyclerviewBadgeBinding
    ) : BaseViewHolder<Badge>(binding.root){
        init {
            binding.setClickListener {
                val badge = binding.badge
                badge?.let {
                    val action = BadgeFragmentDirections.badgeToDetail(badge)
                    navController.navigate(action)
                }
            }

        }
        override fun bind(item: Badge) {
            binding.apply {
                badge = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when(viewType) {
            TYPE_BADGE -> {
                val itemBinding = RecyclerviewBadgeBinding.inflate(inflater, parent, false)
                BadgeViewHolder(itemBinding)
            }
            else -> throw IllegalArgumentException("Invalid view typw")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = badgeList[position]
        when(holder) {
            is BadgeViewHolder -> holder.bind(element as Badge)
            else -> throw IllegalArgumentException()
        }
    }

    internal fun submitList(items: List<Any>) {
        this.badgeList = items

        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) : Int {
        val comparable = badgeList[position]
        return when(comparable){
            is Badge -> TYPE_BADGE
            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }

    override fun getItemCount() = badgeList.size

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

}