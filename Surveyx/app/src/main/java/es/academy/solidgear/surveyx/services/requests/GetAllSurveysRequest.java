package es.academy.solidgear.surveyx.services.requests;

import android.net.Uri;

import com.android.volley.Response;

import es.academy.solidgear.surveyx.model.SurveysModel;
import es.academy.solidgear.surveyx.services.requestparams.AllSurveysParams;

/**
 * Created by idiaz on 11/12/2014.
 */
public class GetAllSurveysRequest extends BaseJSONRequest<AllSurveysParams, SurveysModel> {

    private static final String PATH = "surveys";
    private static final String TOKEN_QUERY_PARAM = "token";

    public GetAllSurveysRequest(String token,
                                Response.Listener<SurveysModel> listener,
                                Response.ErrorListener errorListener) {
        super(Method.GET, buildURL(token), SurveysModel.class, null,
                null, listener, errorListener);
    }

    private static String buildURL(String token) {
        Uri uri = new Uri.Builder()
                .path(PATH)
                .appendQueryParameter(TOKEN_QUERY_PARAM, token)
                .build();
        return uri.toString();
    }
}
