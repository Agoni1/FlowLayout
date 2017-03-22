package com.agoni.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Agoni on 2017/3/21.
 */

public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        super(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

//        Log.d("tag", sizeWidth + "     " + sizeHeight);

        //如果为wrapcontent，需要自己计算宽高
        int width = 0;
        int height = 0;

        //记录行宽和行高
        int lineWidth = 0;
        int lineHeight = 0;

        //遍历所有子view
        int count = getChildCount();
//        Log.d("tag", count + "");

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            //测量child的大小
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            //获取child的layoutparams
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            //获取child的实际占据的宽高
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

//            Log.d("tag", "第" + i + "个view的宽是" + childWidth);

            if (lineWidth + childWidth <= sizeWidth) {
//                Log.d("tag", "放得下");
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
                width = Math.max(width, lineWidth);
                height = Math.max(lineHeight, height);
            } else {
//                Log.d("tag", "放不下");
                width = Math.max(width, childWidth);
                height += childHeight;
                lineWidth = childWidth;
                lineHeight = childHeight;
            }
//            Log.d("tag", width + "        " + height);
        }

        //将计算的尺寸设置给FlowLayout
        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width,
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("tag","onlayout");

        //获取容器的宽度
        int width = getWidth();

        //获取child的数量
        int count = getChildCount();

        //记录当前child所在位置的行宽和行高
        int lineWidth=0;
        int lineHeight=0;

        //当前child距屏幕左边距和上边距
        int top=0;
        int left=0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE)
            {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            if (lineWidth+childWidth>width){
                //需要换行
                top+=lineHeight;
                lineWidth=0;//换行时重置行宽
            }
            left=lineWidth;
            lineWidth+=childWidth;
            lineHeight=Math.max(lineHeight,childHeight);

//            Log.d("sss",left+"   "+top);

            child.layout(left+lp.leftMargin,
                    top+lp.topMargin,left+childWidth-lp.rightMargin,top+childHeight-lp.bottomMargin);

        }

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
