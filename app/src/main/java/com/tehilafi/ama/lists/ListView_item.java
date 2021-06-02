package com.tehilafi.ama.lists;

public class ListView_item {

    int Image;
    String UserName;
    String Date;
    String Question;
    int With_ans;

    public ListView_item(int image,  String userName, String date, String question, int with_ans){
        Image = image;
        UserName = userName;
        Date = date;
        Question = question;
        With_ans = with_ans;
    }

    public String getDate() {
        return Date;
    }

    public String getUserName() {
        return UserName;
    }

    public String getQuestion() {
        return Question;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getImage() {
        return Image;
    }

    public int getWith_ans() {
        return With_ans;
    }

    public void setImage(int image) {
        Image = image;
    }

    public void setWith_ans(int with_ans) {
        With_ans = with_ans;
    }
}
