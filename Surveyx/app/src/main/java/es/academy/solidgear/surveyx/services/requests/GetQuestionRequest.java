package es.academy.solidgear.surveyx.services.requests;

import android.net.Uri;

import com.android.volley.Response;

import es.academy.solidgear.surveyx.model.QuestionModel;
import es.academy.solidgear.surveyx.services.requestparams.LoginRequestParams;

/**
 * Created by Siro on 03/02/2015.
 */
public class GetQuestionRequest extends BaseJSONRequest<LoginRequestParams, QuestionModel> {

    private static final String PATH = "questions/";

    public GetQuestionRequest(int id, Response.Listener<QuestionModel> listener,
                              Response.ErrorListener errorListener) {
        super(Method.GET, buildURL(id), QuestionModel.class, null,
                null, listener, errorListener);
    }

    private static String buildURL(int id) {
        Uri uri = new Uri.Builder()
                .path(PATH + id)
                .build();
        return uri.toString();
    }
}
