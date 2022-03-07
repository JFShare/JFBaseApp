package com.cxkj.common.widget.rv.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

//LinearLayoutManager 添加间隔
class LinearDecoration(
    private val topBottom : Int ,
    private val leftRight : Int ,
    @RecyclerView.Orientation private var orientation : Int = RecyclerView.VERTICAL ,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect : Rect , view : View , parent : RecyclerView ,
        state : RecyclerView.State ,
    ) {
        val totalCount = parent.adapter!!.itemCount
        when (orientation) {
            RecyclerView.VERTICAL -> {
                outRect.left = leftRight
                outRect.right = leftRight
                when (parent.getChildAdapterPosition(view)) {
                    0 -> {
                        outRect.top = topBottom
                        outRect.bottom = topBottom / 2
                    }
                    totalCount - 1 -> {
                        outRect.top = topBottom / 2
                        outRect.bottom = topBottom
                    }
                    else -> {
                        outRect.top = topBottom / 2
                        outRect.bottom = topBottom / 2
                    }
                }

            }
            RecyclerView.HORIZONTAL -> {
                outRect.top = topBottom
                outRect.bottom = topBottom
                when (parent.getChildAdapterPosition(view)) {
                    0 -> {
                        outRect.left = leftRight
                        outRect.right = leftRight / 2
                    }
                    totalCount - 1 -> {
                        outRect.left = leftRight / 2
                        outRect.right = leftRight
                    }
                    else -> {
                        outRect.left = leftRight / 2
                        outRect.right = leftRight / 2
                    }
                }
            }
        }

    }
}