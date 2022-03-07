package com.cxkj.common.widget.bottombar;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import com.cxkj.common.R;


public class BottomBarTab extends FrameLayout {


    private ImageView mIcon;
    private TextView mTvTitle;
    private int mTabPosition = -1;

    private @DrawableRes
    int selectIcon;
    private @DrawableRes
    int normalIcon;

    private @ColorInt
    int selectColor;
    private @ColorInt
    int normalColor;

    private TextView mTvUnreadCount;

    public BottomBarTab(Context context, @DrawableRes int[] icon, CharSequence title, @ColorInt int[] color) {
        this(context, null, icon, title, color);
    }

    public BottomBarTab(Context context, AttributeSet attrs, int[] icon, CharSequence title, @ColorInt int[] color) {
        this(context, attrs, 0, icon, title, color);
    }

    public BottomBarTab(Context context, AttributeSet attrs, int defStyleAttr, int[] icon, CharSequence title, @ColorInt int[] color) {
        super(context, attrs, defStyleAttr);
        init(context, icon, title, color);
    }

    private void init(Context context, int[] icon, CharSequence title, int[] color) {

        normalIcon = icon[0];
        selectIcon = icon[1];

        normalColor = color[0];
        selectColor = color[1];

        LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        mIcon = findViewById(R.id.bottombar_icon);
        mTvTitle = findViewById(R.id.bottombar_text);
        if (mIcon == null || mTvTitle == null) {
            return;
        }
        mIcon.setImageResource(normalIcon);
        mTvTitle.setText(title);
        mTvTitle.setTextColor(normalColor);
    }

    public int getLayoutId() {
        return R.layout.widget_bottom_bar_tab;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            mIcon.setImageResource(selectIcon);
            mTvTitle.setTextColor(selectColor);
        } else {
            mIcon.setImageResource(normalIcon);
            mTvTitle.setTextColor(normalColor);
        }
    }

    public void setTabPosition(int position) {
        mTabPosition = position;
        if (position == 0) {
            setSelected(true);
        }
    }

    public int getTabPosition() {
        return mTabPosition;
    }

    /**
     * 设置未读数量
     */
    public void setUnreadCount(int num) {
        if (num <= 0) {
            mTvUnreadCount.setText(String.valueOf(0));
            mTvUnreadCount.setVisibility(GONE);
        } else {
            mTvUnreadCount.setVisibility(VISIBLE);
            if (num > 99) {
                mTvUnreadCount.setText("99+");
            } else {
                mTvUnreadCount.setText(String.valueOf(num));
            }
        }
    }

    /**
     * 获取当前未读数量
     */
    public int getUnreadCount() {
        int count = 0;
        if (TextUtils.isEmpty(mTvUnreadCount.getText())) {
            return count;
        }
        if (mTvUnreadCount.getText().toString().equals("99+")) {
            return 99;
        }
        try {
            count = Integer.valueOf(mTvUnreadCount.getText().toString());
        } catch (Exception ignored) {
        }
        return count;
    }


}
