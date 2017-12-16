package es.academy.solidgear.surveyx.model;

/**
 * Created by Siro on 10/12/2014.
 */
public class LoginModel {
    private LoginData results[];

    public LoginModel(String token) {
        this.results[0].setToken(token);
    }

    public String getToken() {
        return results[0].getToken();
    }

}


class LoginData {
    private String token;
    private String username;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
