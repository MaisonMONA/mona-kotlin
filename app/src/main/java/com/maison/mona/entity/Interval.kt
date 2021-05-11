package com.maison.mona.entity

import java.io.Serializable

//a delete ?
//For distance sorting algorithm
data class Interval(
    val distance: Double?,
    val item: Any?
) : Serializable

