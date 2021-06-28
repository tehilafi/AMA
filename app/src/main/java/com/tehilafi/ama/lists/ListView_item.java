package com.tehilafi.ama.lists;

public class ListView_item{

    String Image;
    String UserName;
    String Date;
    String NumQ;
    String Question;
    int With_ans;
    int Star;

    public ListView_item(String image, String userName, String date, String numQ, String question, int with_ans, int star){
        Image = image;
        UserName = userName;
        Date = date;
        NumQ = numQ;
        Question = question;
        With_ans = with_ans;
        Star = star;
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

    public String getImage() {
        return Image;
    }

    public int getWith_ans() {
        return With_ans;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setWith_ans(int with_ans) {
        With_ans = with_ans;
    }

    public int getStar() {
        return Star;
    }
    
    public void setStar(int star) {
        Star = star;
    }

    public String getNumQ() {
        return NumQ;
    }

    public void setNumQ(String numQ) {
        NumQ = numQ;
    }
}
