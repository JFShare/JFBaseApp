package com.cxkj.common.base

import com.cxkj.common.net.DataState

interface IHandleNetResult {
    fun onSuccess()
    fun onError(dataState: DataState? )
}