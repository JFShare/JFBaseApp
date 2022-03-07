package com.cxkj.common.widget.countdowntimer;


public interface OnCountDownTimerListener {
    /**
     * 间隔的回调

     */
    void onTick(long millisUntilFinished);

    /**
     * 倒计时完成后的回调

     */
    void onFinish();

    /**
     * 手动停止计时器的回调
     */
    void onCancel();
}
