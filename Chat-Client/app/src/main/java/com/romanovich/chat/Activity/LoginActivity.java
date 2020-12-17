package com.romanovich.chat.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.romanovich.chat.Encryption.Encryption;
import com.romanovich.chat.R;
import com.romanovich.chat.Server.AccessTokenClass;
import com.romanovich.chat.Server.CustomError;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.Cipher;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
      private static final String BASE_URL = "https://85fc5bfe93e9.ngrok.io/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        TextView back_to_registration_text_view = (TextView) findViewById(R.id.back_to_registration_text_view);
        back_to_registration_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RegisterActivity","Try show");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    public void loginClick(View view) {
        try {
            OkHttpClient client = new OkHttpClient();
            EditText loginTextField = findViewById(R.id.email_edittext_login);
            EditText passwordTextField = findViewById(R.id.password_edittext_login);
            String loginText = loginTextField.getText().toString();
            String passwordText = passwordTextField.getText().toString();
            String encryptPass =  Encryption.encrypt(passwordText);
            RequestBody formBody = new FormBody.Builder()
                    .add("username", loginText)
                    .add("password", encryptPass)
                    .add("grant_type", "password")
                    .build();

            final Request request = new Request.Builder()
                    .url(BASE_URL + "token")
                    .post(formBody)
                    .build();

            Call call = client.newCall(request);
            Response response = call.execute();

            Gson g = new Gson();
            if(response.code() == 200) {
                AccessTokenClass object = g.fromJson(response.body().string(), AccessTokenClass.class);
                String ACCESS_TOKEN;
                ACCESS_TOKEN = object.access_token;
                Intent intent = new Intent(this, UserActivity.class);
                intent.putExtra("ACCESS_TOKEN", ACCESS_TOKEN);
                startActivity(intent);
            }
            else if(response.code() == 400){
                CustomError object = g.fromJson(response.body().string(), CustomError.class);
                Toast.makeText(this, object.error_description, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}

