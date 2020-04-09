package com.adventa.memorama.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adventa.memorable.*
import com.adventa.memorama.app.LobbyActivity.Companion.COLUMNS
import com.adventa.memorama.app.LobbyActivity.Companion.ROWS

class MemoryGameActivity : AppCompatActivity(), MemorableListener {

    override fun onFinalize() {
        Toast.makeText(this, "Movements: " + memorableView.getMoves() +
                " Time: " + memorableView.getSeconds(), Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val IMAGES: String = "IMAGES"
        private const val NUM_CLICKS: String = "NUM_CLICKS"
        private const val INIT: String = "INIT"
        private const val IMAGE_ONE: String = "IMAGE_ONE"
        private const val IMAGE_TWO: String = "IMAGE_TWO"
        private const val MOVES: String = "MOVEMENTS"
        private const val SECONDS: String = "TIME"
    }

    private lateinit var memorableView: MemorableView
    private lateinit var memorableBuilder: MemorableBuilder
    private var images = mutableListOf<MemoryItem>()
    private var columns = 0
    private var rows = 0
    private var init: Boolean = true
    private var numClicks: Int = 0
    private var imageOne: Int? = null
    private var imageTwo: Int? = null
    private var moves: Int = 0
    private var seconds: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_game)

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
                    images.add(imageMem as MemoryItem)
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
                images.add(MemoryItem(image, i,false, false,1))
                images.add(MemoryItem(image, i,false, false,2))
            }
        }

        setUp()

    }

    private fun setUp() {
        memorableView = findViewById(R.id.memorable_view)
        memorableBuilder = MemorableBuilder(memorableView, this, images, numClicks, init, imageOne, imageTwo, moves, seconds, columns)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(NUM_CLICKS, memorableView.getNumClicks())
        outState.putParcelableArray(IMAGES, memorableView.getImages().toTypedArray())
        outState.putBoolean(INIT, false)
        if (memorableView.getImageOne() != null) outState.putString(IMAGE_ONE, memorableView.getImageOne()!!.toString())
        if (memorableView.getImageTwo() != null) outState.putString(IMAGE_TWO, memorableView.getImageTwo()!!.toString())
        outState.putInt(MOVES, memorableView.getMoves())
        outState.putInt(SECONDS, memorableView.getSeconds())
        outState.putInt(COLUMNS, columns)
        outState.putInt(ROWS, rows)
    }

}
