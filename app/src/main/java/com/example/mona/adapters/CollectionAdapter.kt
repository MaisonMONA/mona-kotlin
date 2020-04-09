package com.example.mona.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mona.R
import com.example.mona.entity.Oeuvre

class CollectionAdapter internal constructor(
    context: Context?
) : RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var collectionList = emptyList<Oeuvre>()

    //items are clickable
    var onItemClick: ((Oeuvre) -> Unit)? = null


    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val oeuvreTitleItemView: TextView = itemView.findViewById(R.id.titleView)
        val oeuvreBoroughItemView: TextView = itemView.findViewById(R.id.boroughView)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(collectionList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return CollectionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val current = collectionList[position]
        holder.oeuvreTitleItemView.text = current.title
        holder.oeuvreBoroughItemView.text = current.borough
    }

    internal fun submitList(collection: List<Oeuvre>) {

        val sortedList = collection.sortedWith(compareBy(Oeuvre::title, Oeuvre::borough))

        this.collectionList = sortedList

        notifyDataSetChanged()
    }

    override fun getItemCount() = collectionList.size

}
