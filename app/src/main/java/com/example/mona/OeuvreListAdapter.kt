package com.example.mona

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OeuvreListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<OeuvreListAdapter.OeuvreViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var oeuvreList = emptyList<Oeuvre>()

    inner class OeuvreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val oeuvreItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OeuvreViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return OeuvreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OeuvreViewHolder, position: Int) {
        val current = oeuvreList[position]
        holder.oeuvreItemView.text = current.title
    }

    internal fun setTitle(words: List<Oeuvre>) {
        this.oeuvreList = words
        notifyDataSetChanged()
    }

    override fun getItemCount() = oeuvreList.size
}