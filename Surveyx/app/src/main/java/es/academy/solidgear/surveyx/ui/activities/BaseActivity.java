package es.academy.solidgear.surveyx.ui.activities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import es.academy.solidgear.surveyx.R;
import es.academy.solidgear.surveyx.ui.fragments.ErrorDialogFragment;

public class BaseActivity extends AppCompatActivity {
    protected void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_launcher);

        TextView textViewTitle = (TextView) findViewById(R.id.toolbar_title);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/KGSecondChancesSketch.ttf");
        textViewTitle.setTypeface(typeFace);
    }

    protected void showGenericError(String error, ErrorDialogFragment.OnClickClose onClickClose) {
        ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance(error, onClickClose);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        errorDialog.show(fragmentManager, "dialog");
    }
}
