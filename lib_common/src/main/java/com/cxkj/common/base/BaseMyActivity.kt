package com.cxkj.common.base

import android.app.Dialog
import android.hardware.Camera
import android.os.Bundle
import android.service.autofill.Dataset
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.cxkj.common.dialog.DialogUtil
import com.cxkj.common.net.BaseResp
import com.cxkj.common.net.DataState
import com.cxkj.common.utils.ToastUtils
import com.cxkj.common.widget.loadsir.ErrorCallback
import com.kingja.loadsir.core.LoadService
import me.yokeyword.fragmentation.SupportActivity

abstract class BaseMyActivity<VD : ViewDataBinding , VM : BaseViewModel> : SupportActivity() {

    lateinit var mBinding : VD
    lateinit var mViewModel : VM

    lateinit var _mActivity : AppCompatActivity

    private lateinit var mLoadingDialog : Dialog
    lateinit var mLoadService : LoadService<Any>


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        _mActivity = this
        mBinding = DataBindingUtil.setContentView(this , getLayoutId())
        mBinding.lifecycleOwner = this
        mLoadingDialog = DialogUtil.createLoadingDialog(this)
        setImmersionBar()
        mViewModel = initViewModel()
        beforeRegisterData()
        registerData()
        initData(savedInstanceState)
    }

    abstract fun getLayoutId() : Int
    abstract fun initViewModel() : VM
    abstract fun registerData()
    abstract fun initData(savedInstanceState : Bundle?)


    open fun beforeRegisterData() {

    }

    open fun setImmersionBar() {

    }

    open fun onTokenOverdue() {

    }

    override fun onDestroy() {
        super.onDestroy()
        beforeDestroyView()
        disLoading()
        mBinding.unbind()
    }

    open fun beforeDestroyView() {

    }

    fun showLoading() {
        DialogUtil.showDialog(mLoadingDialog)
    }

    fun disLoading() {
        DialogUtil.closeDialog(mLoadingDialog)
    }

    //处理网络数据，返回数据为null，认为是成功
    fun <R> handleDataEmptySuccess(
        baseResp : BaseResp<R> , errorToast : String ,
        listener : IHandleNetResult ,
    ) {
        handleData(baseResp , errorToast , listener , isEmptySuccess = true , isTips = true)
    }

    //处理网络数据，不提示toast信息
    fun <R> handleDataSilent(
        baseResp : BaseResp<R> , errorToast : String ,
        listener : IHandleNetResult ,
    ) {
        handleData(baseResp , errorToast , listener , isEmptySuccess = false , isTips = false)
    }

    //处理网络数据
    fun <R> handleData(
        baseResp : BaseResp<R> , errorToast : String ,
        listener : IHandleNetResult , isEmptySuccess : Boolean , isTips : Boolean ,
    ) {
        disLoading()
        when (val resultState = baseResp.dataState) {
            //请求成功
            DataState.SUCCESS -> {
                listener.onSuccess()
            }
            //服务器错误
            DataState.SERVER_ERROR -> {
                if (isTips) {
                    ToastUtils.instance.toast(baseResp.msg , errorToast)
                }
                listener.onError(resultState)
            }
            //空数据
            DataState.EMPTY -> {
                if (isEmptySuccess) {
                    listener.onSuccess()
                } else {
                    if (isTips) {
                        ToastUtils.instance.toast(errorToast)
                    }
                    listener.onError(resultState)
                }

            }
            //token过期
            DataState.SERVER_TOKEN_OVERDUE , DataState.HTTPERROR_NOPERMISSION -> {
                onTokenOverdue()
                listener.onError(resultState)
            }
            //网络错误
            DataState.HTTPERROR_NOTFOUND , DataState.HTTPERROR_SERVERERROR ,
            DataState.HTTPERROR_OTHER ,
            -> {
                if (isTips) {
                    when (resultState) {
                        DataState.HTTPERROR_NOTFOUND -> {
                            ToastUtils.instance.toast("404网络错误！")
                        }
                        DataState.HTTPERROR_SERVERERROR -> {
                            ToastUtils.instance.toast("500网络错误！")
                        }
                        else -> {
                            ToastUtils.instance.toast(errorToast)
                        }
                    }
                }
                listener.onError(resultState)
            }
            //json解析错误
            DataState.JSONPARSE_ERROR -> {
                if (isTips) {
                    ToastUtils.instance.toast("数据错误！")
                }
                listener.onError(resultState)
            }
            //其他错误
            else -> {
                if (isTips) {
                    ToastUtils.instance.toast(errorToast)
                }
                listener.onError(resultState)
            }
        }
    }

    //处理网络数据 并处理LoadSir
    fun <R> handleDataLoadSir(
        baseResp : BaseResp<R> , errorToast : String ,
        listener : IHandleNetResult ,
    ) {
        when (baseResp.dataState) {
            //请求成功
            DataState.SUCCESS -> {
                mLoadService.showSuccess()
                listener.onSuccess()
            }
            //token过期
            DataState.SERVER_TOKEN_OVERDUE -> {
                mLoadService.showCallback(ErrorCallback::class.java)
                onTokenOverdue()
                listener.onError(baseResp.dataState)
            }
            //其他错误
            else -> {
                mLoadService.showCallback(ErrorCallback::class.java)
                ToastUtils.instance.toast(errorToast)
                listener.onError(baseResp.dataState)
            }
        }
    }


}