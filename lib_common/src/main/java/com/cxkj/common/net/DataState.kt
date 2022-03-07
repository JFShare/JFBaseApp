package com.cxkj.common.net


enum class DataState {
    LOADING , //加载中
    SUCCESS , //请求成功
    EMPTY ,  //返回空数据
    SERVER_ERROR ,  // 后台定义错误、非成功code
    SERVER_TOKEN_OVERDUE , // token过期
    HTTPERROR_NOPERMISSION , //网络错误401，用户没有访问权限，需要进行身份认证
    HTTPERROR_NOTFOUND , //网络错误404，访问资源不存在
    HTTPERROR_SERVERERROR , //网络错误500，服务器错误
    HTTPERROR_OTHER , //其他网络错误
    JSONPARSE_ERROR, //Json解析错误
    OTHER_ERROR,//其他错误
}