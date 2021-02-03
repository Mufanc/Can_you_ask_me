package mufanc.edxp.kiwi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 动态插入控件
//        LayoutInflater.from(this).inflate(
//                R.layout.activity_main,
//                (ViewGroup) getWindow().getDecorView()
//        );
//
//        FrameLayout layout = new FrameLayout(this);
//        LayoutInflater.from(this).inflate(
//                R.layout.floaty_window,
//                layout
//        );
//        ((FrameLayout) getWindow().getDecorView().getRootView()).addView(layout);
    }
}