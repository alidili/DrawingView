package tech.yangle.drawing.utils;

import android.graphics.PointF;
import android.view.MotionEvent;

/**
 * Created by lixuebin on 2020/10/30
 */
public class TouchEventUtils {

    /**
     * 计算两个触点之间的距离
     *
     * @param event 触控事件
     * @return 两个触点之间的距离
     */
    public static float spacingOfTwoFinger(MotionEvent event) {
        if (event.getPointerCount() != 2) return 0;
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 获取两个触点中间的坐标点
     *
     * @param event 触控事件
     * @return 坐标点
     */
    public static PointF middleOfTwoFinger(MotionEvent event) {
        float mx = (event.getX(0) + event.getX(1)) / 2;
        float my = (event.getY(0) + event.getY(1)) / 2;
        return new PointF(mx, my);
    }
}
