package com.romanovich.chat.Adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.romanovich.chat.Models.Message;
import com.romanovich.chat.R;

import java.util.List;

public class MyMessAdapter extends RecyclerView.Adapter<MyMessAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    private List<Message> messages;
    private List<Message> idSender;
    private String[] IdReceiver;
    private String[] image;
    private LayoutInflater inflater;
    private Context context;
    private String receiver;

    public MyMessAdapter(Context context, List<Message> messages, List<Message> idSender, String[] IdReceiver, String receiver, String[] image) {
        this.receiver = receiver;
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.IdReceiver = IdReceiver;
        this.idSender = idSender;
        this.image=image;

    }
    @Override
    public MyMessAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==MSG_TYPE_LEFT){
            View view = inflater.inflate(R.layout.my_message_item, parent, false);
            ViewHolder v = new ViewHolder(view);
            return v;
            }
            else{
                View view1 = inflater.inflate(R.layout.receiver_message_item, parent, false);
                ViewHolder v1 = new ViewHolder(view1);
                return v1;
            }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.textView_message.setText(message.getText());
        Bitmap bitmap=StringToImage(image[position]);
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView_message;
        ImageView imageView;
        ViewHolder(View view){
            super(view);
            textView_message = (TextView)view.findViewById(R.id.textView_message);
            imageView = (ImageView)view.findViewById(R.id.sender_image);
        }
    }
    @Override
    public int getItemViewType(int position){
        if(IdReceiver[position].equals(receiver)){
            return MSG_TYPE_LEFT;
        }else return MSG_TYPE_RIGHT;


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