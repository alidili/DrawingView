package tech.yangle.drawing.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * 像素密度转换
 * Created by yangle on 2020/10/16.
 */
public class DensityUtils {

    /**
     * dp转px
     *
     * @param context Context
     * @param dp      dp
     * @return px
     */
    public static int dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
