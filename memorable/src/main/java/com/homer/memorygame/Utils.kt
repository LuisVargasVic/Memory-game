package com.homer.memorygame

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import com.squareup.picasso.Picasso

/**
 * Created by Luis Vargas on 08/04/20.
 */

fun Int.showImage(): Int {
    return when(this) {
        0 -> R.drawable.memory_bat
        1 -> R.drawable.memory_cat
        2 -> R.drawable.memory_cow
        3 -> R.drawable.memory_dragon
        4 -> R.drawable.memory_garbage_man
        5 -> R.drawable.memory_ghost_dog
        6 -> R.drawable.memory_hen
        7 -> R.drawable.memory_horse
        8 -> R.drawable.memory_pig
        else -> R.drawable.memory_spider
    }
}



fun ImageView.flipImage(image: Int) {
    val imageView = this
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