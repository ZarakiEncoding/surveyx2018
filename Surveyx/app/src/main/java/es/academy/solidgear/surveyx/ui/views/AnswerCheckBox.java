package es.academy.solidgear.surveyx.ui.views;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;

import es.academy.solidgear.surveyx.R;
import es.academy.solidgear.surveyx.managers.Utils;

public class AnswerCheckBox extends android.support.v7.widget.AppCompatCheckBox {
    public AnswerCheckBox(Context context, String option) {
        super(context);

        // Layout params
        LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        setLayoutParams(layoutParams);

        // Radio button
        setButtonDrawable(R.drawable.custom_check_box);

        // Text
        setText(option);
        //setCustomFont(context, getResources().getString(R.string.default_font));
        setTextColor(getResources().getColor(R.color.form_input_color));
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        // Add some padding between button and text for API 17 or above.
        // It is not set by default
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int paddingLeft = (int) Utils.dimenToPixels(context, TypedValue.COMPLEX_UNIT_DIP, 10);
            setPadding(paddingLeft, 0, 0, 0);
        }
    }
}
