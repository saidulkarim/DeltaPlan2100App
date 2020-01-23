package com.cegis.deltaplan2100.models;

public class ModelSendFeedback {
    public String user_name;
    public String phone_no;
    public String user_email;
    public String user_comment;

    public ModelSendFeedback(String name, String phone, String email, String comment) {
        this.user_name = name;
        this.phone_no = phone;
        this.user_email = email;
        this.user_comment = comment;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getPhoneNo() {
        return phone_no;
    }

    public void setPhoneNo(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getEmail() {
        return user_email;
    }

    public void setEmail(String user_email) {
        this.user_email = user_email;
    }

    public String getComment() {
        return user_comment;
    }

    public void setComment(String user_comment) {
        this.user_comment = user_comment;
    }

}
