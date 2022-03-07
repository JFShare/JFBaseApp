package com.cxkj.common.net

class BaseResp <T>{
    var code = -1
    var msg: String? = null
    var data: T? = null
    var dataState: DataState? = null
    var error: Throwable? = null
}
