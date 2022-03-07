package com.cxkj.common.utils.picture;

/**
 * @author Created by linyincongxingkeji on  2022/3/4 11:51
 * @description
 */

import static com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread;

import android.util.Log;

import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.utils.MediaUtils;

import java.util.ArrayList;

/**
 * 选择结果
 */
class MeOnResultCallbackListener implements OnResultCallbackListener<LocalMedia> {

    private ISelectImageResult callBack;

    MeOnResultCallbackListener(ISelectImageResult callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onResult(ArrayList<LocalMedia> result) {
        Log.e("PictureSelector选择结果", "----" + result.size());
        analyticalSelectResults(result);
    }

    @Override
    public void onCancel() {
    }

    /**
     * 处理选择结果
     *
     * @param result
     */
    private void analyticalSelectResults(ArrayList<LocalMedia> result) {
        for (LocalMedia media : result) {
            if (media.getWidth() == 0 || media.getHeight() == 0) {
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(media.getPath());
                    media.setWidth(imageExtraInfo.getWidth());
                    media.setHeight(imageExtraInfo.getHeight());
                } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                    MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(PictureAppMaster.getInstance().getAppContext(), media.getPath());
                    media.setWidth(videoExtraInfo.getWidth());
                    media.setHeight(videoExtraInfo.getHeight());
                }
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onResult(result);
                }
            }
        });
    }
}