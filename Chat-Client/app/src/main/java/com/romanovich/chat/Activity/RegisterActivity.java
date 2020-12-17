package com.romanovich.chat.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.romanovich.chat.Encryption.Encryption;
import com.romanovich.chat.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

public class RegisterActivity extends AppCompatActivity {
    private final int Pick_image = 1;
    private static final String BASE_URL = "https://85fc5bfe93e9.ngrok.io/";
    public static String ACCESS_TOKEN;
    public Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_register);

        TextView already_have_an_account_text_view = (TextView) findViewById(R.id.already_have_an_account_text_view);
        Button selectphoto_button_register = (Button) findViewById(R.id.selectphoto_button_register);

        already_have_an_account_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RegisterActivity","Try show");
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        selectphoto_button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RegisterActivity","Try show");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Pick_image);{
            }
        }
        });


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Button selectphoto_button_register = (Button) findViewById(R.id.selectphoto_button_register);
        ImageView selectphoto_image_register=(ImageView) findViewById(R.id.selectphoto_image_register);
        Button register_button_register = findViewById(R.id.register_button_register);
        switch(requestCode) {
            case Pick_image:
                if(resultCode == RESULT_OK){
                    try {
                        Uri path = imageReturnedIntent.getData();

                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                        //setting the bitmap on the image view
                        selectphoto_image_register.setImageBitmap(bitmap);
                        selectphoto_button_register.setBackgroundColor(android.R.color.transparent);
                        selectphoto_button_register.setText("");
                        register_button_register.setEnabled(true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }

    }

    public void registerClick(View view) throws IOException {

        EditText loginTextField = findViewById(R.id.username_edittext_register);
        EditText emailTextField = findViewById(R.id.email_edittext_register);
        EditText passwordTextField = findViewById(R.id.password_edittext_register);

        String username = loginTextField.getText().toString();
        String email = emailTextField.getText().toString();
        String pass = passwordTextField.getText().toString();

        String image=ImageToString();

        try {
            String encryptPass =  Encryption.encrypt(pass);

        OkHttpClient client = new OkHttpClient();
        image = image .replaceAll("\n", "").replaceAll("\r", "");
        RequestBody formBody = new FormBody.Builder()
                .add("UserName", username)
                .add("Email", email)
                .add("Password", encryptPass )
                .add("isAdmin", "0") //ноль если не админ, 1 если админ
                .add("Image", image) //сюда передать картинку в двоичном формате
                .build();

        final Request request = new Request.Builder()
                .url(BASE_URL + "api/user/CreateUser")
                .post(formBody)
                .build();

        Call call = client.newCall(request);

        Response response = call.execute();

        if(response.code() == 200)
        {
            loginTextField.setText("");
            emailTextField.setText("");
            passwordTextField.setText("");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else if(response.code() == 301){
            Toast.makeText(this, "This user already exists!", Toast.LENGTH_SHORT).show();
            loginTextField.setText("");
            emailTextField.setText("");
            passwordTextField.setText("");
        }
        else if(response.code() == 500)
        {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
            loginTextField.setText("");
            emailTextField.setText("");
            passwordTextField.setText("");
        }else{
            Toast.makeText(this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
            loginTextField.setText("");
            emailTextField.setText("");
            passwordTextField.setText("");
        }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String ImageToString(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imagebyte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imagebyte,Base64.DEFAULT);
    }

}
