package com.adventa.memorama.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adventa.memorama.app.databinding.ActivityLobbyBinding
import com.adventa.memorama.app.databinding.ItemLobbyBinding

class LobbyActivity : AppCompatActivity() {

    companion object {
        const val COLUMNS: String = "COLUMNS"
        const val ROWS: String = "ROWS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityLobbyBinding: ActivityLobbyBinding = DataBindingUtil.setContentView(this, R.layout.activity_lobby)
        activityLobbyBinding.rvLobby.apply {
            layoutManager = LinearLayoutManager(this@LobbyActivity)
            adapter = LobbyAdapter()
        }
    }

    inner class LobbyAdapter: RecyclerView.Adapter<LobbyAdapter.ViewHolder>() {

        val layouts = listOf(
            Layout(3,4),
            Layout(5,2),
            Layout(4,4),
            Layout(4,5)
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemLobbyBinding: ItemLobbyBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this@LobbyActivity),
                R.layout.item_lobby,
                parent,
                false
            )
            return ViewHolder(itemLobbyBinding = itemLobbyBinding)
        }

        override fun getItemCount(): Int {
            return layouts.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(layouts[position])
        }

        inner class ViewHolder(val itemLobbyBinding: ItemLobbyBinding): RecyclerView.ViewHolder(itemLobbyBinding.root), View.OnClickListener {

            lateinit var mLayout: Layout

            fun bind(layout: Layout) {
                mLayout = layout
                itemLobbyBinding.tvGameLayout.text = ("${mLayout.columns} x ${mLayout.rows}")
                itemLobbyBinding.mcvLobby.setOnClickListener(this)
            }

            override fun onClick(p0: View?) {
                val intent = Intent(this@LobbyActivity, MemoryGameActivity::class.java)
                intent.putExtra(COLUMNS, mLayout.columns)
                intent.putExtra(ROWS, mLayout.rows)
                this@LobbyActivity.startActivity(intent)
            }
        }
    }

}