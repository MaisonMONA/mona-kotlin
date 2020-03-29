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
import com.example.mona.entity.Oeuvre

class OeuvreListAdapter internal constructor(
    context: Context?
) : RecyclerView.Adapter<OeuvreListAdapter.OeuvreViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var oeuvreList = emptyList<Oeuvre>()

    //items are clickable
    var onItemClick: ((Oeuvre) -> Unit)? = null


    inner class OeuvreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val oeuvreTitleItemView: TextView = itemView.findViewById(R.id.titleView)
        val oeuvreBoroughItemView: TextView = itemView.findViewById(R.id.boroughView)

        //action happens
        //https://stackoverflow.com/questions/29424944/recyclerview-itemclicklistener-in-kotlin
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(oeuvreList[adapterPosition])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OeuvreViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return OeuvreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OeuvreViewHolder, position: Int) {
        val current = oeuvreList[position]
        holder.oeuvreTitleItemView.text = current.title
        holder.oeuvreBoroughItemView.text = current.borough
    }

    internal fun submitList(oeuvres: List<Oeuvre>) {

        //Initially, sort the list alphabetically
        //https://stackoverflow.com/questions/37259159/sort-collection-by-multiple-fields-in-kotlin
        //val sortedList = oeuvres.sortedWith(compareBy(Oeuvre::title, Oeuvre::borough))

        this.oeuvreList = oeuvres

        notifyDataSetChanged()
    }

    override fun getItemCount() = oeuvreList.size
}