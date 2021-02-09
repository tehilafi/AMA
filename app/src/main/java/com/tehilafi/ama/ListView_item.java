package com.tehilafi.ama;

public class ListView_item {

    int Image;
    String Date;
    String Des;

    public ListView_item(int image, String date, String des){
        Image = image;
        Date = date;
        Des = des;
    }

    public int getImage() {
        return Image;
    }

    public String getDate() {
        return Date;
    }

    public String getDes() {
        return Des;
    }

    public void setImage(int image) {
        Image = image;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setDes(String des) {
        Des = des;
    }
}
