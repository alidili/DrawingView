package tech.yangle.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 支持可缩放的绘画板
 * <p>
 * Created by lixuebin on 2020/10/30
 */
public class ScaleDrawingView extends RelativeLayout {

    private static final float MAX_SCALE = 10.0F;
    private static final float MIN_SCALE = 1.0F;
    // 用于存放矩阵的9个值
    private final float[] mMatrixValues = new float[9];
    private float mBorderX, mBorderY;
    private final DrawingView drawingView;
    // 是否两个触控点接触屏幕
    private boolean isTranslate;
    private float mOldDistance;
    private PointF mOldPointer;

    public ScaleDrawingView(Context context) {
        super(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        drawingView = new DrawingView(getContext());
        drawingView.setIsCanDraw(true);
        addView(drawingView, layoutParams);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                return drawingView.onTouchEvent(ev);

            case MotionEvent.ACTION_POINTER_DOWN:
                isTranslate = true;
                mOldDistance = spacingOfTwoFinger(ev);
                mOldPointer = middleOfTwoFinger(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isTranslate) {
                    return drawingView.onTouchEvent(ev);
                }
                if (ev.getPointerCount() == 2) {
                    float newDistance = spacingOfTwoFinger(ev);
                    float scaleFactor = newDistance / mOldDistance;
                    scaleFactor = checkingScale(drawingView.getScaleX(), scaleFactor);
                    // 设置缩放比例
                    drawingView.setScaleX(drawingView.getScaleY() * scaleFactor);
                    drawingView.setScaleY(drawingView.getScaleY() * scaleFactor);
                    mOldDistance = newDistance;
                    PointF newPointer = middleOfTwoFinger(ev);
                    drawingView.setX(drawingView.getX() + newPointer.x - mOldPointer.x);
                    drawingView.setY(drawingView.getY() + newPointer.y - mOldPointer.y);
                    mOldPointer = newPointer;
                    checkingBorder();
                }

            case MotionEvent.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_UP:
                if (!isTranslate) {
                    return drawingView.onTouchEvent(ev);
                }
                // 获取当前缩放比例
                drawingView.getMatrix().getValues(mMatrixValues);
                drawingView.setScaleAndOffset(drawingView.getScaleX(), mMatrixValues[2],
                        mMatrixValues[5]);
                isTranslate = false;
                break;
        }
        return true;
    }

    /**
     * 检查缩放范围
     *
     * @param scale       缩放值
     * @param scaleFactor 缩放因子
     * @return 缩放因子
     */
    private float checkingScale(float scale, float scaleFactor) {
        // 缩放的范围控制
        if ((scale <= MAX_SCALE && scaleFactor > 1.0)
                || (scale >= MIN_SCALE && scaleFactor < 1.0)) {
            // 最大值最小值判断
            if (scale * scaleFactor < MIN_SCALE) {
                scaleFactor = MIN_SCALE / scale;
            }
            if (scale * scaleFactor > MAX_SCALE) {
                scaleFactor = MAX_SCALE / scale;
            }
        }
        return scaleFactor;
    }

    private void checkingBorder() {
        PointF offset = offsetBorder();
        drawingView.setX(drawingView.getX() + offset.x);
        drawingView.setY(drawingView.getY() + offset.y);
        if (drawingView.getScaleX() == 1) {
            drawingView.setX(0);
            drawingView.setY(0);
        }
    }

    private PointF offsetBorder() {
        PointF offset = new PointF(0, 0);
        if (drawingView.getScaleX() > 1) {
            drawingView.getMatrix().getValues(mMatrixValues);
            if (mMatrixValues[2] > -(mBorderX * (drawingView.getScaleX() - 1))) {
                offset.x = -(mMatrixValues[2] + mBorderX * (drawingView.getScaleX() - 1));
            }

            if (mMatrixValues[2] + drawingView.getWidth() * drawingView.getScaleX() -
                    mBorderX * (drawingView.getScaleX() - 1) < getWidth()) {
                offset.x = getWidth() - (mMatrixValues[2] + drawingView.getWidth() *
                        drawingView.getScaleX() - mBorderX * (drawingView.getScaleX() - 1));
            }

            if (mMatrixValues[5] > -(mBorderY * (drawingView.getScaleY() - 1))) {
                offset.y = -(mMatrixValues[5] + mBorderY * (drawingView.getScaleY() - 1));
            }

            if (mMatrixValues[5] + drawingView.getHeight() * drawingView.getScaleY() -
                    mBorderY * (drawingView.getScaleY() - 1) < getHeight()) {
                offset.y = getHeight() - (mMatrixValues[5] + drawingView.getHeight() *
                        drawingView.getScaleY() - mBorderY * (drawingView.getScaleY() - 1));
            }
        }
        return offset;
    }

    /**
     * 计算两个触点之间的距离
     *
     * @param event 触控事件
     * @return 两个触点之间的距离
     */
    public float spacingOfTwoFinger(MotionEvent event) {
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
    public PointF middleOfTwoFinger(MotionEvent event) {
        float mx = (event.getX(0) + event.getX(1)) / 2;
        float my = (event.getY(0) + event.getY(1)) / 2;
        return new PointF(mx, my);
    }

    /**
     * 设置画笔类型
     *
     * @param penType 画笔类型
     */
    public void setPenCurrentType(int penType) {
        drawingView.setPenType(penType);
    }

    /**
     * 清除画布
     */
    public void clear() {
        drawingView.clear();
    }

    /**
     * 设置画笔宽度
     *
     * @param penWidth 宽度值
     */
    public void setPaintWidth(int penWidth) {
        drawingView.setPenWidth(penWidth);
    }

    /**
     * 设置画笔颜色
     *
     * @param penColor 画笔颜色
     */
    public void setPaintColor(int penColor) {
        drawingView.setPenColor(penColor);
    }

    /**
     * 设置图片涂鸦
     *
     * @param bitmap 位图对象
     */
    public void setBackgroundPic(Bitmap bitmap) {
        drawingView.setBackgroundPic(bitmap);
    }
}
