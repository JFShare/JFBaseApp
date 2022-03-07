package com.cxkj.jfbaseapp

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.cxkj.common.base.BaseMyActivity
import com.cxkj.common.base.BaseViewModel
import com.cxkj.common.utils.picture.ISelectImageResult
import com.cxkj.common.utils.picture.PictureSelectParams
import com.cxkj.common.utils.picture.PictureSelectUtils
import com.cxkj.common.utils.singleClick
import com.cxkj.common.utils.update.UpdateUtils
import com.cxkj.common.widget.imagerv.ImageModelBean
import com.cxkj.common.widget.imagerv.ImageRecyclerView
import com.cxkj.common.widget.imagerv.ImageRecyclerViewParams
import com.cxkj.jfbaseapp.databinding.ActivityMainBinding
import com.luck.picture.lib.entity.LocalMedia
import java.util.ArrayList

class MainActivity : BaseMyActivity<ActivityMainBinding , BaseViewModel>() {

    override fun getLayoutId() : Int {
        return R.layout.activity_main
    }

    override fun initViewModel() : BaseViewModel {
        return ViewModelProvider.NewInstanceFactory().create(BaseViewModel::class.java)
    }

    override fun registerData() {

    }

    override fun initData(savedInstanceState : Bundle?) {
        mBinding.run {
            imageRecycler.init(ImageRecyclerViewParams().setImageCorner(10F))
            btnAlbum.singleClick {
                PictureSelectUtils.selectImages(_mActivity , null , object : ISelectImageResult {
                    override fun onResult(result : ArrayList<LocalMedia>) {
                        val list = mutableListOf<ImageModelBean>()
                        for (image in result) {
                            list.add(ImageModelBean(ImageRecyclerView.IMAGE_ITEM ,
                                PictureSelectUtils.getPicturePath(image) , null))
                        }
                        imageRecycler.setDataList(list)
                    }

                })
            }
            btnCamera.singleClick {
                PictureSelectUtils.selectImages(_mActivity ,
                    PictureSelectParams().setIsOnlyOpenCamera(true) , object : ISelectImageResult {
                        override fun onResult(result : ArrayList<LocalMedia>) {
                            val list = mutableListOf<ImageModelBean>()
                            for (image in result) {
                                list.add(ImageModelBean(ImageRecyclerView.IMAGE_ITEM ,
                                    PictureSelectUtils.getPicturePath(image) , null))
                            }
                            imageRecycler.setDataList(list)
                        }

                    })
            }

            btnUpdate.singleClick {
                val map = hashMapOf<String , String>()
                map["appId"] = "LL3OPFO4-WWC5-377X-NI35-0QPM1BSQAD6Q"
                map["versionNo"] = "1"
                UpdateUtils.instance.checkUpdate(this@MainActivity ,
                    "" , map , "xiazai.apk")
            }
        }

    }
}