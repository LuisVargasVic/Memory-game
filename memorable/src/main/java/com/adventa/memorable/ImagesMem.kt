package com.adventa.memorable

/**
 * Created by jonathan on 11/16/18.
 */

data class ImagesMem(
    val image: String,
    val type: Int,
    var view: Boolean,
    var flip: Boolean,
    var match: Boolean,
    var number: Int
)