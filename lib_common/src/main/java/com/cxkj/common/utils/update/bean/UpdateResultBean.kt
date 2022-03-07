package com.cxkj.common.utils.update.bean


/**
 * @description
 *
 *
 * @author Created by linyincongxingkeji on  2022/3/7 9:09
 */

data class UpdateResultBean(
    val appInfo : UpdateAppInfoBean ,
    val updateType : String ,
    val change_info : MutableList<String> ,
)
