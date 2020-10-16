package tech.yangle.drawing.pen;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import tech.yangle.drawing.utils.DensityUtils;

/**
 * 橡皮擦
 * <p>
 * Created by yangle on 2020/10/15.
 * Website：http://www.yangle.tech
 */
public class Eraser extends BasePen {

    public Eraser(Context context) {
        super(context);
        // 画笔宽度
        setStrokeWidth(DensityUtils.dp2px(context, 28));
        // 画笔颜色
        setColor(Color.TRANSPARENT);
        // 擦除模式
        setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }
}
