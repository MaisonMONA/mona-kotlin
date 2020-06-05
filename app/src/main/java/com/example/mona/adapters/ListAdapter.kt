package com.example.mona.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mona.R
import com.example.mona.databinding.RecyclerviewHeaderBinding
import com.example.mona.databinding.RecyclerviewLieuBinding
import com.example.mona.databinding.RecyclerviewOeuvreBinding
import com.example.mona.entity.Lieu
import com.example.mona.entity.Oeuvre
import com.example.mona.fragment.HomeViewPagerFragmentDirections

class ListAdapter internal constructor(
    context: Context?,
    navController: NavController
) : RecyclerView.Adapter<ListAdapter.BaseViewHolder<*>>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList = emptyList<Any>()
    private val navController = navController

    companion object {
        private var TYPE_OEUVRE = 0
        private var TYPE_LIEU = 1
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

    inner class LieuViewHolder(
        private val binding: RecyclerviewLieuBinding
    ) : BaseViewHolder<Lieu>(binding.root) {
        init {
            binding.setClickListener {
                val lieu = binding.lieu
                lieu?.let {
                    val action = HomeViewPagerFragmentDirections.homeToLieu(it)
                    navController.navigate(action)
                }
            }
        }
        override fun bind(item: Lieu) {
            binding.apply {
                lieu = item
                executePendingBindings()
            }
        }
    }

    inner class HeaderViewHolder(
        private val binding : RecyclerviewHeaderBinding
    ) : BaseViewHolder<String>(binding.root) {
        override fun bind(item: String) {
            val featuredView: TextView = binding.featuredMessage
            val message = "En vedette cette semaine " + getEmojiByUnicode(0x1F525)
            featuredView.text = message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_OEUVRE -> {
                val itemBinding = RecyclerviewOeuvreBinding.inflate(inflater, parent, false)
                OeuvreViewHolder(itemBinding)
            }
            TYPE_LIEU -> {
                val itemBinding = RecyclerviewLieuBinding.inflate(inflater, parent, false)
                LieuViewHolder(itemBinding)
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
        when (holder) {
            is OeuvreViewHolder -> holder.bind(element as Oeuvre)
            is LieuViewHolder -> holder.bind(element as Lieu)
            is HeaderViewHolder -> holder.bind(element as String)
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
        val comparable = itemList[position]
        return when (comparable) {
            is Oeuvre -> TYPE_OEUVRE
            is Lieu -> TYPE_LIEU
            is String -> TYPE_HEADER
            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }

    override fun getItemCount() = itemList.size


    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    fun getEmojiByUnicode(unicode: Int): String? {
        return String(Character.toChars(unicode))
    }
}