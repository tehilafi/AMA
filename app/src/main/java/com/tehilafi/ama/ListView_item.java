package com.tehilafi.ama;

public class ListView_item {

    int Image;
    String UserName;
    String Date;
    String Location;

    public ListView_item(int image,  String userName, String date, String location){
        Image = image;
        UserName = userName;
        Date = date;
        Location = location;
    }

    public int getImage() {
        return Image;
    }

    public String getDate() {
        return Date;
    }

    public String getUserName() {
        return UserName;
    }

    public String getLocation() {
        return Location;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setImage(int image) {
        Image = image;
    }

    public void setDate(String date) {
        Date = date;
    }

}
