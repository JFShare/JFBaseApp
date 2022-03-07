package com.cxkj.common.utils.picture

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import com.cxkj.common.R
import com.cxkj.common.utils.AppHelper
import com.luck.picture.lib.animators.AnimationType
import com.luck.picture.lib.basic.PictureSelectionCameraModel
import com.luck.picture.lib.basic.PictureSelectionModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.style.*
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.PictureFileUtils
import java.io.File


object PictureSelectUtils {
    const val WHITE_STYLE = 0
    const val WEIXIN_STYLE = 1
    const val CUSTOM_STYLE = 2


    fun selectImages(
        activity : Activity , params : PictureSelectParams? = null ,
        resultCallback : ISelectImageResult ,
    ) {
        val psParams = params ?: PictureSelectParams()
        val selectorStyle = PictureSelectorStyle()
        //设置主题样式 ，如果不设置默认样式
        when (psParams.style) {
            WHITE_STYLE -> {
                //白色主题样式
                selectorStyle.titleBarStyle = getWhiteTitleBarStyle(activity)
                selectorStyle.bottomBarStyle = getWhiteBottomNavBarStyle()
                selectorStyle.selectMainStyle = getWhiteSelectMainStyle(activity)
            }
            WEIXIN_STYLE -> {
                //仿微信样式
                selectorStyle.titleBarStyle = getWeixinTitleBarStyle()
                selectorStyle.bottomBarStyle = getWeixinBottomNavBarStyle(activity)
                selectorStyle.selectMainStyle = getWeixinSelectMainStyle(activity)
            }
            CUSTOM_STYLE -> {
                //自定义样式
                selectorStyle.titleBarStyle = psParams.titleBarStyle
                selectorStyle.bottomBarStyle = psParams.bottomBarStyle
                selectorStyle.selectMainStyle = psParams.selectMainStyle
            }
        }
        //裁剪引擎、压缩引擎、沙河文件处理
        val cropEngine = ImageCropEngine(psParams.isCircleCrop , psParams.crop_ratio_x ,
            psParams.crop_ratio_y , selectorStyle)
        val compressEngine = ImageCompressEngine()
        val fileEngine = MeSandboxFileEngine()

        //页面启动动画
        val defaultAnimationStyle = PictureWindowAnimationStyle()
        defaultAnimationStyle.setActivityEnterAnimation(R.anim.ps_anim_enter)
        defaultAnimationStyle.setActivityExitAnimation(R.anim.ps_anim_exit)
        selectorStyle.windowAnimationStyle = defaultAnimationStyle
        //图片加载引擎
        val imageEngine = PictureSelectorGlideEngine.createGlideEngine()

        if (psParams.isOnlyOpenCamera) {
            // 单独拍照
            val cameraModel : PictureSelectionCameraModel = PictureSelector.create(activity)
                    .openCamera(SelectMimeType.ofImage()).setCameraInterceptListener(null)//自定义相机时间
                    .setCropEngine(if (psParams.isCrop) cropEngine else null) //是否裁剪
                    .setCompressEngine(if (psParams.isCompress) compressEngine else null) //是否压缩
                    .setSandboxFileEngine(fileEngine) //自定义沙盒文件处理
                    .isOriginalControl(false) //是否开启原图功能
                    .setOutputAudioDir(getSandboxCameraOutputPath())
            cameraModel.forResult(MeOnResultCallbackListener(resultCallback))
        } else {
            // 进入相册
            val selectionModel : PictureSelectionModel = PictureSelector.create(activity)
                    .openGallery(SelectMimeType.ofImage()) //选择图片
                    .setSelectorUIStyle(selectorStyle)//主题样式
                    .setImageEngine(imageEngine) // 图片加载引擎
                    .setCropEngine(if (psParams.isCrop) cropEngine else null) //是否裁剪
                    .setCompressEngine(if (psParams.isCompress) compressEngine else null) //是否压缩
                    .setSandboxFileEngine(fileEngine)
                    .setCameraInterceptListener(null) //禁用自定义相机，使用系统相机
//                .setSelectLimitTipsListener(MeOnSelectLimitTipsListener()) 自定义拦截提示
                    .setEditMediaInterceptListener(null) //禁用图片编辑功能
//                .setInjectLayoutResourceListener(getInjectLayoutResource()) //是否注入自定义布局
                    .setSelectionMode(
                        if (psParams.maxNumber > 1) SelectModeConfig.MULTIPLE else SelectModeConfig.SINGLE) //单选模式或多选模式
//            自定义输出路径
                    .setOutputCameraDir(getSandboxCameraOutputPath()) //自定义相机路径
                    .setOutputAudioDir(getSandboxAudioOutputPath()) //自定义视频路径
                    .setQuerySandboxDir(getSandboxCameraOutputPath())
                    .isOnlyObtainSandboxDir(false) //是否只查询指定目录
                    .isDisplayTimeAxis(true) //是否显示资源时间轴
                    .isPageStrategy(true) //分页模式
                    .isOriginalControl(false) //是否开启原图功能
                    .isDisplayCamera(true) //是否显示拍照按钮
                    .isOpenClickSound(true) //是否开启点击声音
                    .setSkipCropMimeType(PictureMimeType.ofGIF()) // 禁止压缩裁剪的文件类型
                    .isFastSlidingSelect(true)  //滑动选择
                    .isWithSelectVideoImage(false) //是否图片视频同选
                    .isPreviewFullScreenMode(true) //全屏预览
                    .isPreviewZoomEffect(true) // 预览缩放效果
                    .isPreviewImage(true) //是否预览图片
                    .isPreviewVideo(true) //是否预览视频
                    .isPreviewAudio(true) //是否预览音频
                    .isMaxSelectEnabledMask(true) //是否显示蒙层(达到最大可选数量)
                    .isDirectReturnSingle(false) //单选模式直接返回
                    .setMaxSelectNum(psParams.maxNumber) //最多选择数量
                    .setRecyclerAnimationMode(AnimationType.DEFAULT_ANIMATION) //图片列表滑动动画
                    .isGif(false) //是否显示gif
//                .setSelectedData(mAdapter.getData())
            selectionModel.forResult(MeOnResultCallbackListener(resultCallback))
        }


    }


    //返回图片路径
    fun getPicturePath(media : LocalMedia) : String {

        if (media.isCut && !media.isCompressed) {
            // 裁剪过
            Log.e("裁剪地址" , media.cutPath)
            return media.cutPath
        } else if (media.isCut || media.isCompressed) {
            // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
            Log.e("压缩地址" , media.compressPath)
            Log.e("压缩后文件大小" , (File(media.compressPath).length() / 1024).toString() + "k")
            return media.compressPath
        } else {
            // 原图
            return if (PictureMimeType.isContent(
                    media.path) && !media.isCut && !media.isCompressed
            ) {
                val path = PictureFileUtils.getPath(AppHelper.instance.mApp , Uri.parse(media.path))
                Log.e("Uri转换地址" , path)
                path
            } else {
                Log.e("原图地址" , media.path)
                media.path
            }
        }
    }


    fun getSandboxPath(context : Context) : String {
        val externalFilesDir = context.getExternalFilesDir("")
        val customFile = File(externalFilesDir!!.absolutePath , "Sandbox")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator
    }

    /**
     * 创建相机自定义输出目录
     *
     * @return
     */
    private fun getSandboxCameraOutputPath() : String {

        val externalFilesDir : File? = AppHelper.instance.mApp.getExternalFilesDir("")
        val customFile = File(externalFilesDir?.absolutePath , "Sandbox")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator

    }

    /**
     * 创建音频自定义输出目录
     *
     * @return
     */
    private fun getSandboxAudioOutputPath() : String {
        val externalFilesDir : File? = AppHelper.instance.mApp.getExternalFilesDir("")
        val customFile = File(externalFilesDir?.absolutePath , "Sound")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator

    }

    fun getWhiteTitleBarStyle(context : Context) : TitleBarStyle {
        val whiteTitleBarStyle = TitleBarStyle()
        whiteTitleBarStyle.titleBackgroundColor = ContextCompat.getColor(context , R.color.white)
        whiteTitleBarStyle.titleDrawableRightResource = R.drawable.ic_orange_arrow_down
        whiteTitleBarStyle.titleLeftBackResource = R.drawable.ps_ic_black_back
        whiteTitleBarStyle.titleTextColor = ContextCompat.getColor(context , R.color.black)
        whiteTitleBarStyle.titleCancelTextColor = Color.parseColor("#53575e")
        whiteTitleBarStyle.isDisplayTitleBarLine = true
        return whiteTitleBarStyle
    }

    fun getWhiteBottomNavBarStyle() : BottomNavBarStyle {
        val whiteBottomNavBarStyle = BottomNavBarStyle()
        whiteBottomNavBarStyle.bottomNarBarBackgroundColor = Color.parseColor("#EEEEEE")
        whiteBottomNavBarStyle.bottomPreviewSelectTextColor = Color.parseColor("#53575e")
        whiteBottomNavBarStyle.bottomPreviewNormalTextColor = Color.parseColor("#9b9b9b")
        whiteBottomNavBarStyle.bottomPreviewSelectTextColor = Color.parseColor("#FA632D")
        whiteBottomNavBarStyle.isCompleteCountTips = false
        whiteBottomNavBarStyle.bottomEditorTextColor = Color.parseColor("#53575e")
        whiteBottomNavBarStyle.bottomOriginalTextColor = Color.parseColor("#53575e")
        return whiteBottomNavBarStyle
    }

    fun getWhiteSelectMainStyle(context : Context) : SelectMainStyle {
        val selectMainStyle = SelectMainStyle()
        selectMainStyle.statusBarColor = ContextCompat.getColor(context , R.color.white)
        selectMainStyle.navigationBarColor = ContextCompat.getColor(context , R.color.white)
        selectMainStyle.isDarkStatusBarBlack = true
        selectMainStyle.selectNormalTextColor = Color.parseColor("#9b9b9b")
        selectMainStyle.selectTextColor = Color.parseColor("#FA632D")
        selectMainStyle.previewSelectBackground = R.drawable.ps_demo_white_preview_selector
        selectMainStyle.selectBackground = R.drawable.ps_checkbox_selector
        selectMainStyle.selectText = context.getString(R.string.ps_done_front_num)
        selectMainStyle.mainListBackgroundColor = ContextCompat.getColor(context , R.color.white)
        return selectMainStyle
    }

    fun getWeixinTitleBarStyle() : TitleBarStyle {
        // 头部TitleBar 风格
        val numberTitleBarStyle = TitleBarStyle()
        numberTitleBarStyle.isHideCancelButton = true
        numberTitleBarStyle.isAlbumTitleRelativeLeft = true
        numberTitleBarStyle.titleAlbumBackgroundResource = R.drawable.ps_album_bg
        numberTitleBarStyle.titleDrawableRightResource = R.drawable.ps_ic_grey_arrow
        numberTitleBarStyle.previewTitleLeftBackResource = R.drawable.ps_ic_normal_back
        return numberTitleBarStyle
    }

    fun getWeixinBottomNavBarStyle(context : Context) : BottomNavBarStyle {
        // 底部NavBar 风格
        val numberBottomNavBarStyle = BottomNavBarStyle()
        numberBottomNavBarStyle.bottomPreviewNarBarBackgroundColor = Color.parseColor("#E6393a3e")
        numberBottomNavBarStyle.bottomPreviewNormalText = "预览"
        numberBottomNavBarStyle.bottomPreviewNormalTextColor = Color.parseColor("#9b9b9b")
        numberBottomNavBarStyle.bottomPreviewNormalTextSize = 16
        numberBottomNavBarStyle.isCompleteCountTips = false
        numberBottomNavBarStyle.bottomPreviewSelectText = context.getString(R.string.ps_preview_num)
        numberBottomNavBarStyle.bottomPreviewSelectTextColor = ContextCompat.getColor(context ,
            R.color.white)
        return numberBottomNavBarStyle
    }

    fun getWeixinSelectMainStyle(context : Context) : SelectMainStyle {

        // 主体风格
        val numberSelectMainStyle = SelectMainStyle()
        numberSelectMainStyle.isSelectNumberStyle = true
        numberSelectMainStyle.isPreviewSelectNumberStyle = false
        numberSelectMainStyle.isPreviewDisplaySelectGallery = true
        numberSelectMainStyle.selectBackground = R.drawable.ps_default_num_selector
        numberSelectMainStyle.previewSelectBackground = R.drawable.ps_preview_checkbox_selector
        numberSelectMainStyle.selectNormalBackgroundResources = R.drawable.ps_select_complete_normal_bg
        numberSelectMainStyle.selectNormalTextColor = Color.parseColor("#53575e")
        numberSelectMainStyle.selectNormalText = "完成"
        numberSelectMainStyle.adapterPreviewGalleryBackgroundResource = R.drawable.ps_preview_gallery_bg
        numberSelectMainStyle.adapterPreviewGalleryItemSize = DensityUtil.dip2px(context , 52f)
        numberSelectMainStyle.previewSelectText = "选择"
        numberSelectMainStyle.previewSelectTextSize = 14
        numberSelectMainStyle.previewSelectTextColor = ContextCompat.getColor(context ,
            R.color.white)
        numberSelectMainStyle.previewSelectMarginRight = DensityUtil.dip2px(context , 6f)
        numberSelectMainStyle.selectBackgroundResources = R.drawable.ps_select_complete_bg
        numberSelectMainStyle.selectText = context.getString(R.string.ps_send_num)
        numberSelectMainStyle.selectTextColor = ContextCompat.getColor(context , R.color.white)
        numberSelectMainStyle.mainListBackgroundColor = ContextCompat.getColor(context ,
            R.color.black)
        numberSelectMainStyle.isCompleteSelectRelativeTop = true
        numberSelectMainStyle.isPreviewSelectRelativeBottom = true
        numberSelectMainStyle.isAdapterItemIncludeEdge = false
        return numberSelectMainStyle
    }

}