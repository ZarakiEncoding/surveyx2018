package es.academy.solidgear.surveyx.services.requests;

import com.android.volley.Response;

import org.json.JSONObject;

import es.academy.solidgear.surveyx.services.requestparams.SurveyPostParams;

public class SendResponseRequest extends BaseJSONRequest<SurveyPostParams, JSONObject> {

    private static final String PATH = "responses/";

    public SendResponseRequest(int id, SurveyPostParams surveyPostParams, Response.Listener<JSONObject> listener,
                               Response.ErrorListener errorListener) {
        super(Method.POST, PATH, JSONObject.class, null,
                surveyPostParams, listener, errorListener);
    }
}
