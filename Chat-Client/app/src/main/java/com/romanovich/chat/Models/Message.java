package com.romanovich.chat.Models;

import java.util.Date;

public class Message {
    public int id;
    public int IdSender;
    public int IdReceiver;
    public String Text;
    public String Image;
    public Date Date;

    public Message(String Text){
        this.Text=Text;

    }

    public Message(){

    }
    public int getIdReceiver() {
        return IdReceiver;
    }

    public int getIdSender() {
        return IdSender;
    }

    public String getImage() {
        return Image;
    }

    public String getText() {
        return Text;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


}
