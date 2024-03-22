package com.example.myfirebaseapp;

public class ReadWriteUserDetails  {
    public String firstname,lastname,dob,gender,mobile;

    public ReadWriteUserDetails(){}

    public ReadWriteUserDetails( String textFirstName, String textLastName, String textDOB, String textGender, String textMobile) {

        this.firstname = textFirstName;
        this.lastname = textLastName;
        this.dob = textDOB;
        this.gender = textGender;
        this.mobile = textMobile;
    }
}
