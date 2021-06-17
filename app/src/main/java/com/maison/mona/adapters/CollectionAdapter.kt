package com.maison.mona.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.maison.mona.databinding.RecyclerviewCollectionOeuvreBinding
import com.maison.mona.entity.Oeuvre
import com.maison.mona.fragment.HomeViewPagerFragmentDirections

class CollectionAdapter internal constructor(
    context: Context?,
    private val navController: NavController
) : RecyclerView.Adapter<CollectionAdapter.BaseViewHolder<*>>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList = emptyList<Any>()

    companion object {
        private var TYPE_OEUVRE = 0
    }

    inner class OeuvreViewHolder(
        private val binding: RecyclerviewCollectionOeuvreBinding
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_OEUVRE -> {
                val itemBinding = RecyclerviewCollectionOeuvreBinding.inflate(inflater, parent, false)
                OeuvreViewHolder(itemBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }

    }
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = itemList[position]
        when (holder) {
            is OeuvreViewHolder -> holder.bind(element as Oeuvre)
            else -> throw IllegalArgumentException()
        }
    }
    
    internal fun submitList(items: List<Any>) {
        //Initially, sort the list alphabetically
        //https://stackoverflow.com/questions/37259159/sort-collection-by-multiple-fields-in-kotlin
        //val sortedList = oeuvres.sortedWith(compareBy(Oeuvre::title, Oeuvre::borough))

        this.itemList = items

        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is Oeuvre -> TYPE_OEUVRE
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    override fun getItemCount() = itemList.size

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }
}