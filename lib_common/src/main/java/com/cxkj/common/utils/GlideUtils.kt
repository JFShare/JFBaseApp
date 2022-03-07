package com.cxkj.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.cxkj.common.R
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropCircleTransformation
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import jp.wasabeef.glide.transformations.internal.Utils


object GlideUtils {

    //加载圆角图片

    fun loadRoundCorners(
        context : Context , url : String? , radiusDp : Float ,
        imageView : ImageView ,
    ) {
        val options = RequestOptions()
        options.transform(MultiTransformation(CenterCrop() ,
            RoundedCorners(DensityUtils.dip2px(context , radiusDp))))
        Glide.with(context).load(url).centerCrop().apply(options).into(imageView)
    }

    fun loadRoundCorners(
        context : Context , resId : Int , radiusDp : Float ,
        imageView : ImageView ,
    ) {
        val options = RequestOptions()
        options.transform(MultiTransformation(CenterCrop() ,
            RoundedCorners(DensityUtils.dip2px(context , radiusDp))))
        Glide.with(context).load(resId).apply(options).into(imageView)
    }

    fun loadRoundCorners(
        context : Context , url : String? , default : Int , radiusDp : Float ,
        imageView : ImageView ,
    ) {
        val options = RequestOptions()
        options.transform(MultiTransformation(CenterCrop() ,
            RoundedCorners(DensityUtils.dip2px(context , radiusDp))))
        Glide.with(context).load(url).error(default).apply(options).into(imageView)
    }

    //圆形图片
    fun loadCircle(context : Context , url : String? , imageView : ImageView) {
        val options = RequestOptions()
        options.transform(MultiTransformation(CenterCrop() , CircleCrop()))
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    fun loadCircle(context : Context , resId : Int , imageView : ImageView) {
        val options = RequestOptions()
        options.transform(MultiTransformation(CenterCrop() , CircleCrop()))
        Glide.with(context).load(resId).apply(options).into(imageView)
    }

    fun loadCircle(context : Context , url : String? , default : Int , imageView : ImageView) {
        val options = RequestOptions()
        options.transform(MultiTransformation(CenterCrop() , CircleCrop()))
        Glide.with(context).load(url).error(default).apply(options).into(imageView)
    }

    //圆形带边框
    fun loadCircleWithBorder(
        context : Context , url : String? ,
        orderWidth : Float , color : Int ,
        imageView : ImageView ,
    ) {
        val options = RequestOptions()
        options.transform(MultiTransformation(CenterCrop() ,
            CropCircleWithBorderTransformation(DensityUtils.dip2px(context , orderWidth) , color)))
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    fun loadCircleWithBorder(
        context : Context , resId : Int ,
        orderWidth : Float , color : Int ,
        imageView : ImageView ,
    ) {
        val options = RequestOptions()
        options.transform(MultiTransformation(CenterCrop() ,
            CropCircleWithBorderTransformation(DensityUtils.dip2px(context , orderWidth) , color)))
        Glide.with(context).load(resId).apply(options).into(imageView)
    }

    fun loadCircleWithBorder(
        context : Context , url : String? , default : Int ,
        orderWidth : Float , color : Int ,
        imageView : ImageView ,
    ) {
        val options = RequestOptions()
        options.transform(MultiTransformation(CenterCrop() ,
            CropCircleWithBorderTransformation(DensityUtils.dip2px(context , orderWidth) , color)))
        Glide.with(context).load(url).error(default).apply(options).into(imageView)
    }

    //加载模糊图片
    fun loadBlur(context : Context , url : String? , radius : Int , imageView : ImageView) {
        Glide.with(context).load(url).apply(bitmapTransform(BlurTransformation(radius)))
                .into(imageView)
    }

    fun loadBlur(context : Context , resId : Int , radius : Int , imageView : ImageView) {
        Glide.with(context).load(resId).apply(bitmapTransform(BlurTransformation(radius)))
                .into(imageView)
    }


}