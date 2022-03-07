package com.cxkj.common.utils.update

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.cxkj.common.BuildConfig
import com.cxkj.common.utils.AppHelper
import com.cxkj.common.utils.ToastUtils
import com.cxkj.common.utils.update.bean.UpdateBean
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread
import com.lxj.xpopup.XPopup
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit


class UpdateUtils {

    companion object {
        const val TAG = "UpdateUtils"
        const val CODE_IGNORE_UPDATE = "ignoreUpdate"
        val instance : UpdateUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            UpdateUtils()
        }
    }

    private var isChecking = false
    private lateinit var activity : Activity
    private lateinit var apkFileName : String
    private var handleCustom = false
    private var updateCallBack : IUpdateCallBack? = null
    private var downLoadCallBack : IDownloadCallBack? = null

    private var mApkFile : File? = null
    private var mDialog : VersionUpdateDialog? = null
    fun checkUpdate(
        activity : Activity ,
        url : String , map : HashMap<String , String> ,
        apkFileName : String ,
        handleCustom : Boolean = false ,
        updateCallBack : IUpdateCallBack? = null ,
        downLoadCallBack : IDownloadCallBack? = null ,
    ) {
        if (isChecking) {
            Log.e(TAG , "正在检查更新，请稍后...")
            return
        }
        isChecking = true
        this.activity = activity
        this.apkFileName = apkFileName
        this.handleCustom = handleCustom
        this.updateCallBack = updateCallBack
        this.downLoadCallBack = downLoadCallBack
        val okHttpClient : OkHttpClient = OkHttpClient.Builder().callTimeout(30 , TimeUnit.SECONDS)
                .connectTimeout(30 , TimeUnit.SECONDS).readTimeout(30 , TimeUnit.SECONDS)
                .writeTimeout(30 , TimeUnit.SECONDS).retryOnConnectionFailure(true).build()
        val formBodeBuilder = FormBody.Builder()
        for (param in map) {
            formBodeBuilder.add(param.key , param.value)
            Log.e(TAG , "检查更新参数：${param.key} - ${param.value}")
        }
        val requestBody : RequestBody = formBodeBuilder.build()
        val request : Request = Request.Builder().url(url).post(requestBody).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call : Call , e : IOException) {
                isChecking = false
                Log.e(TAG , "检查更新失败：" + e.message)
            }

            override fun onResponse(call : Call , response : Response) {
                runOnUiThread {
                    isChecking = false
                    if (request.body != null) {
                        val resultInfo = response.body!!.string()
                        Log.e("检查更新结果：" , resultInfo)
                        val gson = Gson()
                        val bean : UpdateBean = gson.fromJson(resultInfo , UpdateBean::class.java)
                        if (bean.success) {
                            val appInfo = bean.result.appInfo
                            if (appInfo.versionNo > AppHelper.instance.getVersion()) {
                                if (CODE_IGNORE_UPDATE == appInfo.updateType) {
                                    if (handleCustom) {
                                        updateCallBack?.onIgnoreUpdate()
                                    } else {
                                        Log.e(TAG , "检查更新结果：忽略更新")
                                    }
                                } else {
                                    if (handleCustom) {
                                        updateCallBack?.onNeedUpdate(appInfo.versionName ,
                                            appInfo.updDesc , appInfo.downloadUrl)
                                    } else {
                                        Log.e(TAG , "检查更新结果：需要更新")
                                        showUpdateDialog(appInfo.versionName , appInfo.updDesc ,
                                            appInfo.downloadUrl)
                                    }
                                }
                            } else {
                                if (handleCustom) {
                                    updateCallBack?.onIgnoreUpdate()
                                } else {
                                    Log.e(TAG , "检查更新结果：当前App版本号大于等于最新版本号，无需更新")
                                }
                            }
                        } else {
                            Log.e(TAG , "检查更新失败：返回数据错误")
                        }
                    } else {
                        Log.e(TAG , "检查更新失败：返回结果为空")
                    }
                }

            }
        })
    }


    fun showUpdateDialog(versionName : String , versionDes : String , downloadUrl : String) {
        if (mDialog != null && mDialog!!.isShow) {
            return
        }
        mDialog = XPopup.Builder(activity).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .asCustom(VersionUpdateDialog(activity , versionName , versionDes ,
                    object : VersionUpdateDialog.IVersionDialogCallBack {
                        override fun onStartDownload() {
                            downloadApkFile(activity , downloadUrl)
                        }

                        override fun onBrowserDownload() {
                            val uri = Uri.parse(downloadUrl)
                            val intent = Intent("android.intent.action.VIEW" , uri)
                            activity.startActivity(intent)
                        }

                        override fun onStartInstall() {
                            installApk()
                        }

                        override fun onAgainStartDownload() {
                            downloadApkFile(activity , downloadUrl)
                        }

                    })).show() as VersionUpdateDialog
    }


    //下载安装包文件
    fun downloadApkFile(context : Context , downUrl : String) {
        val logInterceptor = HttpLoggingInterceptor { message : String? ->
            Log.d(TAG , message!!)
        }
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClientBuild : OkHttpClient.Builder = OkHttpClient().newBuilder()
        okHttpClientBuild.connectTimeout(60 , TimeUnit.SECONDS).readTimeout(60 , TimeUnit.SECONDS)
                .writeTimeout(60 , TimeUnit.SECONDS).addInterceptor(logInterceptor)
                .addInterceptor(ProgressInterceptor { _ , bytesRead , contentLength , done ->
                    val progress = (bytesRead * 1.0f / contentLength * 100).toInt()
                    runOnUiThread {
                        if (handleCustom) {
                            downLoadCallBack?.onDownLoadProgress(progress)
                        } else {
                            if (mDialog != null && mDialog!!.isShow) {
                                mDialog!!.onDownLoadProgress(progress)
                            }
                        }

                    }
                })

        val okHttpClient : OkHttpClient = okHttpClientBuild.build()
        val request : Request = Request.Builder().url(downUrl).build()



        runOnUiThread {
            if (handleCustom) {
                downLoadCallBack?.onStartDownload()
            } else {
                if (mDialog != null && mDialog!!.isShow) {
                    mDialog!!.onStartDownload()
                }
            }
        }

        //异步请求
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call : Call , e : IOException) {
                // 下载失败监听回调
                runOnUiThread {
                    if (handleCustom) {
                        downLoadCallBack?.onDownLoadError()
                    } else {
                        if (mDialog != null && mDialog!!.isShow) {
                            mDialog!!.onDownLoadError()
                        }
                    }
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call : Call , response : Response) {
                val file = createDownFile(context)
                if (file.exists()) {
                    saveFile(response.body , file)
                } else {
                    runOnUiThread {
                        if (handleCustom) {
                            downLoadCallBack?.onDownLoadError()
                        } else {
                            if (mDialog != null && mDialog!!.isShow) {
                                mDialog!!.onDownLoadError()
                            }
                        }
                    }
                }
            }
        })
    }

    //生成 下载安装包的外部存储目录和文件
    private fun createDownFile(context : Context) : File {
        val parentFile = createApkDownloadPath(context)
        val apkFile = File(parentFile.absolutePath + "/" + apkFileName)
        //如果旧文件存在则删除
        if (apkFile.exists()) {
            deleteSingleFile(apkFile.absolutePath)
        }
        try {
            apkFile.createNewFile()
            return apkFile
        } catch (e : IOException) {
            e.printStackTrace()
        }
        return apkFile
    }

    //构建载文件路径
    private fun createApkDownloadPath(context : Context) : File {
        val path = context.getExternalFilesDir(
            Environment.DIRECTORY_DOWNLOADS)?.absolutePath + File.separator
        Log.e("构建下载文件路径：" , path)
        val customFile = File(path)
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile
    }


    /**
     * 删除单个文件
     *
     * @param filePathName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private fun deleteSingleFile(filePathName : String?) : Boolean {
        val file = File(filePathName)
        return if (file.exists() && file.isFile) {
            file.delete()
        } else {
            false
        }
    }

    //将下载的安装包字节流写入本地文件
    fun saveFile(body : ResponseBody? , apkFile : File) {
        val srcInputStream = body!!.byteStream()
        val buf = ByteArray(2048)
        var len : Int
        var fos : FileOutputStream? = null
        try {
            fos = FileOutputStream(apkFile)
            while (srcInputStream.read(buf).also { len = it } != -1) {
                fos.write(buf , 0 , len)
            }
            fos.flush()
            mApkFile = apkFile
        } catch (e : java.lang.Exception) {
            runOnUiThread {
                if (handleCustom) {
                    downLoadCallBack?.onDownLoadError()
                } else {
                    if (mDialog != null && mDialog!!.isShow) {
                        mDialog!!.onDownLoadError()
                    }
                }
            }
        } finally {
            runOnUiThread {
                if (handleCustom) {
                    downLoadCallBack?.onDownLoadSuccess(mApkFile!!)
                } else {
                    if (mDialog != null && mDialog!!.isShow) {
                        mDialog!!.onDownLoadSuccess(mApkFile!!)
                    }
                    ToastUtils.instance.toast("下载成功，正在安装...")
                    installApk()
                }
            }
            try {
                if (srcInputStream != null) {
                    srcInputStream.close()
                }
                fos?.close()
            } catch (e : IOException) {
            }
        }
    }

    fun installApk() {
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri = FileProvider.getUriForFile(activity ,
                AppHelper.instance.getApplicationId() + ".fileProvider" , mApkFile!!)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri , "application/vnd.android.package-archive")
        } else {
            val uri = Uri.fromFile(mApkFile)
            intent.setDataAndType(uri , "application/vnd.android.package-archive")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    }


}
