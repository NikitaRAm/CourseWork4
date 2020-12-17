package com.romanovich.chat.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.romanovich.chat.Adapter.MyMessAdapter;
import com.romanovich.chat.Models.Message;
import com.romanovich.chat.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivityLog extends AppCompatActivity {
    private RecyclerView.LayoutManager layoutManager;
    private static final String BASE_URL = "https://85fc5bfe93e9.ngrok.io/";
    private final int Pick_image = 1;
    public Bitmap bitmap;
    MyMessAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_log);
        Intent intent = getIntent();
        String friend = intent.getStringExtra("strText");
        String receiver = intent.getStringExtra("idReceiver");
        Button btn_img = (Button) findViewById(R.id.btn_img);

        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Pick_image);{
                }
            }
        });
        Toast.makeText(this, friend, Toast.LENGTH_SHORT).show();

        String ACCESS_TOKEN = intent.getStringExtra("ACCESS_TOKEN");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {

            OkHttpClient client = new OkHttpClient();

            final Request request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                    .url(BASE_URL + "api/message/GetMyMessage?fr=" + friend)
                    .get()
                    .build();

            Call call = client.newCall(request);

            Response response = null;

            try {
                response = call.execute();
                if(response.code() != 200)
                {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Gson g = gsonBuilder.create();
            String a = null;
            a = response.body().string();
            a = validate(a);

            List<Message> b = g.fromJson(a, new TypeToken<List<Message>>(){}.getType());
            for (Message i:b) {
                i.getText();
            }
            List<Message> idSender = g.fromJson(a, new TypeToken<List<Message>>(){}.getType());
            for (Message i:b) {
                i.getIdSender();
            }
            Message[] b1 = g.fromJson(a, Message[].class);
            String[] idReceiver = new String[b1.length];
            for (int i = 0; i < b1.length; i++) {
                idReceiver[i] = String.valueOf(b1[i].getIdReceiver());
            }
            Message[] b2 = g.fromJson(a, Message[].class);
            String[] image = new String[b2.length];
            for (int i = 0; i < b2.length; i++) {
                image[i] =b2[i].getImage();
            }
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_messages);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            adapter = new  MyMessAdapter(this, b,idSender,idReceiver,receiver,image);

            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Button btn_img = (Button) findViewById(R.id.btn_img);
        ImageView send_image=(ImageView) findViewById(R.id.send_image);
        switch(requestCode) {
            case Pick_image:
                if(resultCode == RESULT_OK){
                    try {
                        Uri path = imageReturnedIntent.getData();

                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                        //setting the bitmap on the image view
                        send_image.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }

    }
    public void SendMessage(View view) throws IOException {

        Intent intent = getIntent();
        EditText sendmessage = findViewById(R.id.sendmessage_edittext_chat);
        ImageView send_image=(ImageView) findViewById(R.id.send_image);
        String receiver = intent.getStringExtra("strText");
        String ACCESS_TOKEN = intent.getStringExtra("ACCESS_TOKEN");
        String message=sendmessage.getText().toString();
        String image;
        try{
            image=ImageToString();
        }catch (Exception e){
            image ="";
        }
        if(message.equals("")){
            message= "image";
        }
        OkHttpClient client = new OkHttpClient();
        image = image.replaceAll("\n", "").replaceAll("\r", "");

        RequestBody formBody = new FormBody.Builder()
                .add("reciever",receiver)
                .add("text", message)
                .add("image",image)
                .build();
        final Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                .url(BASE_URL + "api/message/sendMessage")
                .post(formBody)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        String error = response.toString();

        if(response.code() == 200)
        {
            sendmessage.setText("");
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.back);
            send_image.setImageBitmap(bitmap);
        }
        else
        {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }
    public void ViewMessage(View view)throws IOException {
        Intent intent = getIntent();
        String friend = intent.getStringExtra("strText");
        Toast.makeText(this, friend, Toast.LENGTH_SHORT).show();

        String ACCESS_TOKEN = intent.getStringExtra("ACCESS_TOKEN");
        String receiver = intent.getStringExtra("idReceiver");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        try {

            OkHttpClient client = new OkHttpClient();

            final Request request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                    .url(BASE_URL + "api/message/GetMyMessage?fr=" + friend)
                    .get()
                    .build();

            Call call = client.newCall(request);

            Response response = null;

            try {
                response = call.execute();
                if(response.code() != 200)
                {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Gson g = gsonBuilder.create();
            String a = null;
            a = response.body().string();
            a = validate(a);

            List<Message> b = g.fromJson(a, new TypeToken<List<Message>>(){}.getType());
            for (Message i:b) {
                i.getText();
            }
            List<Message> idSender = g.fromJson(a, new TypeToken<List<Message>>(){}.getType());
            for (Message i:b) {
                i.getIdSender();
            }
            Message[] b1 = g.fromJson(a, Message[].class);
            String[] idReceiver = new String[b1.length];
            for (int i = 0; i < b1.length; i++) {
                idReceiver[i] = String.valueOf(b1[i].getIdReceiver());
            }
            Message[] b2 = g.fromJson(a, Message[].class);
            String[] image = new String[b2.length];
            for (int i = 0; i < b2.length; i++) {
                image[i] =b2[i].getImage();
            }
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_messages);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            adapter = new  MyMessAdapter(this, b,idSender,idReceiver,receiver,image);

            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public String validate(String s) {
        final char dm = (char) 34;
        String b = s.substring(1, s.length() - 1);
        b = b.replaceAll("\\\\", "");
        //Log.d("USERSACTIVITY",b);
        return b;
    }
    private String ImageToString(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imagebyte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imagebyte,Base64.DEFAULT);
    }
}