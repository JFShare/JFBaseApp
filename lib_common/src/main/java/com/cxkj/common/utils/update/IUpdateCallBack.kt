package com.cxkj.common.utils.update

import okhttp3.Response


/**
 * @description
 *
 *
 * @author Created by linyincongxingkeji on  2022/3/7 8:57
 */

interface IUpdateCallBack {
    fun onNeedUpdate(versionName : String , versionDes : String,updateUrl:String)
    fun onIgnoreUpdate()
}