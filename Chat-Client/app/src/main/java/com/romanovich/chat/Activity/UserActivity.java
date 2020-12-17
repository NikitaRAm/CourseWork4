package com.romanovich.chat.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.romanovich.chat.Adapter.MyUserAdapter;
import com.romanovich.chat.Models.User;
import com.romanovich.chat.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class UserActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://85fc5bfe93e9.ngrok.io/";
    private RecyclerView.LayoutManager layoutManager;
    List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = getIntent();
        String ACCESS_TOKEN = intent.getStringExtra("ACCESS_TOKEN");

        try {

            OkHttpClient client = new OkHttpClient();

            final Request request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                    .url(BASE_URL + "api/user/GetUsers")
                    .get()
                    .build();

            Call call = client.newCall(request);

            Response response = null;

            try {
                response = call.execute();

            } catch (IOException e) {
                e.printStackTrace();
            }

            Gson gson = new GsonBuilder().create();
            String a = null;
            a = response.body().string();
            a = validate(a);

            User[] b = gson.fromJson(a, User[].class);
            String[] namesOfEvents = new String[b.length];
            for (int i = 0; i < b.length; i++) {
                namesOfEvents[i] = b[i].getUserName() ;
            }
            String[] user_email = new String[b.length];
            for (int i = 0; i < b.length; i++) {
                user_email[i] = b[i].getEmail() ;
            }
            String[] id = new String[b.length];
            for (int i = 0; i < b.length; i++) {
               id[i] = String.valueOf(b[i].getId());
            }
            String[] image = new String[b.length];
            for (int i = 0; i < b.length; i++) {
                image[i] =b[i].getImage();
            }

            String test = null;
            for (int i = 0; i < b.length; i++)
            {
                    test = b[i].getImage();
            }

            Bitmap bitmap = StringToImage(test);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            MyUserAdapter adapter = new MyUserAdapter(this, namesOfEvents,user_email,ACCESS_TOKEN,id,image);

            recyclerView.setAdapter(adapter);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String validate(String s) {
        final char dm = (char) 34;
        String b = s.substring(1, s.length() - 1);
        b = b.replaceAll("[\\n\\t ]", "");
        b = b.replaceAll("\\\\", "");

        return b;
    }
    private Bitmap StringToImage(String image){
        try{
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);

            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

}
