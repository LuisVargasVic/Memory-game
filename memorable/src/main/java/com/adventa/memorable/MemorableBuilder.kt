package com.adventa.memorable

import android.content.Context

/**
 * Created by jonathan on 11/16/18.
 */

class MemorableBuilder (
    memorableView: MemorableView,
    activity: Context
){

    init {
        memorableView.setUpMemorable(activity)
    }
}