package com.cxkj.common.widget.imagerv

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

import com.cxkj.common.R
import com.cxkj.common.utils.DensityUtils
import com.cxkj.common.utils.GlideUtils
import com.cxkj.common.utils.picture.ISelectImageResult
import com.cxkj.common.utils.picture.PictureSelectParams
import com.cxkj.common.utils.picture.PictureSelectUtils
import com.cxkj.common.widget.rv.decoration.GridDecoration

import com.luck.picture.lib.entity.LocalMedia
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.SmartGlideImageLoader
import java.util.ArrayList


class ImageRecyclerView(context : Context? , attrs : AttributeSet?) : LinearLayout(context ,
    attrs) {

    companion object {
        const val ADD_ITEM = 0 //新增列表项
        const val IMAGE_ITEM = 1 //图片列表项
    }

    var recyclerView : RecyclerView
    var mAdapter : ImageModelAdapter? = null
    private var mDecoration : GridDecoration? = null

    private var maxNumber = 9 //最大图片数量
    private var rowNumber = 3 //每行图片个数
    private var spaceDp = 15F //图片间距
    private var recyclerViewMarginDp = 15F
    private var isAddModel = true //是否是新增模式：显示新增图片、删除按钮   否的话只能预览图片
    private var dataList : MutableList<ImageModelBean> = mutableListOf() // 图片数据列表

    private var placeholderImage : Int = -1 // 占位图
    private var deleteImage : Int = -1 // 删除图标
    private var imageCornerDp : Float = 0F //图片圆角

    private var pictureSelectParams : PictureSelectParams = PictureSelectParams() //PictureSelector 设置


    init {
        inflate(context , R.layout.widget_image_recyclerview , this)
        recyclerView = findViewById(R.id.recyclerView)
    }

    fun init(params : ImageRecyclerViewParams? = null) {
        resetParams()
        if (params != null) {
            this.maxNumber = params.maxNumber
            this.rowNumber = params.rowNumber
            this.spaceDp = params.spaceDp
            this.recyclerViewMarginDp = params.recyclerViewMarginDp
            this.isAddModel = params.isAddModel
            this.placeholderImage = params.placeholderImage
            this.deleteImage = params.deleteImage
            this.imageCornerDp = params.imageCornerDp
            this.dataList = params.dataList
            this.pictureSelectParams = params.pictureSelectParams
        }
        initRecyclerView()
    }

    //初始化RecyclerView
    private fun initRecyclerView() {
        recyclerView.run {
            layoutManager = GridLayoutManager(context , rowNumber)
            if (itemDecorationCount == 0) {
                setDecoration()
                setMargin()
            } else if (itemDecorationCount == 1 && mDecoration != null && getItemDecorationAt(
                    0) == mDecoration
            ) {
                removeItemDecoration(mDecoration!!)
                setDecoration()
                setMargin()
            }
            completeAddItem()
            mAdapter = ImageModelAdapter(isAddModel , placeholderImage , deleteImage ,
                imageCornerDp , dataList).apply {
                setOnItemClickListener { _ , view , position ->
                    if (ADD_ITEM == dataList[position].modelType) {
                        var realMaxNumber = maxNumber - dataList.size
                        if (hasAddItem()) {
                            realMaxNumber++
                        }
                        val activity = context2Activity(this@ImageRecyclerView)
                        if (activity != null) {
                            PictureSelectUtils.selectImages(activity ,
                                pictureSelectParams.setMaxNumber(realMaxNumber) ,
                                object : ISelectImageResult {
                                    override fun onResult(result : ArrayList<LocalMedia>) {
                                        if (result.size > 0) {
                                            removeAddItem()
                                            for (newImage in result) {
                                                dataList.add(ImageModelBean(IMAGE_ITEM ,
                                                    PictureSelectUtils.getPicturePath(newImage) ,
                                                    null))
                                            }
                                            completeAddItem()
                                            mAdapter?.notifyDataSetChanged()
                                        }
                                    }

                                })
                        }

                    } else {
                        val list = mutableListOf<Any?>()
                        for (image in dataList) {
                            if (ADD_ITEM != image.modelType) {
                                if (!TextUtils.isEmpty(image.netPath)) {
                                    list.add(image.netPath)
                                } else {
                                    list.add(image.localPath)
                                }
                            }
                        }

                        XPopup.Builder(context)
                                .asImageViewer(view.findViewById(R.id.ivImage) , position , list ,
                                    { popupView , _ ->
                                        popupView.updateSrcView(
                                            view.findViewById<View>(R.id.ivImage) as ImageView)
                                    } , SmartGlideImageLoader()).show()

                    }
                }
                setOnItemChildClickListener { _ , view , position ->
                    if (view.id == R.id.llDelete) {
                        dataList.removeAt(position)
                        completeAddItem()
                        mAdapter?.notifyDataSetChanged()
                    }
                }
            }
            adapter = mAdapter
        }
    }

    private fun resetParams() {
        maxNumber = 9
        rowNumber = 3
        spaceDp = 15F
        recyclerViewMarginDp = 15F
        isAddModel = true
        placeholderImage = -1
        deleteImage = -1
        imageCornerDp = 0F
        dataList = mutableListOf()
        pictureSelectParams = PictureSelectParams()
    }

    private fun completeAddItem() {
        if (isAddModel && !hasAddItem() && dataList.size < maxNumber) {
            dataList.add(ImageModelBean(ADD_ITEM))
        }
    }

    private fun removeAddItem() {
        var addItem : ImageModelBean? = null
        for (image in dataList) {
            if (ADD_ITEM == image.modelType) {
                addItem = image
                break
            }
        }
        if (addItem != null) {
            dataList.remove(addItem)
        }
    }

    private fun setDecoration() {
        mDecoration = GridDecoration(DensityUtils.dip2px(context , spaceDp))
        recyclerView.addItemDecoration(mDecoration!!)
    }

    private fun setMargin() {
        val lp = recyclerView.layoutParams as ConstraintLayout.LayoutParams
        lp.marginStart = DensityUtils.dip2px(context , recyclerViewMarginDp)
        lp.marginEnd = DensityUtils.dip2px(context , recyclerViewMarginDp)
        recyclerView.layoutParams = lp
    }

    //设置网络返回的数据
    fun setNetImages(list : List<String>) {
        dataList.clear()
        for (image in list) {
            dataList.add(ImageModelBean(IMAGE_ITEM , null , image))
        }
        completeAddItem()
        mAdapter?.notifyDataSetChanged()
    }

    //设置自定义的数据
    fun setDataList(list : MutableList<ImageModelBean>) {
        dataList.clear()
        dataList.addAll(list)
        completeAddItem()
        mAdapter?.notifyDataSetChanged()
    }

    private fun hasAddItem() : Boolean {
        var hasAddItem = false
        for (image in dataList) {
            if (image.modelType == ADD_ITEM) {
                hasAddItem = true
                break
            }
        }
        return hasAddItem
    }


    class ImageModelAdapter(
        private var addModel : Boolean , private var placeholderImage : Int ,
        private var deleteImage : Int , private var imageCornerDp : Float ,
        var dataList : MutableList<ImageModelBean> ,
    ) : BaseQuickAdapter<ImageModelBean , BaseViewHolder>(R.layout.item_widget_image_recyclerview ,
        dataList) {
        init {
            addChildClickViewIds(R.id.llDelete)
        }

        override fun convert(holder : BaseViewHolder , item : ImageModelBean) {
            if (ADD_ITEM == item.modelType) {
                Glide.with(context).load(
                    if (placeholderImage != -1) placeholderImage else R.drawable.add_image)
                        .into(holder.getView(R.id.ivImage))
            } else {
                GlideUtils.loadRoundCorners(context ,
                    if (!TextUtils.isEmpty(item.netPath)) item.netPath else item.localPath ,
                    imageCornerDp , holder.getView(R.id.ivImage))
            }
            holder.setVisible(R.id.llDelete , addModel && ADD_ITEM != item.modelType)
                    .setImageResource(R.id.ivDelete ,
                        if (deleteImage != -1) deleteImage else R.drawable.red_delete)

        }
    }


    private fun context2Activity(view : View) : Activity? {
        var context = view.context
        while (context is ContextWrapper) {
            context = if (context is Activity) {
                return context
            } else {
                context.baseContext
            }
        }
        return null
    }


}