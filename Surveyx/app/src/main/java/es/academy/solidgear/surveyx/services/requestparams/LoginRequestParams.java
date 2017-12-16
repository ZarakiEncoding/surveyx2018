package es.academy.solidgear.surveyx.services.requestparams;

/**
 * Created by Siro on 10/12/2014.
 */
public class LoginRequestParams {
    private String username;
    private String password;

    public LoginRequestParams(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
