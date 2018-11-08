package com.adventa.memorama.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
    private lateinit var imageOne: ImagesMem
    private lateinit var imageTwo: ImagesMem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpMemorable()
    }

    data class ImagesMem(
        val image: String,
        val type: Int,
        var view: Boolean,
        var match: Boolean,
        var number: Int
    )

    private fun simpleAlertDialog(message: String){
        val alertDialog = android.support.v7.app.AlertDialog.Builder(this@MainActivity).create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setMessage(message)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Reiniciar") { dialogInterface, i ->
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
                        images.add(ImagesMem(obj, i, false,false, 1))
                        images.add(ImagesMem(obj, i, false,false, 2))
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
                        gridView.adapter = ImageAdapter(images)
                        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, view, position, _ ->
                            numClicks += 1
                            view.findViewById<RelativeLayout>(R.id.iv_icon_close).visibility = View.GONE
                            images[position].view = true
                            when (numClicks) {
                                1 -> {
                                    imageOne = images[position]
                                }
                                2 -> {
                                    imageTwo = images[position]
                                    if (imageOne.view && imageTwo.view && imageOne.type == imageTwo.type && imageOne.number != imageTwo.number){
                                        imageOne.match = true
                                        imageTwo.match = true
                                        checkResult(timer)
                                    }
                                }
                                3 -> {
                                    gridView.adapter = ImageAdapter(images)
                                    checkResult(timer)
                                }
                            }
                        }
                    }
                }
            })
        }
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
            timer.cancel()
            simpleAlertDialog("Lo lograste en $movements movimientos y te tomó $count segundos")
        } else {
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

            val layoutInflater = LayoutInflater.from(this@MainActivity)
            convertView = layoutInflater.inflate(R.layout.image_layout, null)

            val ivIconOpen = convertView.findViewById<ImageView>(R.id.iv_icon_open)

            if (imagesMem[position].match){
                convertView.findViewById<RelativeLayout>(R.id.iv_icon_close).visibility = View.GONE
            }

            Picasso.get()
                .load(imagesMem[position].image)
                .into(ivIconOpen)

            return convertView
        }
    }

}
