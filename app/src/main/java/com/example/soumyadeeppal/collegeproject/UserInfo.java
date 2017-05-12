package com.example.soumyadeeppal.collegeproject;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Soumyadeep Pal on 23-01-2017.
 */

public class UserInfo implements Serializable {

    public String phNo;
    public String name;
    public String pic_path;



    public String getPhNo() {
        return phNo;
    }

    public void setPhNo(String phNo) {
        this.phNo = phNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic_path() {
        return pic_path;
    }

    public void setPic_path(String pic_path) {
        this.pic_path = pic_path;
    }



}
