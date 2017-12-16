package es.academy.solidgear.surveyx.services.requests;

/**
 * Created by Siro on 10/12/2014.
 */

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;

import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import es.academy.solidgear.surveyx.BuildConfig;

/**
 * Base class to create requests
 */
public abstract class BaseJSONRequest<T, U> extends Request<U> {
    private Gson mGson = new Gson();
    private Class<U> mClazz;
    private T mParams;
    private Map<String, String> mHeaders;
    private Response.Listener<U> mListener;

    private String HEADER_KEY_TOKEN = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String UTF_8 = "UTF-8";

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param method URL of the request to make
     * @param wsMethodUrl url to the rest method
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     * @param params Class object with request params
     */
    public BaseJSONRequest(int method, String wsMethodUrl, Class<U> clazz, Map<String, String> headers,
                           T params, Response.Listener<U> listener, Response.ErrorListener errorListener) {
        super(method, BuildConfig.WS_URL + wsMethodUrl, errorListener);
        mClazz = clazz;
        mParams = params;
        mHeaders = headers;
        mListener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = mHeaders != null ? mHeaders : new HashMap<String, String>();

        return headers;
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody() {
        if (mParams == null) {
            return null;
        }

        try {
            return mGson.toJson(mParams).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void deliverResponse(U response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<U> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    customParseCharset(response.headers));

            return Response.success(
                    mGson.fromJson(json, mClazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * Returns the charset specified in the Content-Type of this header, or the
     * UTF-8 if none can be found.
     */
    public static String customParseCharset(Map<String, String> headers) {
        String contentType = headers.get(CONTENT_TYPE);
        if (contentType != null) {
            String[] params = contentType.split(";");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }
        return UTF_8;
    }
}
