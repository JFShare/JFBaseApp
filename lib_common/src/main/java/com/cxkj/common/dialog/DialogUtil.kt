package com.cxkj.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.cxkj.common.R




class DialogUtil {
    companion object {
        fun createLoadingDialog(context : Context , msg : String = "加载中") : Dialog {
            val inflater = LayoutInflater.from(context)
            val loadingView = inflater.inflate(R.layout.dialog_laoding , null)
            val tvLoadingTips = loadingView.findViewById<TextView>(R.id.tvLoadingTips)
            tvLoadingTips.text = msg
            val loadingDialog = Dialog(context , R.style.MyDialogStyle)
            loadingDialog.setCancelable(true); // 是否可以按“返回键”消失
            loadingDialog.setCanceledOnTouchOutside(false); // 点击加载框以外的区域
            loadingDialog.setContentView(loadingView ,
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT ,
                    LinearLayout.LayoutParams.MATCH_PARENT))
            val window = loadingDialog.window;
            val lp = window!!.attributes;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setGravity(Gravity.CENTER);
            window.attributes = lp
            window.setWindowAnimations(R.style.PopWindowAnimStyle)
            return loadingDialog
        }

        fun showDialog(dialog : Dialog) {
            if (!dialog.isShowing) {
                dialog.show()
            }
        }

        fun closeDialog(dialog : Dialog) {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }
}

