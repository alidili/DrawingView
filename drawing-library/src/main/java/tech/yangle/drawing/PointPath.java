package tech.yangle.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import tech.yangle.drawing.pen.BasePen;
import tech.yangle.drawing.pen.Eraser;
import tech.yangle.drawing.pen.StandardPen;
import tech.yangle.drawing.pen.TranslucentPen;
import tech.yangle.drawing.utils.DensityUtils;

/**
 * 坐标点轨迹
 * <p>
 * Created by lixuebin on 2020/10/31
 */
public class PointPath {

    private final Path mPath;
    private Paint mPaint;
    private PointF mPrePoint;
    private float currentWidth;
    private int currentColor = Color.BLACK;
    // 画笔类型
    private int mCurrentType = PenType.STANDARD_PEN;
    // 画笔默认宽度
    public static final float NORMAL_LINE_WIDTH = 4;

    public PointPath(Paint paint) {
        mPath = new Path();
        mPaint = paint;
    }

    public synchronized static PointPath getInstance(PointF pointF, Paint paint) {
        PointPath mPointPath = new PointPath(paint);
        // 把画笔移动(pointF.x，pointF.y)出开始绘制
        mPointPath.mPath.moveTo(pointF.x, pointF.y);
        mPointPath.mPrePoint = pointF;
        return mPointPath;
    }

    public void savePointToPath(PointF mCurrentPoint) {
        // 绘制圆滑曲线
        mPath.quadTo(mPrePoint.x, mPrePoint.y, mCurrentPoint.x, mCurrentPoint.y);
        mPrePoint = mCurrentPoint;
    }

    /**
     * 重置画笔属性
     *
     * @param context 上下文
     * @param canvas  画布
     */
    public void disPlayPath(Context context, Canvas canvas) {
        if (mPaint == null) {
            mPaint = new BasePen(context);
        }
        if (mCurrentType == PenType.ERASER) {
            mPaint = new Eraser(context);
        } else if (mCurrentType == PenType.TRANSLUCENT_PEN) {
            mPaint = new TranslucentPen(context);
            mPaint.setColor(currentColor);
            mPaint.setStrokeWidth(DensityUtils.dp2px(context, currentWidth));
        } else {
            mPaint = new StandardPen(context);
            mPaint.setColor(currentColor);
            mPaint.setStrokeWidth(DensityUtils.dp2px(context, currentWidth));
        }
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 设置画笔当前类型
     *
     * @param currentPathType 画笔当前类型
     */
    public void setCurrentPathType(int currentPathType) {
        mCurrentType = currentPathType;
    }

    /**
     * 设置画笔当前宽度
     *
     * @param currentWidth 当前宽度值
     */
    public void setCurrentWidth(float currentWidth) {
        this.currentWidth = currentWidth;
    }

    /**
     * 设置画笔当前颜色
     *
     * @param currentColor 当前颜色值
     */
    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }
}
