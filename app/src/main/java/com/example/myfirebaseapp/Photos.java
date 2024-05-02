package com.example.myfirebaseapp;

public class Photos {
    private String postid,postedby,postedon,postdetails,postimage;

    public Photos(String postid, String postedby, String postedon, String postdetails, String postimage) {
        this.postid = postid;
        this.postedby = postedby;
        this.postedon = postedon;
        this.postdetails = postdetails;
        this.postimage = postimage;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostedby() {
        return postedby;
    }

    public void setPostedby(String postedby) {
        this.postedby = postedby;
    }

    public String getPostedon() {
        return postedon;
    }

    public void setPostedon(String postedon) {
        this.postedon = postedon;
    }

    public String getPostdetails() {
        return postdetails;
    }

    public void setPostdetails(String postdetails) {
        this.postdetails = postdetails;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public Photos()
    {

    }




}
