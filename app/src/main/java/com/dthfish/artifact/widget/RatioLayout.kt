package com.dthfish.artifact.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.dthfish.artifact.R

/**
 * Description
 * Author DthFish
 * Date  2018/12/18.
 */
class RatioLayout : FrameLayout {
    private var ratio = 0f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.RatioLayout)
        ratio = typedArray.getFloat(R.styleable.RatioLayout_ratio, 0f)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {


        var currentHeightMeasureSpec = heightMeasureSpec
        if (Math.abs(ratio - 0) > 0.01f) {
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            //根据宽高比ratio和模式创建一个测量值
            currentHeightMeasureSpec = MeasureSpec.makeMeasureSpec((widthSize * ratio).toInt(), MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, currentHeightMeasureSpec)
    }

    fun setRatio(ratio: Float) {
        this.ratio = ratio
        requestLayout()
    }

}