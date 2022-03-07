package com.cxkj.common.utils.picture

import com.luck.picture.lib.entity.LocalMedia
import java.util.ArrayList


interface ISelectImageResult {
    fun onResult(result : ArrayList<LocalMedia>)
}