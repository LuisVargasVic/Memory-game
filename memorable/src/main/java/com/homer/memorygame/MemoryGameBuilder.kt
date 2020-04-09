package com.homer.memorygame

/**
 * Created by Luis Vargas on 11/16/18.
 */

class MemoryGameBuilder (
    memoryGameView: MemoryGameView,
    listener: MemoryGameListener,
    memoryItems: MutableList<MemoryItem>,
    numClicks: Int,
    init: Boolean,
    imageOne: Int?,
    imageTwo: Int?,
    moves: Int,
    seconds: Int,
    span: Int
){

    init {
        memoryGameView.setUpMemorable(listener, memoryItems, numClicks, init, imageOne, imageTwo, moves, seconds, span)
    }
}