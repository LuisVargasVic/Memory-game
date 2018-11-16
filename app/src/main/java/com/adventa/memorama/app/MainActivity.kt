package com.adventa.memorama.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.*
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var movementsTextView: TextView
    private lateinit var timeTextView: TextView
    private var images: MutableList<ImagesMem> = mutableListOf()
    private var mHandler: Handler? = null
    private var numClicks = 0
    private var movements = 0
    private var count = 0
    private var finish = false
    private var columns = 0
    private lateinit var imageOne: ImagesMem
    private lateinit var imageTwo: ImagesMem
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpMemorable()
    }

    data class ImagesMem(
        val image: String,
        val type: Int,
        var view: Boolean,
        var flip: Boolean,
        var match: Boolean,
        var number: Int
    )

    private fun simpleAlertDialog(message: String){
        val alertDialog = android.support.v7.app.AlertDialog.Builder(this@MainActivity).create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setMessage(message)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Reiniciar") { _, _ ->
            for (index in 0 until images.size) {
                images.removeAt(0)
            }
            finish = false
            count = 0
            movements = 0
            setUpMemorable()
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun setUpMemorable() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://demo9782239.mockable.io/images")
            .build()

        timeTextView = findViewById(R.id.tv_user_time)
        timeTextView.text = ("$count segundos")
        gridView = findViewById(R.id.grid_view)
        movementsTextView = findViewById(R.id.tv_user_movements)
        movementsTextView.text = ("$movements movimientos")

        mHandler = Handler()

        mHandler!!.post {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    System.out.println("request failed: " + e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    val jsonObject = JSONObject(response.body()!!.string())
                    val jsonArray = jsonObject.getJSONArray("images")

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getString(i)
                        //val url = URL(obj)
                        //val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                        images.add(ImagesMem(obj, i, false,false, false,1))
                        images.add(ImagesMem(obj, i, false,false, false,2))
                    }

                    val timer = Timer()
                    timer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                count++
                                timeTextView.text = ("$count segundos")
                            }
                        }
                    }, 1000, 1000)

                    runOnUiThread {
                        images.shuffle()
                        if ((images.size / 2)%3 == 0){
                            columns = 3
                            gridView.numColumns = columns
                        } else {
                            columns = 4
                            gridView.numColumns = columns
                        }
                        gridView.adapter = ImageAdapter(images)
                        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, view, position, _ ->
                            numClicks += 1
                            images[position].view = true
                            imageView = view.findViewById(R.id.iv_icon)
                            for (i in 0 until images.size){
                                images[i].flip = false
                            }
                            if (!images[position].match){
                                flipImage(imageView, images[position].image)
                            }
                            when (numClicks) {
                                1 -> imageOne = images[position]
                                2 -> {
                                    imageTwo = images[position]
                                    if (imageOne.view && imageTwo.view && imageOne.type == imageTwo.type
                                        && imageOne.number != imageTwo.number
                                        && !imageOne.match && !imageTwo.match){
                                        imageOne.match = true
                                        imageTwo.match = true
                                        checkResult(timer)
                                    } else if (imageOne.match && imageTwo.match) {
                                        numClicks = 0
                                    } else if (!imageOne.match && imageTwo.match) {
                                        numClicks = 1
                                        gridView.adapter = ImageAdapter(images)
                                    } else if (imageOne.match && !imageTwo.match) {
                                        numClicks = 1
                                        imageOne = imageTwo
                                        gridView.adapter = ImageAdapter(images)
                                    } else if (imageOne.type == imageTwo.type){
                                        numClicks = 1
                                    } else {
                                        imageOne.view = false
                                        imageTwo.view = true
                                    }
                                }
                                3 -> {
                                    imageOne.flip = true
                                    imageTwo.flip = true
                                    gridView.adapter = ImageAdapter(images)
                                    checkResult(timer)
                                    imageOne = images[position]
                                    numClicks = 1
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    private fun flipImage(imageView: ImageView, image: String) {
        val oa1 = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0f)
        val oa2 = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1f)
        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = AccelerateDecelerateInterpolator()
        oa1.duration = 100
        oa2.duration = 100
        oa1.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                Picasso.get()
                    .load(image)
                    .into(imageView)
                oa2.start()
            }
        })
        oa1.start()
    }

    private fun checkResult(timer: Timer) {
        var matches = 0
        for (i in 0 until images.size){
            if (images[i].match){
                matches++
            }
        }
        if (matches == images.size){
            finish = true
        }
        if (finish){
            numClicks = 0
            timer.cancel()
            simpleAlertDialog("Lo lograste en $movements movimientos y te tomó $count segundos")
        } else {
            imageOne.view = false
            imageTwo.view = false
            movements += 1
            movementsTextView.text = ("$movements movimientos")
            numClicks = 0
        }
    }

    @Suppress("NAME_SHADOWING")
    internal inner class ImageAdapter(private val imagesMem: List<ImagesMem>) : BaseAdapter() {

        override fun getCount(): Int {
            return imagesMem.size
        }

        override fun getItem(position: Int): Any {
            return imagesMem[position]
        }

        override fun getItemId(position: Int): Long {
            return imagesMem[position].hashCode().toLong()
        }

        @SuppressLint("SetTextI18n", "InflateParams", "ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val convertView: View

            convertView = if (columns == 3){
                val layoutInflater = LayoutInflater.from(this@MainActivity)
                layoutInflater.inflate(R.layout.image_layout_three, null)
            } else {
                val layoutInflater = LayoutInflater.from(this@MainActivity)
                layoutInflater.inflate(R.layout.image_layout_four, null)
            }

            val ivIconOpen = convertView.findViewById<ImageView>(R.id.iv_icon)

            if (imagesMem[position].flip){
                flipImage(ivIconOpen, R.drawable.background)
            } else if (imagesMem[position].match || imagesMem[position].view){
                Picasso.get()
                    .load(imagesMem[position].image)
                    .into(ivIconOpen)
            } else {
                Picasso.get()
                    .load(R.drawable.background)
                    .into(ivIconOpen)
            }
            return convertView
        }

        private fun flipImage(imageView: ImageView, image: Int) {
            val oa1 = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0f)
            val oa2 = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1f)
            oa1.interpolator = DecelerateInterpolator()
            oa2.interpolator = AccelerateDecelerateInterpolator()
            oa1.duration = 100
            oa2.duration = 100
            oa1.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    Picasso.get()
                        .load(image)
                        .into(imageView)
                    oa2.start()
                }
            })
            oa1.start()
        }
    }

}
