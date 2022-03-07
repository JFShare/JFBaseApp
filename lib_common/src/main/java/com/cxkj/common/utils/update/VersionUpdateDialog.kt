package com.cxkj.common.utils.update

import android.content.Context
import android.widget.ProgressBar
import android.widget.TextView
import com.lxj.xpopup.core.CenterPopupView
import com.cxkj.common.R
import com.cxkj.common.utils.singleClick
import java.io.File

class VersionUpdateDialog(
    context : Context , private val versionName : String ,
    private val versionDes : String , private val callBack : IVersionDialogCallBack ,
) : CenterPopupView(context) , IDownloadCallBack {

    private lateinit var tvVersionName : TextView
    private lateinit var tvVersionDes : TextView
    private lateinit var pbDownLoading : ProgressBar
    private lateinit var tvBrowser : TextView
    private lateinit var tvUpdate : TextView

    override fun getImplLayoutId() : Int {
        return R.layout.dialog_version_update
    }

    override fun getMaxWidth() : Int {
        return 0
    }

    override fun getMaxHeight() : Int {
        return 0
    }

    override fun onCreate() {
        super.onCreate()
        tvVersionName = findViewById(R.id.tvVersionName)
        tvVersionDes = findViewById(R.id.tvVersionDes)
        pbDownLoading = findViewById(R.id.pbDownLoading)
        tvUpdate = findViewById(R.id.tvUpdate)
        tvBrowser = findViewById(R.id.tvBrowser)

        tvVersionName.text = "版本号 $versionName"
        tvVersionDes.text = versionDes

        tvBrowser.singleClick {
            callBack.onBrowserDownload()
        }
        tvUpdate.singleClick {
            if ("立即更新" == tvUpdate.text.toString()) {
                callBack.onStartDownload()
            } else if ("立即更新" == tvUpdate.text.toString()) {
                callBack.onStartInstall()
            } else if ("下载失败,重新下载" == tvUpdate.text.toString()) {
                callBack.onAgainStartDownload()
            }
        }
    }


    interface IVersionDialogCallBack {
        fun onBrowserDownload()
        fun onStartDownload()
        fun onStartInstall()
        fun onAgainStartDownload()
    }

    override fun onStartDownload() {
        pbDownLoading.progress = 0
        tvUpdate.text = "下载中..."
        tvUpdate.setTextColor(context.resources.getColor(R.color.colorPrimary))
    }

    override fun onDownLoadProgress(progress : Int) {
        pbDownLoading.progress = progress
    }

    override fun onDownLoadSuccess(apk : File) {
        tvUpdate.text = "立即安装"
        tvUpdate.setTextColor(context.resources.getColor(R.color.colorPrimary))
    }

    override fun onDownLoadError() {
        tvUpdate.text = "下载失败,重新下载"
        tvUpdate.setTextColor(context.resources.getColor(R.color.red))
    }
}