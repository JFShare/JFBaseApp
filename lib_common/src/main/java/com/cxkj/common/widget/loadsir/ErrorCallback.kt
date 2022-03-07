package com.cxkj.common.widget.loadsir

import com.cxkj.common.R
import com.kingja.loadsir.callback.Callback

class ErrorCallback : Callback() {
    override fun onCreateView() : Int {
        return R.layout.loadsir_callback_error
    }
}