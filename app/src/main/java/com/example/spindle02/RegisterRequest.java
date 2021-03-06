package com.example.spindle02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    final static private String URL = "http://13.59.96.134/userRegister.php";
    private Map<String, String> parameters;

    public RegisterRequest(String userID, String userPassword, String userName, String userPhone,
                           String userEmail, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);//해당 URL에 POST방식으로 파마미터들을 전송함
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
        parameters.put("userName", userName);
        parameters.put("userPhone", userPhone);
        parameters.put("userEmail", userEmail);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }

}
