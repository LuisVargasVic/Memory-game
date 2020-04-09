package com.homer.memorygame.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.homer.memorygame.*
import com.homer.memorygame.app.LobbyActivity.Companion.COLUMNS
import com.homer.memorygame.app.LobbyActivity.Companion.ROWS
import com.homer.memorygame.app.databinding.ActivityMemoryGameBinding

class MemoryGameActivity : AppCompatActivity(), MemoryGameListener {

    private lateinit var activityMemoryGameBinding: ActivityMemoryGameBinding
    private lateinit var memoryGameBuilder: MemoryGameBuilder
    private var memoryItems = mutableListOf<MemoryItem>()
    private var columns = 0
    private var rows = 0
    private var init: Boolean = true
    private var numClicks: Int = 0
    private var imageOne: Int? = null
    private var imageTwo: Int? = null
    private var moves: Int = 0
    private var seconds: Int = 0

    companion object {
        private const val IMAGES: String = "IMAGES"
        private const val NUM_CLICKS: String = "NUM_CLICKS"
        private const val INIT: String = "INIT"
        private const val IMAGE_ONE: String = "IMAGE_ONE"
        private const val IMAGE_TWO: String = "IMAGE_TWO"
        private const val MOVES: String = "MOVEMENTS"
        private const val SECONDS: String = "TIME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMemoryGameBinding = DataBindingUtil.setContentView(this, R.layout.activity_memory_game)

        if (intent.hasExtra(COLUMNS)) {
            columns = intent.getIntExtra(COLUMNS, 0)
        } else if (savedInstanceState?.getInt(COLUMNS) != null) {
            columns = savedInstanceState.getInt(COLUMNS)
        }

        if (intent.hasExtra(ROWS)) {
            rows = intent.getIntExtra(ROWS, 0)
        } else if (savedInstanceState?.getInt(ROWS) != null) {
            rows = savedInstanceState.getInt(ROWS)
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelableArray(IMAGES) != null) {
                for (imageMem in savedInstanceState.getParcelableArray(IMAGES)!!.toMutableList()) {
                    memoryItems.add(imageMem as MemoryItem)
                }
            }
            numClicks = savedInstanceState.getInt(NUM_CLICKS)
            init = savedInstanceState.getBoolean(INIT)
            if (savedInstanceState.getString(IMAGE_ONE) != null) imageOne = savedInstanceState.getString(IMAGE_ONE)!!.toInt()
            if (savedInstanceState.getString(IMAGE_TWO) != null) imageTwo = savedInstanceState.getString(IMAGE_TWO)!!.toInt()
            moves = savedInstanceState.getInt(MOVES)
            seconds = savedInstanceState.getInt(SECONDS)
        } else {
            for (i in 0 until (columns * rows) / 2) {
                val image = i.showImage()
                memoryItems.add(MemoryItem(image, i,false, false,1))
                memoryItems.add(MemoryItem(image, i,false, false,2))
            }
        }

        setUp()

    }

    private fun setUp() {
        memoryGameBuilder = MemoryGameBuilder(activityMemoryGameBinding.memoryGameView, this, memoryItems, numClicks, init, imageOne, imageTwo, moves, seconds, columns)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(NUM_CLICKS, activityMemoryGameBinding.memoryGameView.getNumClicks())
        outState.putParcelableArray(IMAGES, activityMemoryGameBinding.memoryGameView.getImages().toTypedArray())
        outState.putBoolean(INIT, false)
        if (activityMemoryGameBinding.memoryGameView.getImageOne() != null) outState.putString(IMAGE_ONE, activityMemoryGameBinding.memoryGameView.getImageOne()!!.toString())
        if (activityMemoryGameBinding.memoryGameView.getImageTwo() != null) outState.putString(IMAGE_TWO, activityMemoryGameBinding.memoryGameView.getImageTwo()!!.toString())
        outState.putInt(MOVES, activityMemoryGameBinding.memoryGameView.getMoves())
        outState.putInt(SECONDS, activityMemoryGameBinding.memoryGameView.getSeconds())
        outState.putInt(COLUMNS, columns)
        outState.putInt(ROWS, rows)
    }

    override fun onFinalize() {
        Toast.makeText(this, "Moves: " + activityMemoryGameBinding.memoryGameView.getMoves() +
                " Seconds: " + activityMemoryGameBinding.memoryGameView.getSeconds(), Toast.LENGTH_SHORT).show()
    }

}
