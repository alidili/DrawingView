package tech.yangle.drawing.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

/**
 * Android高效载入大图片（按指定大小取得图片缩略图）
 * <p>
 * Created by lixuebin on 2020/11/3
 */
public class BitmapUtils {

    /**
     * 通过Uri加载图片
     *
     * @param context   上下文
     * @param uri       图片Uri
     * @param reqWidth  控制要显示的缩略图宽度
     * @param reqHeight 控制要显示的缩略图高度
     * @return Bitmap
     */
    public static Bitmap decodeBitmapFromResource(Context context, Uri uri, int reqWidth,
                                                  int reqHeight) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().
                    openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // true将不返回实际的bitmap，不给其分配内存空间
            // 里面只包括一些解码边界信息即图片大小信息
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            // 计算图片缩放比例
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // 重新读取图片，才能真正返回一个Bitmap
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过 Resources 载入本地图片
     *
     * @param resources 应用程序资源类
     * @param resId     图片资源ID
     * @param reqWidth  控制要显示的缩略图宽度
     * @param reqHeight 控制要显示的缩略图高度
     * @return Bitmap
     */
    public static Bitmap decodeBitmapFromResource(Resources resources, int resId, int reqWidth,
                                                  int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    /**
     * 根据宽高度和屏幕宽高度计算压缩程度
     *
     * @param options   创建位图对象的操作对象
     * @param reqWidth  控制要显示的缩略图宽度
     * @param reqHeight 控制要显示的缩略图高度
     * @return 图片压缩程度
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                             int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
