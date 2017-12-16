package es.academy.solidgear.surveyx.services.requests;

import android.net.Uri;

import com.android.volley.Response;

import es.academy.solidgear.surveyx.model.LoginModel;
import es.academy.solidgear.surveyx.services.requestparams.LoginRequestParams;

/**
 * Created by Siro on 10/12/2014.
 */
public class UserLoginRequest extends BaseJSONRequest<LoginRequestParams, LoginModel> {

    private static final String PATH = "users";
    private static final String USERNAME_QUERY_PARAM = "username";
    private static final String PASSWORD_QUERY_PARAM = "password";

    public UserLoginRequest(String username, String password, Response.Listener<LoginModel> listener,
                            Response.ErrorListener errorListener) {
        super(Method.GET, buildURL(username, password), LoginModel.class, null,
                null, listener, errorListener);

    }

    private static String buildURL(String username, String password) {
        Uri uri = new Uri.Builder()
                .path(PATH)
                .appendQueryParameter(USERNAME_QUERY_PARAM, username)
                .appendQueryParameter(PASSWORD_QUERY_PARAM, password)
                .build();
        return uri.toString();
    }
}
