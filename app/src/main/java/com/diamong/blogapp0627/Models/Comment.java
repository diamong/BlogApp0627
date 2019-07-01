package com.diamong.blogapp0627.Models;

import com.google.firebase.database.ServerValue;

public class Comment {

    private String content,uid,uimg,uname;
    private Object timestamep;

    public Comment() {
    }

    public Comment(String content, String uid, String uimg, String uname) {
        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;

        this.timestamep= ServerValue.TIMESTAMP;
    }

    public Comment(String content, String uid, String uimg, String uname, Object timestamep) {
        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.timestamep = timestamep;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUimg() {
        return uimg;
    }

    public void setUimg(String uimg) {
        this.uimg = uimg;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public Object getTimestamep() {
        return timestamep;
    }

    public void setTimestamep(Object timestamep) {
        this.timestamep = timestamep;
    }
}
