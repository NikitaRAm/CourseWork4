package com.romanovich.chat.Adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.romanovich.chat.Activity.ChatActivityLog;
import com.romanovich.chat.Encryption.Encryption;
import com.romanovich.chat.R;


public class MyUserAdapter extends RecyclerView.Adapter<MyUserAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private String[] users;
    private String[] user_email;
    private String[] id;
    private String[] image;
    private Context context;
    private String ACCESS_TOKEN;

    public MyUserAdapter(Context context, String[] users, String[] user_email, String ACCESS_TOKEN, String[] id, String[] image) {
        this.users = users;
        this.user_email=user_email;
        this.image=image;
        this.id = id;
        this.inflater = LayoutInflater.from(context);
        this.context =context;
        this.ACCESS_TOKEN=ACCESS_TOKEN;
    }
    @Override
    public MyUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        holder.nameView.setText(users[position]);
        holder.emailView.setText(user_email[position]);
        holder.idView.setText(id[position]);
        Bitmap bitmap=StringToImage(image[position]);
        holder.imageView.setImageBitmap(bitmap);
    }


    @Override
    public int getItemCount() {
        return users.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
        final TextView nameView,emailView,idView;
        ViewHolder(View view){
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image_user);
            nameView =  (TextView)view.findViewById(R.id.name);
            emailView = (TextView)view.findViewById(R.id.email);
            idView = (TextView)view.findViewById(R.id.id);

            view.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    String strText = nameView.getText().toString();
                    String idReceiver = idView.getText().toString();
                    Intent intent = new Intent(context, ChatActivityLog.class);
                    intent.putExtra("strText", strText);
                    intent.putExtra("idReceiver",idReceiver);
                    intent.putExtra("ACCESS_TOKEN", ACCESS_TOKEN);
                    context.startActivity(intent);
                }
            });


        }
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