package com.cxkj.common.utils.picture;

/**
 * @author Created by linyincongxingkeji on  2022/3/4 11:46
 * @description
 */

import android.content.Context;

import com.cxkj.common.R;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectLimitType;
import com.luck.picture.lib.interfaces.OnSelectLimitTipsListener;
import com.luck.picture.lib.utils.ToastUtils;

/**
 * 拦截自定义提示
 */
class MeOnSelectLimitTipsListener implements OnSelectLimitTipsListener {

    @Override
    public boolean onSelectLimitTips(Context context, PictureSelectionConfig config, int limitType) {
//        if (limitType == SelectLimitType.SELECT_MAX_VIDEO_SELECT_LIMIT) {
//            ToastUtils.showToast(context, context.getString(R.string.ps_message_video_max_num, String.valueOf(config.maxVideoSelectNum)));
//            return true;
//        }
        return false;
    }
}