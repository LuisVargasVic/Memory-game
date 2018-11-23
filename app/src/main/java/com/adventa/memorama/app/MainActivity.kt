package com.adventa.memorama.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.adventa.memorable.MemorableBuilder
import com.adventa.memorable.MemorableListener
import com.adventa.memorable.MemorableView


class MainActivity : AppCompatActivity(), MemorableListener {

    override fun onInitialize() {
        Toast.makeText(this, "Movements: " + memorableView.getMovements() +
                 " Time: " + memorableView.getTime(), Toast.LENGTH_SHORT).show()
    }

    override fun onFinalize() {
        Toast.makeText(this, "Movements: " + memorableView.getMovements() +
                " Time: " + memorableView.getTime(), Toast.LENGTH_SHORT).show()

    }

    private lateinit var memorableView: MemorableView
    private lateinit var memorableBuilder: MemorableBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUp()
    }

    private fun setUp() {
        memorableView = findViewById(R.id.memorable_view)
        memorableBuilder = MemorableBuilder(memorableView, this, this)
    }

}
