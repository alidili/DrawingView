package tech.yangle.drawing.pen;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import tech.yangle.drawing.utils.DensityUtils;

/**
 * 画笔基类
 * <p>
 * Created by yangle on 2020/10/15.
 * Website：http://www.yangle.tech
 */
public class BasePen extends Paint {

    // 画笔类型
    public int mPenType = 0;

    public BasePen(Context context) {
        // 抗锯齿、防抖动
        setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        // 画笔模式为描边
        setStyle(Paint.Style.STROKE);
        // 拐角为圆角
        setStrokeJoin(Paint.Join.ROUND);
        // 两端为圆角
        setStrokeCap(Paint.Cap.ROUND);
    }
}
