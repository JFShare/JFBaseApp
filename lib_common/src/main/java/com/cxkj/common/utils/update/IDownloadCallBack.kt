package com.cxkj.common.utils.update

import java.io.File


/**
 * @description
 *
 *
 * @author Created by linyincongxingkeji on  2022/3/7 10:08
 */

interface IDownloadCallBack {
    fun onStartDownload()
    fun onDownLoadProgress(progress : Int)
    fun onDownLoadSuccess(apk : File)
    fun onDownLoadError()
}