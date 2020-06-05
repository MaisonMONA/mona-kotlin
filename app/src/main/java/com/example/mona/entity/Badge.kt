package com.example.mona.entity

import java.io.Serializable

data class Badge (
    val name : String,
    val completion_message : String,
    val uncomplete_picture_id : Int,
    val complete_picture_id : Int,
    val collected : Int,
    val collected_goal: Int
) : Serializable