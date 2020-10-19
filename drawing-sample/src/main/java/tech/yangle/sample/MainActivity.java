package tech.yangle.sample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import tech.yangle.drawing.DrawingView;
import tech.yangle.drawing.PenType;

/**
 * 示例
 * <p>
 * Created by yangle on 2020/10/15.
 * Website：http://www.yangle.tech
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingView = findViewById(R.id.drawing_view);
        AppCompatButton btnPen = findViewById(R.id.btn_pen);
        AppCompatButton btnEraser = findViewById(R.id.btn_eraser);
        AppCompatButton btnReset = findViewById(R.id.btn_reset);
        btnPen.setOnClickListener(this);
        btnEraser.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        drawingView.init(1920, 1080);
        drawingView.setIsCanDraw(true);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pen:
                drawingView.setPenType(PenType.STANDARD_PEN);
                break;

            case R.id.btn_eraser:
                drawingView.setPenType(PenType.ERASER);
                break;

            case R.id.btn_reset:
                drawingView.reset();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawingView.release();
    }
}