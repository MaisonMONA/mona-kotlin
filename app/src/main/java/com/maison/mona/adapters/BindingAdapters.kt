package com.maison.mona.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.maison.mona.entity.Badge

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("isVisible")
fun bindIsVisible(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("isTarget")
fun bindIsTarget(view: View, isTarget: Boolean) {
    view.visibility = if (isTarget) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("imagePath")
fun loadImage(view: ImageView, photo_path: String?) {
    Glide.with(view.context)
        .load(photo_path)
        .into(view)
}

@BindingAdapter("badgeCompletion")
fun loadBadge(view: ImageView, badge:Badge) {
    //verify which badge to display
    badge.apply {
        view.background = if(collected >= collected_goal){
            ContextCompat.getDrawable(view.context, complete_picture_id)
        } else {
            ContextCompat.getDrawable(view.context, uncomplete_picture_id)
        }
    }
}

@BindingAdapter("badgeProgress")
fun loadProgress(view: TextView, badge:Badge) {
    badge.apply {
        view.text = ""+collected+"/"+collected_goal
    }
}

@BindingAdapter("isBadgeVisible")
fun loadVisibility(view: TextView, badge:Badge) {
    badge.apply {
        view.visibility = if(collected >= collected_goal){
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }
}

/*
@BindingAdapter("setTint")
fun setIconTint(fab: FloatingActionButton, isTarget: Boolean, context: Context){

    var color = R.color.white

    if (isTarget){
        color = R.color.black
    }

    fab.drawable.mutate().setTint(ContextCompat.getColor(context, color))
}

 */