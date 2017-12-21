package es.academy.solidgear.surveyx.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import es.academy.solidgear.surveyx.R;
import es.academy.solidgear.surveyx.managers.NetworkManager;
import es.academy.solidgear.surveyx.managers.Utils;
import es.academy.solidgear.surveyx.model.OptionModel;
import es.academy.solidgear.surveyx.model.QuestionModel;
import es.academy.solidgear.surveyx.services.requests.GetQuestionRequest;
import es.academy.solidgear.surveyx.ui.activities.SurveyActivity;
import es.academy.solidgear.surveyx.ui.views.AnswerCheckBox;

public class SurveyFragment extends Fragment implements CheckBox.OnCheckedChangeListener{
    private static final int UNCHECKED_VALUE = -1;

    private ViewGroup.LayoutParams PADDING_LAYOUT_PARAMS;
    private TextView mQuestionTextView;
    private LinearLayout layoutRespuestas;
    private ArrayList<CheckBox> arrayCheck = new ArrayList<CheckBox>();

    private ArrayList<Integer> mResponseSelected;
    private int[] mQuestionsId;
    private QuestionModel[] mQuestions;

    private int mLastRadioButtonId = 0;
    private int mIteration = 0;
    private boolean mIsLastQuestion = false;

    private SurveyActivity mActivity;

    private void getQuestion(int questionId) {
        Response.Listener<QuestionModel> onGetQuestion = new Response.Listener<QuestionModel>() {
            @Override
            public void onResponse(QuestionModel question) {
                SurveyFragment.this.showQuestion(question);
            }
        };

        Response.ErrorListener onGetQuestionFail = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };

        GetQuestionRequest getQuestionRequest = new GetQuestionRequest(questionId,
                                                                       onGetQuestion,
                                                                       onGetQuestionFail);
        NetworkManager.getInstance(this.getActivity()).makeRequest(getQuestionRequest);
    }

    public static SurveyFragment newInstance() {
        SurveyFragment fragment =  new SurveyFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = (SurveyActivity) getActivity();

        int paddingInPixels = (int) Utils.dimenToPixels(getActivity(), TypedValue.COMPLEX_UNIT_DIP, 20);

        PADDING_LAYOUT_PARAMS = new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, paddingInPixels);

        mQuestionsId = ((SurveyActivity)getActivity()).getQuestions();
        mQuestions = new QuestionModel[mQuestionsId.length];

        mResponseSelected = new ArrayList<Integer>();

        // show first question
        getQuestion(mQuestionsId[0]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_survey, null);

        mQuestionTextView = (TextView) root.findViewById(R.id.question_text);

        layoutRespuestas = root.findViewById(R.id.answers_outlet);
        //mAnswersOutlet.setOnCheckedChangeListener(this);

        return root;
    }

    public ArrayList<Integer> getResponseSelected() {
        return mResponseSelected;
    }

    public ArrayList<Integer> getResponses() {
        return mResponseSelected;
    }

    public void showNextQuestion() {
        if (mIteration >= mQuestionsId.length - 1) {
            return;
        }

        for(CheckBox ch : arrayCheck){
            ch.setChecked(false);
        }

        mIteration++;
        mIsLastQuestion = mIteration == (mQuestionsId.length - 1);
        getQuestion(mQuestionsId[mIteration]);
    }

    private void showQuestion(QuestionModel currentQuestion) {

        mResponseSelected.clear();
        if ( mIteration > 0 && mQuestions[mIteration-1] != null ) {
            mLastRadioButtonId = mLastRadioButtonId + mQuestions[mIteration-1].getChoices().size();
        }

        //mAnswersOutlet.clearCheck();
        //mAnswersOutlet.removeAllViews();

        mQuestions[mIteration] = currentQuestion;

        mQuestionTextView.setText(currentQuestion.getText());

        for (OptionModel option : currentQuestion.getChoices()) {

            // Create radio button with answer
            AnswerCheckBox checkBox = new AnswerCheckBox(getActivity(), option.getText());
            checkBox.setTag(option.getId());
            checkBox.setOnCheckedChangeListener(this);
            layoutRespuestas.addView(checkBox);
            arrayCheck.add(checkBox);
            //mAnswersOutlet.addView(checkBox);

            // Add padding for each answer
            // There is a bug in API 16 and below that with padding method of RadioButton
            View paddingView = new View(getActivity());
            paddingView.setLayoutParams(PADDING_LAYOUT_PARAMS);
            layoutRespuestas.addView(paddingView);
            //mAnswersOutlet.addView(paddingView);
        }
    }

    public int getCurrentQuestion() {
        return mIteration;
    }

    /*@Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        boolean enabled = checkedId != UNCHECKED_VALUE;

        if (enabled) {
            mResponseSelected.clear();
            View radioButton = group.findViewById(group.getCheckedRadioButtonId());
            mResponseSelected.add((int)radioButton.getTag());
        }

        mActivity.enableNextButton(enabled);
        mActivity.setNextButtonLabel(mIsLastQuestion);
    }*/

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            for(CheckBox ch : arrayCheck){
                if(ch.isChecked()){
                    mResponseSelected.add((int)ch.getTag());
                    break;
                }
            }
            //View checkedButton = buttonView.findViewById((Integer) buttonView.getTag());
        }

        for(int i = 0; i < arrayCheck.size(); i++){
            if(arrayCheck.get(i).isChecked()) {
                mActivity.enableNextButton(true);
                mActivity.setNextButtonLabel(true);
                break;
            }
            else if(!arrayCheck.get(i).isChecked() && (i + 1) == arrayCheck.size()){
                mActivity.enableNextButton(false);
                mActivity.setNextButtonLabel(false);
                break;
            }
        }

    }
}
