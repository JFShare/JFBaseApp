package com.cxkj.common.base


import android.app.Dialog
import com.cxkj.common.net.BaseResp
import com.cxkj.common.net.DataState


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.cxkj.common.dialog.DialogUtil
import com.cxkj.common.utils.ToastUtils
import com.cxkj.common.widget.loadsir.ErrorCallback
import com.kingja.loadsir.core.LoadService
import me.yokeyword.fragmentation.SupportFragment


abstract class BaseMyFragment<T : ViewDataBinding , VM : BaseViewModel> : SupportFragment() {

    lateinit var mBinding : T
    lateinit var mViewModel : VM
    private lateinit var mLoadingDialog : Dialog
    lateinit var mLoadService : LoadService<Any>

    override fun onCreateView(
        inflater : LayoutInflater ,
        container : ViewGroup? ,
        savedInstanceState : Bundle? ,
    ) : View? {
        mBinding = DataBindingUtil.inflate(inflater , getLayoutId() , container , false)
        return mBinding.root
    }

    override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        mBinding = DataBindingUtil.setContentView(_mActivity , getLayoutId())
        mViewModel = initViewModel()
        beforeRegisterData()
        registerData()
        initData()
    }

    abstract fun getLayoutId() : Int
    abstract fun initViewModel() : VM
    abstract fun registerData()
    abstract fun initData()

    override fun onSupportVisible() {
        super.onSupportVisible()
        setImmersionBar()
    }

    open fun beforeRegisterData() {

    }


    protected open fun setImmersionBar() {

    }


    override fun onDestroy() {
        super.onDestroy()
        beforeDestroyView()
        disLoading()
        mBinding.unbind()

    }

    open fun beforeDestroyView() {

    }

    open fun onTokenOverdue() {

    }

    fun showLoading() {
        DialogUtil.showDialog(mLoadingDialog)
    }

    fun disLoading() {
        DialogUtil.closeDialog(mLoadingDialog)
    }

    //????????????????????????????????????null??????????????????
    fun <R> handleDataEmptySuccess(
        baseResp : BaseResp<R> , errorToast : String ,
        listener : IHandleNetResult ,
    ) {
        handleData(baseResp , errorToast , listener , isEmptySuccess = true , isTips = true)
    }

    //??????????????????????????????toast??????
    fun <R> handleDataSilent(
        baseResp : BaseResp<R> , errorToast : String ,
        listener : IHandleNetResult ,
    ) {
        handleData(baseResp , errorToast , listener , isEmptySuccess = false , isTips = false)
    }

    //??????????????????
    fun <R> handleData(
        baseResp : BaseResp<R> , errorToast : String ,
        listener : IHandleNetResult , isEmptySuccess : Boolean , isTips : Boolean ,
    ) {
        disLoading()
        when (val resultState = baseResp.dataState) {
            //????????????
            DataState.SUCCESS -> {
                listener.onSuccess()
            }
            //???????????????
            DataState.SERVER_ERROR -> {
                if (isTips) {
                    ToastUtils.instance.toast(baseResp.msg , errorToast)
                }
                listener.onError(resultState)
            }
            //?????????
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
            //token??????
            DataState.SERVER_TOKEN_OVERDUE , DataState.HTTPERROR_NOPERMISSION -> {
                onTokenOverdue()
                listener.onError(resultState)
            }
            //????????????
            DataState.HTTPERROR_NOTFOUND , DataState.HTTPERROR_SERVERERROR ,
            DataState.HTTPERROR_OTHER ,
            -> {
                if (isTips) {
                    when (resultState) {
                        DataState.HTTPERROR_NOTFOUND -> {
                            ToastUtils.instance.toast("404???????????????")
                        }
                        DataState.HTTPERROR_SERVERERROR -> {
                            ToastUtils.instance.toast("500???????????????")
                        }
                        else -> {
                            ToastUtils.instance.toast(errorToast)
                        }
                    }
                }
                listener.onError(resultState)
            }
            //json????????????
            DataState.JSONPARSE_ERROR -> {
                if (isTips) {
                    ToastUtils.instance.toast("???????????????")
                }
                listener.onError(resultState)
            }
            //????????????
            else -> {
                if (isTips) {
                    ToastUtils.instance.toast(errorToast)
                }
                listener.onError(resultState)
            }
        }
    }

    //?????????????????? ?????????LoadSir
    fun <R> handleDataLoadSir(
        baseResp : BaseResp<R> , errorToast : String ,
        listener : IHandleNetResult ,
    ) {
        when (baseResp.dataState) {
            //????????????
            DataState.SUCCESS -> {
                mLoadService.showSuccess()
                listener.onSuccess()
            }
            //token??????
            DataState.SERVER_TOKEN_OVERDUE -> {
                mLoadService.showCallback(ErrorCallback::class.java)
                onTokenOverdue()
                listener.onError(baseResp.dataState)
            }
            //????????????
            else -> {
                mLoadService.showCallback(ErrorCallback::class.java)
                ToastUtils.instance.toast(errorToast)
                listener.onError(baseResp.dataState)
            }
        }
    }
}