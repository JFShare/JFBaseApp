package com.cxkj.common.widget.rv.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

//GridLayoutManager添加间隔,每行第一个item左边和最后一个item右边间隔是0，中间的item左右间隔总和相同实现item大小相同
class GridDecoration(private val spacing : Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect : Rect ,
        view : View ,
        parent : RecyclerView ,
        state : RecyclerView.State ,
    ) {
        val position = parent.getChildAdapterPosition(view)
        val spanCount = (parent.layoutManager as GridLayoutManager).spanCount
        val column = position % spanCount
        outRect.left = column * spacing / spanCount
        outRect.right = spacing - (column + 1) * spacing / spanCount
        if (position < spanCount) {
            outRect.top = spacing
        }
        outRect.bottom = spacing
    }
}
