package com.cxkj.common.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import com.cxkj.common.base.BaseApp
import java.util.*

class AppHelper private constructor() {

    companion object {
        val instance : AppHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AppHelper()
        }
    }

    lateinit var mApp : Application
    lateinit var mContext : Context

    var activityLinkedList : LinkedList<Activity>? = null

    fun init(application : Application) {
        mApp = application
        this.mContext = application.applicationContext
        activityLinkedList = LinkedList()
        mApp.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity : Activity , savedInstanceState : Bundle?) {
                Log.e(BaseApp.TAG , "onActivityCreated: " + activity.localClassName)
                activityLinkedList!!.add(activity)
            }

            override fun onActivityDestroyed(activity : Activity) {
                Log.e(BaseApp.TAG , "onActivityDestroyed: " + activity.localClassName)
                activityLinkedList!!.remove(activity)
            }

            override fun onActivityStarted(activity : Activity) {}
            override fun onActivityResumed(activity : Activity) {}
            override fun onActivityPaused(activity : Activity) {}
            override fun onActivityStopped(activity : Activity) {}
            override fun onActivitySaveInstanceState(activity : Activity , outState : Bundle) {}
        })

    }

    fun exitApp() {
        Log.e(BaseApp.TAG , "正逐步退出容器内所有Activity")
        // 逐个退出Activity
        for (activity in activityLinkedList!!) {
            activity.finish()
        }
    }

    fun getVersion() : Int {
        var info : PackageInfo? = null
        val manager : PackageManager = mApp.packageManager
        try {
            info = manager.getPackageInfo(mApp.packageName , 0)
        } catch (e : PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return info!!.versionCode
    }

    fun getVersionName() : String {
        var info : PackageInfo? = null
        val manager : PackageManager = mApp.packageManager
        try {
            info = manager.getPackageInfo(mApp.packageName , 0)
        } catch (e : PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return info!!.versionName
    }

    fun getApplicationId() : String {
        return mApp.packageName
    }
}