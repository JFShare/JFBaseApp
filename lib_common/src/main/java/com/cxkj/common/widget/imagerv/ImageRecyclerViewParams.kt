package com.cxkj.common.widget.imagerv

import com.cxkj.common.utils.picture.PictureSelectParams


class ImageRecyclerViewParams {
    var maxNumber : Int = 9
        private set
    var rowNumber : Int = 3
        private set
    var spaceDp : Float = 15F
        private set
    var recyclerViewMarginDp : Float = 15F
        private set
    var isAddModel : Boolean = true
        private set
    var placeholderImage : Int = -1
        private set
    var deleteImage : Int = -1
        private set
    var imageCornerDp : Float = 0F
        private set
    var dataList : MutableList<ImageModelBean> = mutableListOf()
        private set
    var pictureSelectParams : PictureSelectParams = PictureSelectParams()
        private set

    fun setMaxNumber(maxNumber : Int) =
            apply {
                this.maxNumber = maxNumber
            }

    fun setRowNumber(rowNumber : Int) =
            apply {
                this.rowNumber = rowNumber
            }

    fun setSpaceDp(spaceDp : Float) =
            apply {
                this.spaceDp = spaceDp
            }

    fun setRecyclerViewMargin(recyclerViewMargin : Float) =
            apply {
                this.recyclerViewMarginDp = recyclerViewMarginDp
            }

    fun setIsAddModel(isAddModel : Boolean) =
            apply {
                this.isAddModel = isAddModel
            }

    fun setPlaceholderImage(placeholderImage : Int) =
            apply {
                this.placeholderImage = placeholderImage
            }

    fun setDeleteImage(deleteImage : Int) =
            apply {
                this.deleteImage = deleteImage
            }

    fun setImageCorner(imageCornerDp : Float) =
            apply {
                this.imageCornerDp = imageCornerDp
            }

    fun setDataList(dataList : MutableList<ImageModelBean>) =
            apply {
                this.dataList = dataList
            }

    fun setPictureSelectParams(pictureSelectParams : PictureSelectParams) =
            apply {
                this.pictureSelectParams = pictureSelectParams
            }

}