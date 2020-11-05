package tech.yangle.sample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.permissionx.guolindev.PermissionX;

import tech.yangle.drawing.DrawingView;
import tech.yangle.drawing.PenType;
import tech.yangle.drawing.ScaleDrawingView;
import tech.yangle.drawing.utils.BitmapUtils;

/**
 * 示例
 * <p>
 * Created by yangle on 2020/10/15.
 * Website：http://www.yangle.tech
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private DrawingView drawingView;
    private ScaleDrawingView scaleDrawingView;
    // 默认不选中
    private int mSelectIndex = -1;
    private static final int REQUEST_CODE_PICTURE_GALLERY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 动态替换根布局
        ConstraintLayout rootView = findViewById(R.id.cl_root);
        scaleDrawingView = new ScaleDrawingView(getApplicationContext());
        rootView.addView(scaleDrawingView, 1);

        drawingView = findViewById(R.id.drawing_view);
        AppCompatButton btnPen = findViewById(R.id.btn_pen);
        AppCompatButton btnPenWidth = findViewById(R.id.btn_pen_width);
        AppCompatButton btnPenColor = findViewById(R.id.btn_pen_color);
        AppCompatButton btnEraser = findViewById(R.id.btn_eraser);
        AppCompatButton btnClear = findViewById(R.id.btn_clear);
        AppCompatButton btnPenGraffiti = findViewById(R.id.btn_pic_graffiti);
        btnPen.setOnClickListener(this);
        btnPenWidth.setOnClickListener(this);
        btnPenColor.setOnClickListener(this);
        btnEraser.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnPenGraffiti.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pen:
                scaleDrawingView.setPenCurrentType(PenType.STANDARD_PEN);
                break;

            case R.id.btn_pen_width: // 设置画笔宽度
                scaleDrawingView.setPaintWidth(10);
                break;

            case R.id.btn_pen_color: // 设置画笔颜色
                scaleDrawingView.setPenCurrentType(PenType.STANDARD_PEN);
                showPenColorDialog();
                break;

            case R.id.btn_eraser: // 设置橡皮檫
                scaleDrawingView.setPenCurrentType(PenType.ERASER);
                break;

            case R.id.btn_clear:
                scaleDrawingView.clear(); // 撤销
                break;

            case R.id.btn_pic_graffiti: // 设置图片涂鸦
                startPicGraffiti();
                break;

            default:
                break;
        }
    }

    /**
     * 动态申请存储卡读写权限
     */
    private void startPicGraffiti() {
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .explainReasonBeforeRequest()
                .onExplainRequestReason((scope, deniedList, beforeRequest) ->
                        scope.showRequestReasonDialog(deniedList,
                                "即将申请的权限是程序必须依赖的权限",
                                "我已明白"))
                .onForwardToSettings((scope, deniedList) ->
                        scope.showForwardToSettingsDialog(deniedList,
                                "您需要去应用程序设置当中手动开启权限",
                                "我已明白"))
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        Log.i(TAG, " [PermissionX]: 所有申请的权限都已通过 ");
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_CODE_PICTURE_GALLERY);

                    } else {
                        Toast.makeText(MainActivity.this, "您拒绝了如下权限："
                                + deniedList.size(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICTURE_GALLERY) {
            if (data == null) return;
            int reqWidth = 720;
            int reqHeight = 720;
            Bitmap bitmap = BitmapUtils.decodeBitmapFromResource(this, data.getData(),
                    reqWidth, reqHeight);
            if (bitmap != null) {
                Log.i(TAG, "[onActivityResult] 缩略图高度：" + bitmap.getHeight() +
                        " 宽度： " + bitmap.getWidth());
                scaleDrawingView.setBackgroundPic(bitmap);
            } else {
                Log.i(TAG, "[onActivityResult]: bitmap is null");
            }
        }
    }

    /**
     * 选择画笔颜色
     */
    private void showPenColorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_color);
        String[] stringArray = getResources().getStringArray(R.array.paint_style);
        builder.setSingleChoiceItems(stringArray, mSelectIndex, (dialog, which) -> {
            mSelectIndex = which;
            String color = stringArray[mSelectIndex];
            switch (color) {
                case "红色":
                    scaleDrawingView.setPaintColor(Color.RED);
                    break;
                case "绿色":
                    scaleDrawingView.setPaintColor(Color.GREEN);
                    break;
                case "黄色":
                    scaleDrawingView.setPaintColor(Color.YELLOW);
                    break;
                case "蓝色":
                    scaleDrawingView.setPaintColor(Color.BLUE);
                    break;
                default:
                    scaleDrawingView.setPaintColor(Color.BLACK);
                    break;
            }
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.dialog_negative, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawingView.release();
    }
}