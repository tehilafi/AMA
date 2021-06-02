package com.tehilafi.ama.lists;

public class ListView_item_my {

    int Image;
    String UserName;
    String Date;
    String Location;
    String Anew;
    int With_ans;



    public ListView_item_my(int image, String userName, String date, String location, String anew, int with_ans){
        Image = image;
        UserName = userName;
        Date = date;
        Location = location;
        Anew = anew;
        With_ans = with_ans;

    }


    public String getDate() {
        return Date;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getLocation() {
        return Location;
    }

    public String getAnew() {
        return Anew;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setAnew(String anew) {
        Anew = anew;
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
