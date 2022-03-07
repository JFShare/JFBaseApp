package com.cxkj.common.utils

import android.text.TextUtils
import android.widget.Toast



class ToastUtils private constructor() {
    companion object {
        val instance : ToastUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ToastUtils()
        }
    }
    private var time : Long = 0
    private var oldMsg : String? = null

    fun toast(msg : String?) {
        if (!TextUtils.isEmpty(msg)) {
            if (msg != oldMsg) {
                Toast.makeText(AppHelper.instance.mApp , msg , Toast.LENGTH_SHORT).show()
                time = System.currentTimeMillis()
            } else {
                if (System.currentTimeMillis() - time > 1000) {
                    Toast.makeText(AppHelper.instance.mApp , msg , Toast.LENGTH_SHORT).show()
                    time = System.currentTimeMillis()
                }
            }
            oldMsg = msg
        }
    }

     fun toast(msg : String? , defaultMsg : String) {
        if (!TextUtils.isEmpty(msg)) {
            toast(msg)
        } else {
            toast(defaultMsg)
        }
    }
}