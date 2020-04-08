package com.adventa.memorable

import android.content.Context

/**
 * Created by Luis Vargas on 11/16/18.
 */

class MemorableBuilder (
    memorableView: MemorableView,
    activity: Context,
    listener: MemorableListener
){

    init {
        memorableView.setUpMemorable(activity, listener)
    }
}