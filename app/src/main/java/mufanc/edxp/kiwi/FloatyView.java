package mufanc.edxp.kiwi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatyView extends FrameLayout {
    public FloatyView(Context context) {
        super(context);
    }

    public FloatyView(Context context, Drawable background) {
        super(context);

        LinearLayout linearLayout = new LinearLayout(context);

        TextView hint = new TextView(context);
        hint.setText("该站点请求打开您的应用，是否允许？");
        hint.setTextSize(15);
        hint.setPadding(5, 5, 5, 5);
        linearLayout.addView(hint);

        TextView button = new TextView(context);
        button.setText("确定");
        button.setTextSize(15);
        button.setTextColor(Color.parseColor("#03a9f4"));
        button.setPadding(5, 5, 5, 5);
        linearLayout.addView(button);

        addView(linearLayout);
        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM
        );
        layoutParams.setMargins(0, 0, 0, 200);
        setLayoutParams(layoutParams);
        setBackground(background);

//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getLayoutParams();
//        params.height = 100;
//        params.width = 100;
//        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
//        this.setLayoutParams(params);
//        this.setBackgroundColor(Color.YELLOW);
    }
}
