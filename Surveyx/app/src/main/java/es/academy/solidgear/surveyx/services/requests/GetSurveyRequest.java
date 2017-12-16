package es.academy.solidgear.surveyx.services.requests;

import android.net.Uri;

import com.android.volley.Response;

import es.academy.solidgear.surveyx.model.SurveyModel;
import es.academy.solidgear.surveyx.services.requestparams.LoginRequestParams;

public class GetSurveyRequest extends BaseJSONRequest<LoginRequestParams, SurveyModel> {

    private static final String PATH = "surveys/";

    public GetSurveyRequest(int id, Response.Listener<SurveyModel> listener,
                            Response.ErrorListener errorListener) {
        super(Method.GET, buildURL(id), SurveyModel.class, null,
                null, listener, errorListener);
    }

    private static String buildURL(int id) {
        Uri uri = new Uri.Builder()
                .path(PATH + id)
                .build();
        return uri.toString();
    }
}
