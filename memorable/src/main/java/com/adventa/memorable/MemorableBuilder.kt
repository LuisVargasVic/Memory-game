package com.adventa.memorable

/**
 * Created by Luis Vargas on 11/16/18.
 */

class MemorableBuilder (
    memorableView: MemorableView,
    listener: MemorableListener,
    memoryItem: MutableList<MemoryItem>,
    numClicks: Int,
    init: Boolean,
    imageOne: Int?,
    imageTwo: Int?,
    moves: Int,
    seconds: Int,
    span: Int
){

    init {
        memorableView.setUpMemorable(listener, memoryItem, numClicks, init, imageOne, imageTwo, moves, seconds, span)
    }
}