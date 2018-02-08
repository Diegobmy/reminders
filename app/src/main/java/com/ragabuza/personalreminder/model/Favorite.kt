package com.ragabuza.personalreminder.model

/**
 * Created by diego.moyses on 2/8/2018.
 */
data class Favorite(
        val id: Long,
        var tag: Int,
        var icon: Int,
        var type: String,
        var condition: String,
        var location: String = ""
)