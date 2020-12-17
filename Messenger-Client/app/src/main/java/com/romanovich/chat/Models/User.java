package com.romanovich.chat.Models;

import java.util.Collection;

public class User {
    private String UserName;
    private String Password;
    private String Email;
    private int isAdmin;
    private String Image;
    private int id;

   public User(String UserName, String Email){
       this.UserName=UserName;
       this.Email= Email;
   }
   public  User(){

   }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getEmail() {
        return Email;
    }
    public String getUserName() {
        return UserName;
    }

    public String getImage() {
        return Image;
    }


}

