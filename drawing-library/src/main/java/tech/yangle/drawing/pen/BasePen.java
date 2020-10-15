package tech.yangle.drawing.pen;

import android.graphics.Paint;

/**
 * 画笔基类
 * <p>
 * Created by yangle on 2020/10/15.
 * Website：http://www.yangle.tech
 */
class BasePen extends Paint {

    public BasePen() {
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
