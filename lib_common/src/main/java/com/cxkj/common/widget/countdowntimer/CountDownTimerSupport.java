package com.cxkj.common.widget.countdowntimer;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class CountDownTimerSupport implements ITimerSupport {
    private Timer mTimer;

    private Handler mHandler;

    /**
     * 倒计时时间
     */
    private long mMillisInFuture;

    /**
     * 间隔时间
     */
    private long mCountDownInterval;
    /**
     * 倒计时剩余时间
     */
    private long mMillisUntilFinished;

    private OnCountDownTimerListener mOnCountDownTimerListener;

    private TimerState mTimerState = TimerState.FINISH;

    @Deprecated
    public CountDownTimerSupport() {
        this.mHandler = new Handler();
    }

    public CountDownTimerSupport(long millisInFuture, long countDownInterval) {
        this.setMillisInFuture(millisInFuture);
        this.setCountDownInterval(countDownInterval);
        this.mHandler = new Handler();
    }

    @Override
    public void start() {
        //防止重复启动 重新启动要先reset再start
        if (mTimer == null && mTimerState != TimerState.START) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(createTimerTask(), 0, mCountDownInterval);
            mTimerState = TimerState.START;
        }
    }

    @Override
    public void pause() {
        if (mTimer != null && mTimerState == TimerState.START) {
            cancelTimer();
            mTimerState = TimerState.PAUSE;
        }
    }

    @Override
    public void resume() {
        if (mTimerState == TimerState.PAUSE) {
            start();
        }
    }

    @Override
    public void stop() {
        stopTimer(true);
    }

    @Override
    public void reset() {
        if (mTimer != null) {
            cancelTimer();
        }
        mMillisUntilFinished = mMillisInFuture;
        mTimerState = TimerState.FINISH;
    }

    private void stopTimer(final boolean cancel) {
        if (mTimer != null) {
            cancelTimer();
            mMillisUntilFinished = mMillisInFuture;
            mTimerState = TimerState.FINISH;

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mOnCountDownTimerListener != null) {
                        if (cancel) {
                            mOnCountDownTimerListener.onCancel();
                        } else {
                            mOnCountDownTimerListener.onFinish();
                        }
                    }
                }
            });
        }
    }

    private void cancelTimer() {
        mTimer.cancel();
        mTimer.purge();
        mTimer = null;
    }

    public boolean isStart() {
        return mTimerState == TimerState.START;
    }

    public boolean isFinish() {
        return mTimerState == TimerState.FINISH;
    }


    public void setMillisInFuture(long millisInFuture) {
        this.mMillisInFuture = millisInFuture;
        this.mMillisUntilFinished = mMillisInFuture;
    }


    public void setCountDownInterval(long countDownInterval) {
        this.mCountDownInterval = countDownInterval;
    }

    public void setOnCountDownTimerListener(OnCountDownTimerListener listener) {
        this.mOnCountDownTimerListener = listener;
    }

    public long getMillisUntilFinished() {
        return mMillisUntilFinished;
    }

    public TimerState getTimerState() {
        return mTimerState;
    }

    protected TimerTask createTimerTask() {
        return new TimerTask() {
            private long startTime = -1;

            @Override
            public void run() {
                if (startTime < 0) {
                    //第一次回调 记录开始时间

                    startTime = scheduledExecutionTime() - (mMillisInFuture - mMillisUntilFinished);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mOnCountDownTimerListener != null) {
                                mOnCountDownTimerListener.onTick(mMillisUntilFinished);
                            }
                        }
                    });
                } else {
                    //剩余时间
                    mMillisUntilFinished = mMillisInFuture - (scheduledExecutionTime() - startTime);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mOnCountDownTimerListener != null) {
                                mOnCountDownTimerListener.onTick(mMillisUntilFinished);
                            }
                        }
                    });
                    if (mMillisUntilFinished <= 0) {
                        //如果没有剩余时间 就停止
                        stopTimer(false);
                    }
                }
            }
        };
    }

    //示例
//    private CountDownTimerSupport mTimer;
//    public void clickStart(View v) {
//        if (mTimer != null) {
//            mTimer.stop();
//            mTimer = null;
//        }
//        long millisInFuture = Long.parseLong(ed_future.getText().toString());
//        long countDownInterval = Long.parseLong(ed_interval.getText().toString());
//        mTimer = new CountDownTimerSupport(millisInFuture, countDownInterval);
//        mTimer.setOnCountDownTimerListener(new OnCountDownTimerListener() {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                tv.setText(millisUntilFinished + "ms\n" + millisUntilFinished / 1000 + "s");
//                Log.d("CountDownTimerSupport", "onTick : " + millisUntilFinished + "ms");
//            }
//
//            @Override
//            public void onFinish() {
//                tv.setText("倒计时已结束");
//                Log.d("CountDownTimerSupport", "onFinish");
//
//                tv_state.setText(getStateText());
//            }
//
//            @Override
//            public void onCancel() {
//                tv.setText("倒计时已手动停止");
//                Log.d("CountDownTimerSupport", "onCancel");
//
//                tv_state.setText(getStateText());
//            }
//        });
//        mTimer.start();
//        tv_state.setText(getStateText());
//    }
//
//    public void clickPause(View v) {
//        if (mTimer != null) {
//            mTimer.pause();
//
//            tv_state.setText(getStateText());
//        }
//    }
//
//    public void clickResume(View v) {
//        if (mTimer != null) {
//            mTimer.resume();
//
//            tv_state.setText(getStateText());
//        }
//    }
//
//    public void clickCancel(View v) {
//        if (mTimer != null) {
//            mTimer.stop();
//        }
//    }
//
//    public void clickResetStart(View v) {
//        if (mTimer != null) {
//            mTimer.reset();
//            mTimer.start();
//
//            tv_state.setText(getStateText());
//        }
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mTimer != null) {
//            mTimer.resume();
//            tv_state.setText(getStateText());
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mTimer != null) {
//            mTimer.pause();
//            tv_state.setText(getStateText());
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mTimer != null) {
//            mTimer.stop();
//            tv_state.setText(getStateText());
//        }
//    }
}
