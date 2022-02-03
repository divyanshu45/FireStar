package com.example.firestar.model

import java.io.Serializable

data class Item(
    var name: String? = null,
    var imageUri: String? = null,
    var videoUri: String? = null,
    var duration: String? = null,
    var type: Int? = null
) : Serializable