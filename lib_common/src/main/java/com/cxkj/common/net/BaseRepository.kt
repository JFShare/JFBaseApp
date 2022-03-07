package com.cxkj.common.net

import android.net.ParseException
import android.util.Log
import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseRepository {

    companion object {
        const val HTTP_ERRORCODE_NOPERMISSION = 401 //网络错误401，用户没有访问权限，需要进行身份认证
        const val HTTP_ERRORCODE_NOTFOUND = 404 //网络错误404，访问资源不存在
        const val HTTP_ERRORCODE_SERVERERROR = 500 //网络错误500，服务器错误
    }

    suspend fun <T : Any> executeResp(
        block : suspend () -> BaseResp<T> ,
        stateLiveData : StateLiveData<T> ,
    ) {
        var baseResp = BaseResp<T>()
        try {
            baseResp.dataState = DataState.LOADING
            //开始请求数据
            val invoke = block.invoke()
            //将结果复制给baseResp
            baseResp = invoke
            if (baseResp.code == RetrofitManager.instance.NETCODE_SUCCESS) {
                if (baseResp.data == null) {
                    baseResp.dataState = DataState.EMPTY
                } else {
                    baseResp.dataState = DataState.SUCCESS
                }
            } else if (baseResp.code == RetrofitManager.instance.NETCODE_TOKEN_OVERDUE) {
                //token过期
                baseResp.dataState = DataState.SERVER_TOKEN_OVERDUE
            } else {
                //服务器请求错误
                baseResp.dataState = DataState.SERVER_ERROR
            }
        } catch (e : Exception) {
            //非后台返回错误，捕获到的异常
            baseResp.dataState = DataState.OTHER_ERROR
            //HTTP错误
            if (e is HttpException) {
                when {
                    HTTP_ERRORCODE_NOPERMISSION == e.code() -> {
                        baseResp.dataState = DataState.HTTPERROR_NOPERMISSION
                    }
                    HTTP_ERRORCODE_NOTFOUND == e.code() -> {
                        baseResp.dataState = DataState.HTTPERROR_NOTFOUND
                    }
                    HTTP_ERRORCODE_SERVERERROR == e.code() -> {
                        baseResp.dataState = DataState.HTTPERROR_SERVERERROR
                    }
                    else -> {
                        baseResp.dataState = DataState.HTTPERROR_OTHER
                    }
                }
            }
            //Json错误
            else if (e is JsonParseException || e is JSONException || e is ParseException) {
                baseResp.dataState = DataState.JSONPARSE_ERROR
            }
            //其他网络错误
            else if (e is ConnectException || e is UnknownHostException || e is SocketTimeoutException) {
                baseResp.dataState = DataState.HTTPERROR_OTHER
            }

            baseResp.error = e

            e.message?.let {
                Log.e("网络请求错误" , e.message.toString())
            }

        } finally {
            stateLiveData.postValue(baseResp)
        }
    }


}