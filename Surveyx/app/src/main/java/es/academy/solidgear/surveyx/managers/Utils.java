package es.academy.solidgear.surveyx.managers;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.TypedValue;

import java.text.DecimalFormat;

public class Utils {
    public static float dimenToPixels(Context context, int complexUnit, float units) {
        float pixels = TypedValue.applyDimension(complexUnit, units, context.getResources().getDisplayMetrics());
        return pixels;
    }

    public static void showFragment(Activity act, Fragment fragment, int idFragmentContent) {
        FragmentTransaction transaction = act.getFragmentManager().beginTransaction();
        transaction.replace(idFragmentContent, fragment);
        transaction.commit();
    }

    public static String getDistanceString(float distance) {
        String unit = "m";
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        if (distance >= 1000) {
            distance /= 1000;
            unit = "km";
        }

        return decimalFormat.format(distance) + " " + unit;
    }

}
