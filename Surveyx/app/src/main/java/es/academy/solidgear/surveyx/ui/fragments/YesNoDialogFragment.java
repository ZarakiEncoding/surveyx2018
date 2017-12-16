package es.academy.solidgear.surveyx.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.academy.solidgear.surveyx.R;

public class YesNoDialogFragment extends DialogFragment {
    private TextView textViewInformation;
    private Button buttonYes;
    private Button buttonNo;
    private OnClick mOnClick;
    private String message;

    public static YesNoDialogFragment newInstance(String message, OnClick mOnClick) {
        YesNoDialogFragment fragment = new YesNoDialogFragment();
        fragment.message = message;
        fragment.mOnClick = mOnClick;
        return fragment;
    }

    public YesNoDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null) {
            return;
        }

        getDialog().getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                                          RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setCancelable(false);
        View view = inflater.inflate(R.layout.fragment_yes_no, container, false);
        textViewInformation = (TextView) view.findViewById(R.id.textViewMessage);
        textViewInformation.setText(message);
        buttonYes = (Button) view.findViewById(R.id.buttonYes);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mOnClick.onClickYes();
            }
        });
        buttonNo = (Button) view.findViewById(R.id.buttonNo);
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mOnClick.onClickNo();
            }
        });

        return view;
    }

    public interface OnClick {
        public void onClickYes();
        public void onClickNo();
    }
}
