package es.academy.solidgear.surveyx.ui.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.Collections;
import java.util.List;

import es.academy.solidgear.surveyx.R;
import es.academy.solidgear.surveyx.managers.ImageRequestManager;
import es.academy.solidgear.surveyx.model.SurveyModel;
import es.academy.solidgear.surveyx.ui.activities.SurveyListActivity;

public class SurveyListAdapter extends RecyclerView.Adapter<SurveyListAdapter.SurveyViewHolder> {
    private List<SurveyModel> mSurveyModelList;
    private SurveyListActivity mActivity;

    public SurveyListAdapter(List<SurveyModel> questionnaireList, SurveyListActivity activity) {
        mSurveyModelList = questionnaireList;
        Collections.sort(mSurveyModelList);
        mActivity = activity;
    }

    @Override
    public int getItemCount() {
        return mSurveyModelList.size();
    }

    @Override
    public void onBindViewHolder(final SurveyViewHolder surveyViewHolder, int i) {
        final SurveyModel survey = mSurveyModelList.get(i);
        surveyViewHolder.layout.setContentDescription("surveyCard" + mSurveyModelList.get(i).getTitle());
        surveyViewHolder.title.setText(survey.getTitle());
        Typeface typeFace = Typeface.createFromAsset(mActivity.getAssets(), "fonts/MuseoSans-900Italic.otf");
        surveyViewHolder.title.setTypeface(typeFace);

        surveyViewHolder.description.setText(survey.getDescription());
        typeFace = Typeface.createFromAsset(mActivity.getAssets(), "fonts/MuseoSans-100.otf");
        surveyViewHolder.description.setTypeface(typeFace);
        if (!survey.hasCoordinates()) {
            surveyViewHolder.map.setVisibility(View.INVISIBLE);
        }

        surveyViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.showSurveyPage(survey);
            }
        });

        ImageLoader imageLoader = ImageRequestManager.getInstance(mActivity).getImageLoader();
        if (survey.getImage() != null) {
            surveyViewHolder.icon.setImageUrl(survey.getImage(), imageLoader);
        }
    }

    @Override
    public SurveyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_survey, viewGroup, false);

        return new SurveyViewHolder(itemView);
    }

    public static class SurveyViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView description;
        protected RelativeLayout layout;
        protected ImageView map;
        protected NetworkImageView icon;

        public SurveyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textTitle);
            description = (TextView) itemView.findViewById(R.id.textDescription);
            layout = (RelativeLayout) itemView.findViewById(R.id.layoutSurvey);
            map = (ImageView) itemView.findViewById(R.id.imageViewLocation);
            icon = (NetworkImageView) itemView.findViewById(R.id.imageViewIcon);
        }
    }
}
