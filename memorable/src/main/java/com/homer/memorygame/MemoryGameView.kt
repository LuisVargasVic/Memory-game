package com.homer.memorygame

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.os.Parcelable
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.homer.memorygame.databinding.ItemMemoryBinding
import com.homer.memorygame.databinding.ViewVictoryBinding
import java.util.*

/**
 * Created by Luis Vargas on 11/16/18.
 */

class MemoryGameView @kotlin.jvm.JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private lateinit var movesTextView: TextView
    private lateinit var memoryItems: MutableList<MemoryItem>
    private var activity: Activity? = null
    private var numClicks = 0
    private var moves = 0
    private var seconds = 0
    private var span = 0
    private var finish = false
    private var itemOne: Int? = null
    private var itemTwo: Int? = null
    private var listener: MemoryGameListener? = null
    private var mTimer: Timer? = null

    companion object {
        const val TAG = "MemorableView"
    }

    init {
        activity = getActivity()

        if (activity == null) {
            throw RuntimeException(String.format("%s must be call from activity", TAG))
        }

        LayoutInflater.from(activity)
            .cloneInContext(ContextThemeWrapper(context, R.style.AppTheme))
            .inflate(R.layout.memory_game_view, this)
    }

    private fun getActivity(): Activity? {
        var context = context

        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

    private fun simpleAlertDialog(activity: Activity){
        val alertDialog = AlertDialog.Builder(activity).create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val inflater = activity.layoutInflater
        val bodyView: ViewVictoryBinding = DataBindingUtil.inflate(inflater, R.layout.view_victory, this, false)
        bodyView.tvMovements.text = activity.getString(R.string.activity_memory_game_moves, moves)
        bodyView.tvTime.text = activity.getString(R.string.activity_memory_game_seconds, seconds)
        alertDialog.setView(bodyView.root)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.getString(R.string.activity_memory_game_restart)) { _, _ ->
            finish = false
            seconds = 0
            moves = 0
            memoryItems.forEach {
                it.match = false
                it.view = false
            }
            setUpMemorable(listener, memoryItems, 0, true, null, null, 0, 0, span)
            alertDialog.dismiss()
        }
        alertDialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                alertDialog.dismiss()
                activity.finish()
            }
            true
        }
        alertDialog.show()
    }

    fun setUpMemorable(
        listener: MemoryGameListener?,
        memoryItems: MutableList<MemoryItem>,
        numClicks: Int,
        init: Boolean,
        imageOne: Int?,
        imageTwo: Int?,
        moves: Int,
        seconds: Int,
        span: Int
    ) {
        this.moves = moves
        this.seconds = seconds
        this.itemOne = imageOne
        this.itemTwo = imageTwo
        this.numClicks = numClicks
        this.memoryItems = memoryItems
        this.span = span
        val timeTextView = findViewById<TextView>(R.id.tv_user_time)
        val stringTime = if (seconds == 1) R.string.activity_memory_game_second else R.string.activity_memory_game_seconds
        timeTextView.text = activity?.getString(stringTime, seconds)
        movesTextView = findViewById(R.id.tv_user_moves)
        val stringMoves= if (moves == 1) R.string.activity_memory_game_move else R.string.activity_memory_game_moves
        movesTextView.text = activity?.getString(stringMoves, moves)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            activity?.onBackPressed()
        }

        mTimer = Timer()
        mTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                this@MemoryGameView.activity!!.runOnUiThread {
                    this@MemoryGameView.seconds++
                    val string = if (this@MemoryGameView.seconds == 1) R.string.activity_memory_game_second else R.string.activity_memory_game_seconds
                    timeTextView.text = activity?.getString(string, this@MemoryGameView.seconds)
                }
            }
        }, 1000, 1000)

        checkResult(mTimer, activity!!)

        this@MemoryGameView.listener = listener
        if (init) this.memoryItems.shuffle()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, span)
        }
        recyclerView.adapter = MemoryGameAdapter()
        isClickable = true
    }

    fun getMoves(): Int {
        return moves
    }

    fun getSeconds(): Int {
        return seconds
    }

    private fun checkResult(timer: Timer?, activity: Activity) {
        if (memoryItems.filter { it.match }.size == memoryItems.size){
            finish = true
        }
        if (finish){
            numClicks = 0
            timer?.cancel()
            listener?.onFinalize()
            simpleAlertDialog(activity)
        } else {
            if (itemOne != null && itemTwo != null) {
                memoryItems[itemOne!!].view = false
                memoryItems[itemTwo!!].view = false
                moves += 1
                val stringMoves =
                    if (moves == 1) R.string.activity_memory_game_move else R.string.activity_memory_game_moves
                movesTextView.text = activity.getString(stringMoves, moves)
            }
        }
    }

    fun getNumClicks(): Int {
        return numClicks
    }

    fun getImages(): List<Parcelable> {
        return memoryItems
    }

    fun getImageOne(): Int? {
        return itemOne
    }

    fun getImageTwo(): Int? {
        return itemTwo
    }

    internal inner class MemoryGameAdapter : RecyclerView.Adapter<MemoryGameAdapter.ViewHolder>() {

        override fun getItemCount(): Int {
            return memoryItems.size
        }

        override fun getItemId(position: Int): Long {
            return memoryItems[position].hashCode().toLong()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val inflater = LayoutInflater.from(activity)
                .cloneInContext(ContextThemeWrapper(context, R.style.AppTheme))

            val itemMemoryBinding: ItemMemoryBinding = DataBindingUtil.inflate(
                    inflater,
                R.layout.item_memory,
                parent,
                false
            )
            return ViewHolder(itemMemoryBinding = itemMemoryBinding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(memoryItems[position], position)
        }

        inner class ViewHolder(val itemMemoryBinding: ItemMemoryBinding): RecyclerView.ViewHolder(itemMemoryBinding.root), OnClickListener {

            private lateinit var mMemoryItem: MemoryItem
            private var mPosition: Int? = null

            fun bind(imageMem: MemoryItem, position: Int) {
                mPosition = position
                mMemoryItem = imageMem

                itemMemoryBinding.ivMemory.setOnClickListener(this)

                if (mPosition != null) {
                    if (memoryItems[mPosition!!].match || memoryItems[mPosition!!].view) {
                        itemMemoryBinding.ivMemory.flipImage(memoryItems[mPosition!!].image)
                    } else if (!memoryItems[mPosition!!].view) {
                        itemMemoryBinding.ivMemory.flipImage(R.drawable.background)
                    }
                }
            }

            override fun onClick(p0: View?) {
                if (isClickable) {
                    numClicks += 1
                    memoryItems[mPosition!!].view = true
                    when (numClicks) {
                        1 -> {
                            itemOne = mPosition!!
                            if (!memoryItems[itemOne!!].match) {
                                itemMemoryBinding.ivMemory.flipImage(memoryItems[itemOne!!].image)
                            } else {
                                numClicks = 0
                            }
                        }
                        2 -> {
                            itemTwo = mPosition!!
                            if (!memoryItems[itemTwo!!].match && itemOne != itemTwo) {
                                itemMemoryBinding.ivMemory.flipImage(memoryItems[itemTwo!!].image)
                            } else {
                                numClicks = 1
                                return
                            }
                            if (memoryItems[itemOne!!].view && memoryItems[itemTwo!!].view
                                && memoryItems[itemOne!!].type == memoryItems[itemTwo!!].type
                                && memoryItems[itemOne!!].number != memoryItems[itemTwo!!].number
                            ) {
                                memoryItems[itemOne!!].match = true
                                memoryItems[itemTwo!!].match = true
                            } else {
                                isClickable = false
                                val handler = Handler()
                                handler.postDelayed({
                                    memoryItems[itemOne!!].view = false
                                    memoryItems[itemTwo!!].view = false
                                    notifyItemChanged(itemOne!!)
                                    notifyItemChanged(itemTwo!!)
                                    isClickable = true
                                }, 1000)
                            }
                            numClicks = 0
                            checkResult(mTimer, activity!!)
                        }
                    }
                }
            }
        }
    }
}