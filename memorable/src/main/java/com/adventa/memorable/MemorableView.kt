package com.adventa.memorable

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Created by Luis Vargas on 11/16/18.
 */

class MemorableView @kotlin.jvm.JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var activity: Activity? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var movementsTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var memoryItems: MutableList<MemoryItem>
    private var numClicks = 0
    private var movements = 0
    private var count = 0
    private var finish = false
    private var itemOne: Int? = null
    private var itemTwo: Int? = null
    private var listener: MemorableListener? = null
    private lateinit var imageView: ImageView
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
            .inflate(R.layout.memorable_view, this)
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

    private fun simpleAlertDialog(message: String, activity: Context){
        val alertDialog = AlertDialog.Builder(activity).create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setMessage(message)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Reiniciar") { _, _ ->
            finish = false
            count = 0
            movements = 0
            memoryItems.forEach {
                it.match = false
                it.view = false
            }
            setUpMemorable(listener, memoryItems, 0, true, null, null, 0, 0)
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    fun setUpMemorable(
        listener: MemorableListener?,
        memoryItems: MutableList<MemoryItem>,
        numClicks: Int,
        init: Boolean,
        imageOne: Int?,
        imageTwo: Int?,
        movements: Int,
        count: Int
    ) {
        this.movements = movements
        this.count = count
        this.itemOne = imageOne
        this.itemTwo = imageTwo
        this.numClicks = numClicks
        this.memoryItems = memoryItems
        Log.wtf("memoryItems", this.memoryItems.size.toString())
        timeTextView = findViewById(R.id.tv_user_time)
        timeTextView.text = ("$count segundos")
        recyclerView = findViewById(R.id.recycler_view)
        movementsTextView = findViewById(R.id.tv_user_movements)
        movementsTextView.text = ("$movements movimientos")

        mTimer = Timer()
        mTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                this@MemorableView.activity!!.runOnUiThread {
                    this@MemorableView.count++
                    timeTextView.text = ("${this@MemorableView.count} segundos")
                }
            }
        }, 1000, 1000)

        this@MemorableView.listener = listener
        listener?.onInitialize()
        if (init) this.memoryItems.shuffle()
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
        }
        recyclerView.adapter = MemoryGameAdapter()

    }

    fun getMovements(): Int {
        return movements
    }

    fun getTime(): Int {
        return count
    }

    private fun checkResult(timer: Timer?, activity: Context) {
        var matches = 0
        for (i in 0 until memoryItems.size){
            if (memoryItems[i].match){
                matches++
            }
        }
        if (matches == memoryItems.size){
            finish = true
        }
        if (finish){
            numClicks = 0
            timer?.cancel()
            listener?.onFinalize()
            simpleAlertDialog("Lo lograste en $movements movimientos y te tomó $count segundos", activity)
        } else {
            memoryItems[itemOne!!].view = false
            memoryItems[itemTwo!!].view = false
            movements += 1
            movementsTextView.text = ("$movements movimientos")
            numClicks = 0
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

    internal inner class MemoryGameAdapter() : RecyclerView.Adapter<MemoryGameAdapter.ViewHolder>() {

        override fun getItemCount(): Int {
            return memoryItems.size
        }

        override fun getItemId(position: Int): Long {
            return memoryItems[position].hashCode().toLong()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(activity!!.applicationContext)
                .cloneInContext(ContextThemeWrapper(context, R.style.AppTheme))
            val mConvertView = layoutInflater.inflate(R.layout.image_layout, parent, false)
            return ViewHolder(mConvertView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(memoryItems[position], position)
        }

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), OnClickListener {

            lateinit var mMemoryItem: MemoryItem
            var mPosition: Int? = null

            fun bind(imageMem: MemoryItem, position: Int) {
                mPosition = position
                mMemoryItem = imageMem

                val ivIcon = itemView.findViewById<ImageView>(R.id.iv_icon)
                ivIcon.setOnClickListener(this)

                if (mPosition != null) {
                    if (memoryItems[mPosition!!].match || memoryItems[mPosition!!].view) {
                        ivIcon.flipImage(memoryItems[mPosition!!].image)
                    } else if (!memoryItems[mPosition!!].view) {
                        ivIcon.flipImage(R.drawable.background)
                    }
                }
            }

            override fun onClick(p0: View?) {
                numClicks += 1
                memoryItems[mPosition!!].view = true
                imageView = itemView.findViewById(R.id.iv_icon)
                when (numClicks) {
                    1 -> {
                        itemOne = mPosition!!
                        if (!memoryItems[itemOne!!].match){
                            imageView.flipImage(memoryItems[itemOne!!].image)
                        }
                    }
                    2 -> {
                        itemTwo = mPosition!!
                        if (!memoryItems[itemTwo!!].match && itemOne != itemTwo){
                            imageView.flipImage(memoryItems[itemTwo!!].image)
                        }
                        if (memoryItems[itemOne!!].view && memoryItems[itemTwo!!].view
                            && memoryItems[itemOne!!].type == memoryItems[itemTwo!!].type
                            && memoryItems[itemOne!!].number != memoryItems[itemTwo!!].number
                            && !memoryItems[itemOne!!].match && !memoryItems[itemTwo!!].match) {
                            memoryItems[itemOne!!].match = true
                            memoryItems[itemTwo!!].match = true
                            checkResult(mTimer, activity!!)
                        } else if (memoryItems[itemOne!!].match && memoryItems[itemTwo!!].match) {
                            numClicks = 0
                        } else if (!memoryItems[itemOne!!].match && memoryItems[itemTwo!!].match) {
                            numClicks = 1
                        } else if (memoryItems[itemOne!!].match && !memoryItems[itemTwo!!].match) {
                            numClicks = 1
                            itemOne = itemTwo
                        } else if (memoryItems[itemOne!!].type == memoryItems[itemTwo!!].type) {
                            numClicks = 1
                        }
                    }
                    3 -> {
                        if (itemOne!! != mPosition) {
                            notifyItemChanged(itemOne!!)
                        }
                        if (itemTwo!! != mPosition) {
                            notifyItemChanged(itemTwo!!)
                        }
                        checkResult(mTimer, activity!!)
                        itemOne = mPosition!!
                        if (!memoryItems[itemOne!!].match){
                            imageView.flipImage(memoryItems[itemOne!!].image)
                        }
                        numClicks = 1
                    }
                }
            }
        }
    }
}