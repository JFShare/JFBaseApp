package com.cxkj.common.utils.picture

import com.luck.picture.lib.style.BottomNavBarStyle
import com.luck.picture.lib.style.SelectMainStyle
import com.luck.picture.lib.style.TitleBarStyle


class PictureSelectParams {
     var maxNumber : Int = 9
        private set
    var isOnlyOpenCamera : Boolean = false
        private set
    var style : Int = PictureSelectUtils.WHITE_STYLE
        private set
    var titleBarStyle : TitleBarStyle? = null
        private set
    var bottomBarStyle : BottomNavBarStyle? = null
        private set
    var selectMainStyle : SelectMainStyle? = null
        private set
    var isCompress : Boolean = true
        private set
    var isCrop : Boolean = false
        private set
    var isCircleCrop : Boolean = false
        private set
    var crop_ratio_x : Int = -1
        private set
    var crop_ratio_y : Int = -1
        private set

    fun setMaxNumber(maxNumber : Int) =
            apply {
                this.maxNumber = maxNumber
            }

    fun setIsOnlyOpenCamera(isOnlyOpenCamera : Boolean) =
            apply {
                this.isOnlyOpenCamera = isOnlyOpenCamera
            }

    fun setStyle(style : Int) =
            apply {
                this.style = style
            }

    fun setTitleBarStyle(titleBarStyle : TitleBarStyle?) =
            apply {
                this.titleBarStyle = titleBarStyle
            }

    fun setBottomNavBarStyle(bottomBarStyle : BottomNavBarStyle?) =
            apply {
                this.bottomBarStyle = bottomBarStyle
            }

    fun setSelectMainStyle(selectMainStyle : SelectMainStyle?) =
            apply {
                this.selectMainStyle = selectMainStyle
            }

    fun setIsCompress(isCompress : Boolean) =
            apply {
                this.isCompress = isCompress
            }

    fun setIsCrop(isCrop : Boolean) =
            apply {
                this.isCrop = isCrop
            }

    fun setIsCircleCrop(isCircleCrop : Boolean) =
            apply {
                this.isCircleCrop = isCircleCrop
            }

    fun setCropRatioX(crop_ratio_x : Int) =
            apply {
                this.crop_ratio_x = crop_ratio_x
            }

    fun setCropRatioY(crop_ratio_y : Int) =
            apply {
                this.crop_ratio_y = crop_ratio_y
            }
}