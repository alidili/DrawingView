package tech.yangle.drawing.pen;

import android.content.Context;
import android.graphics.Color;

import tech.yangle.drawing.utils.DensityUtils;

/**
 * 半透明笔
 * <p>
 * Created by yangle on 2020/10/15.
 * Website：http://www.yangle.tech
 */
public class TranslucentPen extends BasePen {

    public TranslucentPen(Context context) {
        super(context);
        // 画笔宽度
        setStrokeWidth(DensityUtils.dp2px(context, 8));
        // 画笔颜色
        setColor(Color.BLACK);
        // 画笔透明度，先设置颜色，再设置透明度0-255
        setAlpha(80);
    }
}
