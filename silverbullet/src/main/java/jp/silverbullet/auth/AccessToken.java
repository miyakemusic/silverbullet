package jp.silverbullet.auth;


import java.io.Serializable;

public class AccessToken implements Serializable {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
