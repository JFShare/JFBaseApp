package com.cxkj.common.net

import com.cxkj.common.utils.MMKVUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class CommonHeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain : Interceptor.Chain) : Response {
        val request : Request = chain.request()
        val builder : Request.Builder = request.newBuilder()
//        builder.addHeader("token" , MMKVUtils.instance.decodeString("token" , "")!!)
//        builder.addHeader("userId",MMKVUtils.instance.decodeString("userId","")!!)
        return chain.proceed(builder.build())
    }
}