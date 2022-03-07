package com.cxkj.common.base

import android.app.Application
import android.util.Log
import com.cxkj.common.utils.AppHelper
import com.cxkj.common.widget.loadsir.ErrorCallback
import com.cxkj.common.widget.loadsir.LoadingCallback
import com.kingja.loadsir.core.LoadSir
import com.tencent.mmkv.MMKV


open class BaseApp : Application() {

    companion object {
        const val TAG = "Application打印信息："
    }

    override fun onCreate() {
        super.onCreate()
        AppHelper.instance.init(this)
        val mmkvDir = MMKV.initialize(this)
        Log.e("MMKV存储路径：" , mmkvDir)
        LoadSir.beginBuilder().addCallback(ErrorCallback()).addCallback(LoadingCallback())
                .setDefaultCallback(LoadingCallback::class.java).commit()
    }

}