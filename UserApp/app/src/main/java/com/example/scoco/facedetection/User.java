package com.example.scoco.facedetection;

public class User {
    public String id;
    public String firstName;
    public String lastName;
    public String contact;
    public String email;

    User(String id, String firstName, String lastName, String contact, String email_){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contact = contact;
        this.email = email_;
    }

    User(){

    }

    @Override
    public String toString() {
        return (id + "\n" + lastName +"\n"+ firstName +"\n"+contact);
    }
}