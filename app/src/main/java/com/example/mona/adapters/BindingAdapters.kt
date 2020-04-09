package com.example.mona.adapters

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import com.example.mona.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

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