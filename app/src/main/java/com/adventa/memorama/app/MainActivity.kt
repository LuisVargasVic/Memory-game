package com.adventa.memorama.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.adventa.memorable.MemorableBuilder
import com.adventa.memorable.MemorableView


class MainActivity : AppCompatActivity() {

    private lateinit var memorableView: MemorableView
    private lateinit var memorableBuilder: MemorableBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUp()
    }

    private fun setUp() {
        memorableView = findViewById(R.id.memorable_view)
        memorableBuilder = MemorableBuilder(memorableView, this)
    }

}
