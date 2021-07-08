package com.tehilafi.ama.db;


import com.tehilafi.ama.R;

public class Users {
    private String userName;
    private String password;
    private String phone;
    private int score;
    private int importantQuestions;
    private int id;
    private String token;
    private Double latitude;
    private Double longitude;
    private int numAnswer;
    private int numlike;
    private int numPicture;
    private int numVideo;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getImportantQuestions() {
        return importantQuestions;
    }

    public void setImportantQuestions(int importantQuestions) {
        this.importantQuestions = importantQuestions;
    }

    public int getNumAnswer() {
        return numAnswer;
    }

    public int getNumLike() {
        return numlike;
    }

    public int getNumPicture() {
        return numPicture;
    }

    public int getNumVideo() {
        return numVideo;
    }

    public void setNumAnswer(int numAnswer) {
        this.numAnswer = numAnswer;
    }

    public void setNumLike(int like) {
        this.numlike = like;
    }

    public void setNumPicture(int numPicture) {
        this.numPicture = numPicture;
    }

    public void setNumVideo(int numVideo) {
        this.numVideo = numVideo;
    }

    public Users(){
    }

    public int StarRating() {
        int starR = R.drawable.star;
        if(getScore() < 150)
            starR = R.drawable.star1;
        if(getScore()  >= 150 && getScore() < 420)
            starR = R.drawable.star2;
        if(getScore() >= 420)
            starR = R.drawable.star3;
        return starR;
    }

}
