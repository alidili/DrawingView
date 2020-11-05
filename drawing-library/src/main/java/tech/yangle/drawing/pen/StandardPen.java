package tech.yangle.drawing.pen;

import android.content.Context;
import android.graphics.Color;

import tech.yangle.drawing.PenType;
import tech.yangle.drawing.utils.DensityUtils;

/**
 * 标准笔
 * <p>
 * Created by yangle on 2020/10/15.
 * Website：http://www.yangle.tech
 */
public class StandardPen extends BasePen {

    public StandardPen(Context context) {
        super(context);
        // 画笔类型
        mPenType = PenType.STANDARD_PEN;
        // 画笔宽度
        setStrokeWidth(DensityUtils.dp2px(context, 4));
        // 画笔颜色
        setColor(Color.BLACK);
    }
}
