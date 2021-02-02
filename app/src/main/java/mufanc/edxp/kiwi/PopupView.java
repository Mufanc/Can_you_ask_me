package mufanc.edxp.kiwi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import de.robv.android.xposed.XposedHelpers;

public class PopupView extends PopupWindow {
    @SuppressLint("SetTextI18n")
    public PopupView(Context context, Drawable background, String packageName, Intent intent) {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout frame = new LinearLayout(context);
        frame.setGravity(Gravity.CENTER);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView hint = new TextView(context);
        hint.setText("网页请求打开" + getNameByPackage(context, packageName) + "，是否允许？");
        hint.setTextSize(16);
        hint.setTextColor(Color.WHITE);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(5, 15, 5, 5);
        linearLayout.addView(hint);

        LinearLayout line = new LinearLayout(context);
        line.setOrientation(LinearLayout.HORIZONTAL);
        View colorLine = new View(context);
        colorLine.setBackgroundColor(Color.argb(128, 255, 255, 255));
        colorLine.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                2,
                1.0f
        ));
        line.addView(colorLine);
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        line.setPadding(30, 10, 30, 10);
        line.setLayoutParams(new LinearLayout.LayoutParams(
                (int) (0.8 * metrics.widthPixels),
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        linearLayout.addView(line);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);
        TextView confirm = new TextView(context);
        confirm.setText("确定");
        confirm.setTextSize(20);
        confirm.setTextColor(Color.parseColor("#03a9f4"));
        confirm.setPadding(10, 10, 10, 10);
        confirm.setGravity(Gravity.CENTER);
        confirm.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f
        ));
        confirm.setOnClickListener((event) -> {
            XposedHelpers.setAdditionalInstanceField(intent, "ACCEPT", "");
            context.startActivity(intent);
            dismiss();
        });
        container.addView(confirm);

        View view = new View(context);
        view.setBackgroundColor(Color.argb(128, 255, 255, 255));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                2, ViewGroup.LayoutParams.MATCH_PARENT
        );
        params.setMargins(0, 0, 0, 10);
        view.setLayoutParams(params);
        container.addView(view);

        TextView cancel = new TextView(context);
        cancel.setText("不好");
        cancel.setTextSize(20);
        cancel.setTextColor(Color.parseColor("#ff0f0f"));
        cancel.setPadding(10, 10, 10, 10);
        cancel.setGravity(Gravity.CENTER);
        cancel.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f
        ));
        container.addView(cancel);
        linearLayout.addView(container);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        linearLayout.setBackground(background);
        frame.addView(linearLayout);
        frame.setBackgroundColor(Color.argb(64, 0, 0,0));
        frame.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        frame.setOnClickListener((event) -> {
            dismiss();
        });


        setContentView(frame);
    }

    public static String getNameByPackage(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            String name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
            if (name.equals("")) {
                return "您的应用";
            }
            return "「" + name + "」";
        } catch (PackageManager.NameNotFoundException err) {
            return "您的应用";
        }
    }
}
