package com.cxkj.common.net

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TAG = "RetrofitManager"

class RetrofitManager private constructor() {

    companion object {
        val instance : RetrofitManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitManager()
        }
    }

    //网络状态
    var NETCODE_SUCCESS = 200  //成功
    var NETCODE_TOKEN_OVERDUE = 401 //token过期

    private var mRetrofit : Retrofit? = null
    private var mOkClient : OkHttpClient = getOkHttpClientBuilder().addInterceptor(
        HttpLoggingInterceptor { message ->
            Log.e(TAG , "log: $message")
        }.setLevel(HttpLoggingInterceptor.Level.BODY)).build()

    //初始化Retrofit
    fun initRetrofit(
        baseUrl : String ,
    ) : RetrofitManager {
        mRetrofit = Retrofit.Builder().baseUrl(baseUrl).client(mOkClient)
                .addConverterFactory(GsonConverterFactory.create()).build()
        return this
    }

    //初始化Retrofit，并添加拦截器
    fun initRetrofit(
        baseUrl : String ,
        interceptors : MutableList<Interceptor>? ,
    ) : RetrofitManager {
        if (interceptors != null && interceptors.isNotEmpty()) {
            val build = getOkHttpClientBuilder()
            for (interceptor in interceptors) {
                build.addInterceptor(interceptor)
            }
            mOkClient = build.build()
        }
        mRetrofit = Retrofit.Builder().baseUrl(baseUrl).client(mOkClient)
                .addConverterFactory(GsonConverterFactory.create()).build()
        return this
    }

    private fun getOkHttpClientBuilder() : OkHttpClient.Builder {
        return OkHttpClient.Builder().callTimeout(30 , TimeUnit.SECONDS)
                .connectTimeout(30 , TimeUnit.SECONDS).readTimeout(30 , TimeUnit.SECONDS)
                .writeTimeout(30 , TimeUnit.SECONDS).retryOnConnectionFailure(true)
    }

    fun <T> getService(serviceClass : Class<T>) : T {
        if (mRetrofit == null) {
            throw UninitializedPropertyAccessException("Retrofit必须初始化")
        } else {
            return mRetrofit!!.create(serviceClass)
        }
    }


}