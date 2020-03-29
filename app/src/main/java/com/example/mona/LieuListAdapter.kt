package com.example.mona

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mona.entity.Lieu
import com.example.mona.entity.Oeuvre


class LieuListAdapter internal constructor(
    context: Context?
) : RecyclerView.Adapter<LieuListAdapter.LieuViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var lieuList = emptyList<Lieu>()

    //items are clickable
    var onItemClick: ((Lieu) -> Unit)? = null


    inner class LieuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lieuTitleItemView: TextView = itemView.findViewById(R.id.titleView)
        val lieuBoroughItemView: TextView = itemView.findViewById(R.id.boroughView)

        //action happens
        //https://stackoverflow.com/questions/29424944/recyclerview-itemclicklistener-in-kotlin
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(lieuList[adapterPosition])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LieuViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return LieuViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LieuViewHolder, position: Int) {
        val current = lieuList[position]
        holder.lieuTitleItemView.text = current.title
        holder.lieuBoroughItemView.text = current.borough
    }

    internal fun submitList(lieux: List<Lieu>) {

        //Initially, sort the list alphabetically
        //https://stackoverflow.com/questions/37259159/sort-collection-by-multiple-fields-in-kotlin
        val sortedList = lieux.sortedWith(compareBy(Lieu::title, Lieu::borough))

        this.lieuList = sortedList

        notifyDataSetChanged()
    }

    override fun getItemCount() = lieuList.size
}