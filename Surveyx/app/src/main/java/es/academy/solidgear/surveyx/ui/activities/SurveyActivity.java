package es.academy.solidgear.surveyx.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import es.academy.solidgear.surveyx.R;
import es.academy.solidgear.surveyx.managers.NetworkManager;
import es.academy.solidgear.surveyx.managers.Utils;
import es.academy.solidgear.surveyx.model.SurveyModel;
import es.academy.solidgear.surveyx.services.requestparams.SurveyPostParams;
import es.academy.solidgear.surveyx.services.requests.GetSurveyRequest;
import es.academy.solidgear.surveyx.services.requests.SendResponseRequest;
import es.academy.solidgear.surveyx.ui.fragments.ErrorDialogFragment;
import es.academy.solidgear.surveyx.ui.fragments.InformationDialogFragment;
import es.academy.solidgear.surveyx.ui.fragments.SurveyFragment;
import es.academy.solidgear.surveyx.ui.fragments.YesNoDialogFragment;
import es.academy.solidgear.surveyx.ui.views.CustomButton;

public class SurveyActivity extends BaseActivity {
    private static final String TAG = "SurveyActivity";
    public static final String SURVEY_ID = "surveyId";
    public static final String EXTRA_TOKEN = "token";

    private CustomButton mButtonNext;
    private CustomButton mButtonCancel;
    private TextView mTextViewCurrentQuestion;
    private TextView mTextViewTotal;

    private SurveyFragment mSurveyFragment;
    private InformationDialogFragment mDialog;

    private int[] mQuestions;
    private int mSurveyId;

    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        initToolbar();

        Bundle extras = getIntent().getExtras();
        mToken = extras.getString(EXTRA_TOKEN, null);

        mButtonNext = (CustomButton) findViewById(R.id.buttonNext);
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performNext();
            }
        });
        mButtonNext.setEnabled(false);

        mButtonCancel = (CustomButton) findViewById(R.id.buttonCancel);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSurvey();
            }
        });

        mTextViewCurrentQuestion = (TextView) findViewById(R.id.textViewCurrentQuestion);
        mTextViewTotal = (TextView) findViewById(R.id.textViewTotal);

        mSurveyFragment = SurveyFragment.newInstance();

        mDialog = InformationDialogFragment.newInstance(R.string.dialog_getting_survey);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mDialog.show(fragmentManager, "dialog");

        mSurveyId = getIntent().getExtras().getInt(SURVEY_ID);
        getSurvey(mSurveyId);
    }

    @Override
    public void onBackPressed() {
        cancelSurvey();
    }

    @Override
    public void onStop() {
        NetworkManager.getInstance(this).cancelAll(TAG);
        super.onStop();
    }
    private void getSurvey(int surveyId) {
        Response.Listener<SurveyModel> onGetSurvey = new Response.Listener<SurveyModel>() {
            @Override
            public void onResponse(SurveyModel response) {
                mQuestions = response.getQuestions();
                mTextViewTotal.setText(String.valueOf(mQuestions.length));
                updateCurrentQuestionTitle();
                mDialog.dismiss();
                Utils.showFragment(SurveyActivity.this, mSurveyFragment, R.id.container);
            }
        };

        Response.ErrorListener onGetSurveyFail = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();

                ErrorDialogFragment.OnClickClose onClickClose = new ErrorDialogFragment.OnClickClose() {
                    @Override
                    public void onClickClose() {
                        finish();
                    }
                };
                showGenericError(error.toString(), onClickClose);
            }
        };

        GetSurveyRequest surveyRequest = new GetSurveyRequest(surveyId, onGetSurvey, onGetSurveyFail);
        NetworkManager.getInstance(this).makeRequest(surveyRequest);
    }

    public int[] getQuestions() {
        return mQuestions;
    }

    private void cancelSurvey() {
        YesNoDialogFragment.OnClick onClick = new YesNoDialogFragment.OnClick() {
            @Override
            public void onClickYes() {
                finish();
            }

            @Override
            public void onClickNo() {

            }
        };
        String dialogMessage = getString(R.string.dialog_finish_survey);
        YesNoDialogFragment yesNoDialogFragment = YesNoDialogFragment.newInstance(dialogMessage,
                                                                                  onClick);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        yesNoDialogFragment.show(fragmentManager, "dialog");
    }

    public void enableNextButton(boolean enabled) {
        mButtonNext.setEnabled(enabled);
    }

    public void setNextButtonLabel(boolean isLast) {
        if (isLast) {
            mButtonNext.setText(R.string.global_submit);
            mButtonNext.setContentDescription(getString(R.string.descriptor_survey_submit_button));
        } else {
            mButtonNext.setText(R.string.global_next);
            mButtonNext.setContentDescription(getString(R.string.descriptor_survey_next_button));
        }
    }

    private void performNext() {
        sendAnswerToServer();
    }

    private void onAnswerSentToServer() {
        if (mSurveyFragment.getCurrentQuestion() >= mQuestions.length-1) {
            showSocialNetworkPage();
            finish();
        } else {
            mSurveyFragment.showNextQuestion();
            updateCurrentQuestionTitle();
        }
    }

    private void showSocialNetworkPage() {
        Intent intent = new Intent(SurveyActivity.this, SocialNetworkActivity.class);
        startActivity(intent);
    }

    private void updateCurrentQuestionTitle() {
        int currentQuestion = mSurveyFragment.getCurrentQuestion();
        mTextViewCurrentQuestion.setText(String.valueOf(++currentQuestion));
    }

    private void sendAnswerToServer() {
        int questionId = mQuestions[mSurveyFragment.getCurrentQuestion()];
        SurveyPostParams surveyPostParams = new SurveyPostParams();
        surveyPostParams.setToken(mToken);
        surveyPostParams.setSurvey(mSurveyId);
        surveyPostParams.setQuestion(questionId);
        ArrayList<Integer> responseSelected = mSurveyFragment.getResponseSelected();
        surveyPostParams.setChoice(responseSelected);

        Response.Listener<JSONObject> mListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            if (response != null) {
                Toast.makeText(SurveyActivity.this, "Response sent successfully", Toast.LENGTH_SHORT).show();
                onAnswerSentToServer();
            } else {
                Toast.makeText(SurveyActivity.this, "Response NOT sent successfully", Toast.LENGTH_SHORT).show();
            }
            }
        };

        Response.ErrorListener mErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SurveyActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        SendResponseRequest sendResponseRequest = new SendResponseRequest(questionId, surveyPostParams, mListener, mErrorListener);
        NetworkManager.getInstance(this).makeRequest(sendResponseRequest);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        cancelSurvey();
        return true;
    }

}
