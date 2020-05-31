package com.example.mona.adapters

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


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